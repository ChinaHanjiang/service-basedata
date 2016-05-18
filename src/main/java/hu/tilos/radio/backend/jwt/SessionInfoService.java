package hu.tilos.radio.backend.jwt;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.auth.Role;
import hu.tilos.radio.backend.auth.Session;
import hu.tilos.radio.backend.auth.UserInfo;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SessionInfoService {

    @Inject
    DB mongodb;

    public Session getSession(String username) {
        DBObject user = mongodb.getCollection("user").find(new BasicDBObject("username", username)).one();
        UserInfo ui = new UserInfo();
        ui.setUsername((String) user.get("username"));
        ui.setId(((ObjectId) user.get("_id")).toHexString());
        ui.setRole(Role.values()[(int) user.get("role_id")]);
        Session s = new Session();
        s.setCurrentUser(ui);
        return s;
    }
}
