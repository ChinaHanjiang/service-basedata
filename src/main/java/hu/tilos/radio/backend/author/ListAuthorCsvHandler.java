package hu.tilos.radio.backend.author;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.util.AvatarLocator;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListAuthorCsvHandler {


    @Inject
    private DB db;

    @Inject
    AvatarLocator avatarLocator;

    @Inject
    private DozerBeanMapper mapper;


    public void handle() {
        DBCursor selectedAuthors = db.getCollection("author").find();

        List<String> resultList = new ArrayList();
        for (DBObject rawAuthor : selectedAuthors) {
            AuthorListElement author = mapper.map(rawAuthor, AuthorListElement.class);
            if (author.getContributions().size() > 0) {
                StringBuilder result = new StringBuilder();
                result.append(author.getName());
                result.append(";");
                result.append(author.getContributions().stream().map(contribution -> contribution.getNick()).collect(Collectors.joining(",")));
                result.append(";");
                if (rawAuthor.get("email") != null) {
                    result.append(rawAuthor.get("email").toString());
                }
                result.append(";");
                result.append(author.getContributions().stream().map(contribution -> contribution.getShow().getName()).collect(Collectors.joining(",")));
                result.append(";");
                result.append(rawAuthor.get("id"));
                resultList.add(result.toString() + "\n");
            }
        }


    }

}

