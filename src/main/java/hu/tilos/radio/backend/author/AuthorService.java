package hu.tilos.radio.backend.author;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.auth.Role;
import hu.tilos.radio.backend.auth.Session;
import hu.tilos.radio.backend.data.error.NotFoundException;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.util.AvatarLocator;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

@Service
public class AuthorService {

    private static Logger LOG = LoggerFactory.getLogger(AuthorService.class);

    @Inject
    private DozerBeanMapper mapper;

    @Inject
    private DB db;

    @Inject
    AvatarLocator avatarLocator;

    public List<AuthorListElement> list() {

        DBCursor selectedAuthors = db.getCollection("author").find();

        List<AuthorListElement> mappedAuthors = new ArrayList<>();
        for (DBObject author : selectedAuthors) {
            mappedAuthors.add(mapper.map(author, AuthorListElement.class));
        }

        return mappedAuthors;

    }


    public AuthorDetailed get(String alias, Session session) {
        DBObject one = findAuthor(alias);
        if (one == null) {
            throw new NotFoundException("No such show");
        }
        AuthorDetailed author = mapper.map(one, AuthorDetailed.class);
        avatarLocator.locateAvatar(author);
        if (session != null && session.getCurrentUser() != null && (session.getCurrentUser().getRole() == Role.ADMIN || session.getCurrentUser().getRole() == Role.AUTHOR)) {
            author.setEmail((String) one.get("email"));
        }
        return author;

    }

    private DBObject findAuthor(String alias) {
        BasicDBObject query = aliasOrId(alias);
        return db.getCollection("author").findOne(query);
    }


    public UpdateResponse update(String alias, AuthorToSave authorToSave) {
        DBObject author = findAuthor(alias);
        mapper.map(authorToSave, author);
        db.getCollection("author").update(aliasOrId(alias), author);
        return new UpdateResponse(true);

    }


    public CreateResponse create(AuthorToSave authorToSave) {
        DBObject author = mapper.map(authorToSave, BasicDBObject.class);
        db.getCollection("author").insert(author);
        return new CreateResponse(((ObjectId) author.get("_id")).toHexString());

    }

    public void setDb(DB db) {
        this.db = db;
    }
}
