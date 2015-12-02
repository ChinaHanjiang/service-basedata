package hu.tilos.radio.backend.scheduling;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.GuiceRunner;
import hu.tilos.radio.backend.TestUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

public class SchedulingServiceTest {

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Inject
    FongoRule fongoRule;

    @Inject
    SchedulingService controller;

    @Test
    public void testHandle() throws ParseException {
        //given

        loadTo(fongoRule, "show", "show-sched1.json");
        loadTo(fongoRule, "show", "show-sched2.json");


        //when
        //List<SchedulingWithShow> schedulings = controller.listSchedulings(TestUtil.YYYYMMDD.parse("20140520"));

        //Assert.assertEquals(2, schedulings.size());
        //SchedulingWithShow schedulingWithShow = schedulings.get(0);
        //Assert.assertEquals(Integer.valueOf((5 * 24 + 8) * 60), schedulingWithShow.getWeekMinFrom());

        controller.generatePdf(new Date());
    }

}