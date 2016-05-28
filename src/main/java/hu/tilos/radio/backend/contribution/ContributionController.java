package hu.tilos.radio.backend.contribution;

import hu.tilos.radio.backend.data.response.OkResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;

@RestController
public class ContributionController {

    @Inject
    private ContributionService contributionService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/contribution", method = RequestMethod.POST)
    public String save(@RequestBody  ContributionToSave contribution) throws IOException {
        return contributionService.save(contribution);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/contribution", method = RequestMethod.DELETE)
    public OkResponse delete(@RequestParam String author, @RequestParam String show) throws IOException {
        return contributionService.delete(author, show);
    }


}
