package hu.tilos.radio.backend.bookmark;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.GuiceRunner;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.user.UserInfo;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

public class BookmarkServiceTest {

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);

    @Inject
    BookmarkService service;

    @Inject
    DozerBeanMapper mapper;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

//    @Test
//    public void testSave() throws Exception {
//        //given
//        String episodeId = loadTo(fongoRule, "episode", "episode-episode2.json");
//        String userId = loadTo(fongoRule, "user", "user-1.json");
//
//        BookmarkToSave bts = new BookmarkToSave();
//        bts.setFrom(TestUtil.YYYYMMDDHHMM.parse("201410121000"));
//        bts.setTo(TestUtil.YYYYMMDDHHMM.parse("201410121100"));
//        bts.setEpisodeRef(episodeId);
//        bts.setTitle("asd");
//
//        //when
//        service.create(new Session(new UserInfo(userId, "asd")), episodeId, bts);
//
//        //then
//        DBObject episode = fongoRule.getDB().getCollection("episode").findOne(new BasicDBObject("_id", new ObjectId((episodeId))));
//        BasicDBList bookmarks = (BasicDBList) episode.get("bookmarks");
//        Assert.assertEquals(1, bookmarks.size());
//    }

//    @Test(expected = IllegalArgumentException.class)
//    public void testSaveWithWrongId() throws Exception {
//        //given
//        String episodeId = loadTo(fongoRule, "episode", "episode-episode2.json");
//        String userId = loadTo(fongoRule, "user", "user-1.json");
//
//        BookmarkToSave bts = new BookmarkToSave();
//        bts.setFrom(TestUtil.YYYYMMDDHHMM.parse("201410121000"));
//        bts.setTo(TestUtil.YYYYMMDDHHMM.parse("201410121100"));
//        bts.setEpisodeRef(episodeId);
//        bts.setTitle("asd");
//
//        //when
//        service.create(new Session(new UserInfo(userId, "asd")), "asd", bts);
//
//    }
}