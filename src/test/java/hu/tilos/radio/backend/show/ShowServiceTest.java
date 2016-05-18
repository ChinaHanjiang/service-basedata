package hu.tilos.radio.backend.show;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.GuiceRunner;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.input.UrlToSave;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.user.UserInfo;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadFrom;
import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ShowServiceTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmm");

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);

    @Inject
    ShowService controller;

    @Inject
    Session session;

    @Inject
    FongoRule fongoRule;

    @Inject
    DozerBeanMapper mapper;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void testGet() throws Exception {
        //given
        String authorId = loadTo(fongoRule, "author", "author-author2.json");
        String showId = loadTo(fongoRule, "show", "show-3utas2.json", authorId);

        //when
        ShowDetailed show = controller.get("3utas");

        //then
        Assert.assertEquals("3utas", show.getAlias());
        Assert.assertEquals("3. utas", show.getName());

        Assert.assertEquals(1, show.getContributors().size());

        Assert.assertEquals("AUTHOR2", show.getContributors().get(0).getNick());
        Assert.assertEquals(authorId, show.getContributors().get(0).getAuthor().getId());

        Assert.assertEquals(1, show.getSchedulings().size());

        Assert.assertEquals(0, show.getStats().mixCount);

        Assert.assertEquals(2, show.getUrls().size());
        Assert.assertEquals("facebook", show.getUrls().get(0).getType());
        Assert.assertEquals("facebook/3.Utas", show.getUrls().get(0).getLabel());

    }

    @Test
    public void testGetWithId() throws Exception {
        //given
        loadTo(fongoRule, "show", "show-3utas.json");

        //when
        ShowDetailed show = controller.get("1");

        //then
        Assert.assertEquals("3utas", show.getAlias());
        Assert.assertEquals("3. utas", show.getName());

        Assert.assertEquals(1, show.getSchedulings().size());

    }

    @Test
    public void testMapping() {
        //given
        ShowToSave save = new ShowToSave();
        save.setName("showname");
        save.setType(ShowType.SPEECH);

        //when
        BasicDBObject mapped = mapper.map(save, BasicDBObject.class);

        //then
        Assert.assertEquals("showname", mapped.get("name"));
        Assert.assertEquals(1, mapped.get("type"));
    }


    @Test
    public void list() throws Exception {
        //given
        controller.setDb(fongoRule.getDB());
        loadTo(fongoRule, "show", "show-3utas.json");
        loadTo(fongoRule, "show", "show-vendeglo.json");


        //when
        List<ShowSimple> showSimples = controller.list(null);

        //then
        assertThat(showSimples.size(), equalTo(2));

    }
//
//    @Test
//    public void update() throws Exception {
//        //given
//        String showId = loadTo(fongoRule, "show", "show-update-original.json");
//
//        UserInfo detailed = new UserInfo();
//        detailed.setRole(Role.ADMIN);
//        session.setCurrentUser(detailed);
//
//        ShowToSave showToSave = new ShowToSave();
//        showToSave.setType(ShowType.MUSIC);
//        showToSave.setStatus(ShowStatus.ACTIVE);
//        showToSave.setName("test");
//        showToSave.setAlias("alias");
//        UrlToSave url = new UrlToSave();
//        url.setAddress("http://pipacs.com");
//        showToSave.getUrls().add(url);
//
//        //when
//        UpdateResponse update = controller.update("3utas", showToSave);
//
//        //then
//        DBObject user = fongoRule.getDB().getCollection("show").findOne();
//        String expectedUser = loadFrom("show-update-expected.json", showId);
//        System.out.println(JSON.serialize(user));
//        JSONAssert.assertEquals(expectedUser, JSON.serialize(user), false);
//    }


//    @Test
//    public void updateWithAliasChange() throws Exception {
//        //given
//        String showId = loadTo(fongoRule, "show", "show-update-original.json");
//        String mix1Id = loadTo(fongoRule, "mix", "mix-1.json", showId);
//        String mix2Id = loadTo(fongoRule, "mix", "mix-2.json", showId);
//        String episodeId = loadTo(fongoRule, "episode", "episode-episode1.json", showId);
//
//        UserInfo detailed = new UserInfo();
//        detailed.setRole(Role.ADMIN);
//        session.setCurrentUser(detailed);
//
//        ShowToSave showToSave = new ShowToSave();
//        showToSave.setType(ShowType.MUSIC);
//        showToSave.setStatus(ShowStatus.ACTIVE);
//        showToSave.setName("test");
//        showToSave.setAlias("alias");
//        UrlToSave url = new UrlToSave();
//        url.setAddress("http://pipacs.com");
//        showToSave.getUrls().add(url);
//
//        //when
//        UpdateResponse update = controller.update("3utas", showToSave);
//
//        //then
//        DBObject show = fongoRule.getDB().getCollection("show").findOne(new BasicDBObject("_id", new ObjectId(showId)));
//        Assert.assertEquals("alias", show.get("alias"));
//
//        DBObject mix = fongoRule.getDB().getCollection("mix").findOne(new BasicDBObject("_id", new ObjectId(mix1Id)));
//        Assert.assertEquals("alias", ((DBObject) mix.get("show")).get("alias"));
//
//        mix = fongoRule.getDB().getCollection("mix").findOne(new BasicDBObject("_id", new ObjectId(mix2Id)));
//        Assert.assertEquals("alias", ((DBObject) mix.get("show")).get("alias"));
//
//        DBObject episode = fongoRule.getDB().getCollection("episode").findOne(new BasicDBObject("_id", new ObjectId(episodeId)));
//        Assert.assertEquals("alias", ((DBObject) episode.get("show")).get("alias"));
//    }


    @Test
    public void listAll() throws Exception {
        //given
        loadTo(fongoRule, "show", "show-3utas.json");
        loadTo(fongoRule, "show", "show-vendeglo.json");

        //when
        List<ShowSimple> showSimples = controller.list("all");

        //then
        assertThat(showSimples.size(), equalTo(2));

    }
}