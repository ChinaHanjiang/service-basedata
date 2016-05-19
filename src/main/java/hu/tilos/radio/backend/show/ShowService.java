package hu.tilos.radio.backend.show;

import com.mongodb.*;
import hu.tilos.radio.backend.captcha.RecaptchaValidator;
import hu.tilos.radio.backend.contribution.ShowContribution;
import hu.tilos.radio.backend.converters.SchedulingTextUtil;
import hu.tilos.radio.backend.data.error.InternalErrorException;
import hu.tilos.radio.backend.data.error.NotFoundException;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.UrlData;
import hu.tilos.radio.backend.scheduling.SchedulingSimple;
import hu.tilos.radio.backend.util.AvatarLocator;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.stream.Collectors;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

@Service
public class ShowService {

    private static Logger LOG = LoggerFactory.getLogger(ShowService.class);

    private final SchedulingTextUtil schedulingTextUtil = new SchedulingTextUtil();


    @Inject
    AvatarLocator avatarLocator;

    @Inject
    private DozerBeanMapper mapper;

    @Inject

    private RecaptchaValidator captchaValidator;

    @Inject
    private DB db;

    @Inject
    private JavaMailSender mailSender;


    public List<ShowSimple> list(String status) {
        BasicDBObject criteria = new BasicDBObject();

        //FIXME
        if (!"all".equals(status)) {
            criteria.put("status", ShowStatus.ACTIVE.ordinal());
        }
        DBCursor selectedShows = db.getCollection("show").find(criteria).sort(new BasicDBObject("name", 1));

        List<ShowSimple> mappedShows = new ArrayList<>();
        for (DBObject show : selectedShows) {
            mappedShows.add(mapper.map(show, ShowSimple.class));
        }
        Collections.sort(mappedShows, new Comparator<ShowSimple>() {
            @Override
            public int compare(ShowSimple s1, ShowSimple s2) {
                return s1.getName().toLowerCase().compareTo(s2.getName().toLowerCase());
            }
        });
        return mappedShows;

    }

    private ShowStatus processStatus(String status) {
        if (status == null) {
            return ShowStatus.ACTIVE;
        } else if (status.equals("all")) {
            return null;
        } else {
            return ShowStatus.valueOf(status.toUpperCase());
        }
    }

    public ShowDetailed get(String alias) {
        DBObject one = db.getCollection("show").findOne(aliasOrId(alias));
        if (one == null) {
            throw new NotFoundException("No such show");
        }
        ShowDetailed detailed = mapper.map(one, ShowDetailed.class);


        Date now = new Date();
        for (SchedulingSimple ss : detailed.getSchedulings()) {
            if (ss.getValidFrom().compareTo(now) < 0 && ss.getValidTo().compareTo(now) > 0)
                ss.setText(schedulingTextUtil.create(ss));
        }
        if (detailed.getContributors() != null) {
            for (ShowContribution contributor : detailed.getContributors()) {
                if (contributor.getAuthor() != null) {
                    avatarLocator.locateAvatar(contributor.getAuthor());
                }
            }
        }
        long mixCount = db.getCollection("mix").count(new BasicDBObject("show.ref", new DBRef(db, "show", one.get("_id").toString())));
        detailed.getStats().mixCount = (int) mixCount;
        detailed.setUrls(processUrls(detailed.getUrls()));
        return detailed;

    }

    private List<UrlData> processUrls(List<UrlData> urls) {
        return urls.stream().map(url -> {
            if (url.getAddress().contains("facebook")) {
                url.setType("facebook");
                url.setLabel(url.getAddress().replaceAll("http(s?)://(www.?)facebook.com/", "facebook/"));
            } else if (url.getAddress().contains("mixcloud")) {
                url.setType("mixcloud");
                url.setLabel(url.getAddress().replaceAll("http(s?)://(www.?)mixcloud.com/", "mixcloud/"));
            } else {
                url.setType("url");
                url.setLabel(url.getAddress().replaceAll("http(s?)://", ""));
            }
            return url;
        }).collect(Collectors.toList());
    }


    public UpdateResponse update(String alias, ShowToSave showToSave) {
        DBObject show = findShow(alias);


        if (!show.get("alias").toString().equals(showToSave.getAlias())) {
            updateDenormalizedFields(show.get("alias").toString(), showToSave.getAlias());
        }

        mapper.map(showToSave, show);
        db.getCollection("show").update(aliasOrId(alias), show);
        return new UpdateResponse(true);

    }

    private void updateDenormalizedFields(String oldAlias, String newAlias) {
        db.getCollection("mix").update(
                new BasicDBObject("show.alias", oldAlias),
                new BasicDBObject("$set", new BasicDBObject("show.alias", newAlias)),
                false, true
        );
        db.getCollection("episode").update(
                new BasicDBObject("show.alias", oldAlias),
                new BasicDBObject("$set", new BasicDBObject("show.alias", newAlias)),
                false, true
        );
        db.getCollection("author").update(
                new BasicDBObject("contributions.show.alias", oldAlias),
                new BasicDBObject("$set", new BasicDBObject("contributions.$.show.alias", newAlias)),
                false, true
        );
    }

    private DBObject findShow(String alias) {
        return db.getCollection("show").findOne(aliasOrId(alias));
    }


    public CreateResponse create(ShowToSave objectToSave) {
        DBObject newObject = mapper.map(objectToSave, BasicDBObject.class);
        newObject.put("alias", objectToSave.getAlias());
        db.getCollection("show").insert(newObject);
        return new CreateResponse(((ObjectId) newObject.get("_id")).toHexString());
    }

    public OkResponse contact(String alias, MailToShow mailToSend) {
        if (!captchaValidator.validate("http://tilos.hu", mailToSend.getCaptchaChallenge(), mailToSend.getCaptchaResponse())) {
            throw new IllegalArgumentException("Rosszul megadott Captcha");
        }
        MimeMessage mail = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, false);

            String body = "----- Ez a levél a tilos.hu műsoroldaláról lett küldve-----\n" +
                    "\n" +
                    "A form kitöltője a " + mailToSend.getFrom() + " email-t adta meg válasz címnek, de ennek valódiságát nem ellenőriztük." +
                    "\n" +
                    "-------------------------------------" +
                    "\n" +
                    mailToSend.getBody();


            helper.setFrom("postas@tilos.hu");
            helper.setReplyTo(mailToSend.getFrom());
            helper.setSubject("[tilos.hu] " + mailToSend.getSubject());
            helper.setText(body);


            DBObject one = db.getCollection("show").findOne(aliasOrId(alias));
            if (one == null) {
                throw new IllegalArgumentException("No such show: " + alias);
            }
            ShowDetailed detailed = mapper.map(one, ShowDetailed.class);

            detailed.getContributors().forEach(contributor -> {
                DBObject dbAuthor = db.getCollection("author").findOne(aliasOrId(contributor.getAuthor().getId()));

                if (dbAuthor.get("email") != null) {
                    try {
                        helper.setTo((String) dbAuthor.get("email"));
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                    mailSender.send(mail);
                }
            });
        } catch (Exception e) {
            throw new InternalErrorException("Can't send the email message: " + e.getMessage(), e);
        }
        return new OkResponse("Üzenet elküldve.");
    }

    public void setDb(DB db) {
        this.db = db;
    }
}
