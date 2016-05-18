package hu.tilos.radio.backend.scheduling;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RestController
public class SchedulingController {

    @Inject
    private SchedulingService schedulingService;

    @RequestMapping(value = "/api/v1/scheduling")
    public void scheduling(HttpServletResponse response) throws IOException {
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=tilos-heti.pdf");
        response.setHeader("Content-Transfer-Encoding", "binary");
        schedulingService.generatePdf(new Date(), response.getOutputStream());
    }


}
