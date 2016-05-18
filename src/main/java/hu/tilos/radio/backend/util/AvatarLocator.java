package hu.tilos.radio.backend.util;

import hu.tilos.radio.backend.author.AuthorBasic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;

@Service
public class AvatarLocator {

    @Inject
    @Value("${upload.dir}")
    private String uploadDir;

    public void locateAvatar(AuthorBasic author) {
        if (new File(uploadDir + "/avatar", author.getId() + ".jpg").exists()) {
            author.setAvatar("https://tilos.hu/upload/avatar/" + author.getId() + ".jpg");
        } else if (new File(uploadDir + "/avatar", author.getAlias() + ".jpg").exists()) {
            author.setAvatar("https://tilos.hu/upload/avatar/" + author.getAlias() + ".jpg");
        } else {
            author.setAvatar("https://tilos.hu/upload/avatar/noimage.jpg");
        }
    }


}
