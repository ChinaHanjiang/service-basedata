package hu.tilos.radio.backend.show;

import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
public class ShowController {

    @Inject
    private ShowService showService;

    @RequestMapping(value = "/api/v1/show")
    public List<ShowSimple> list(@RequestParam(defaultValue = "active",required = false) String status) {
        return showService.list(status);
    }

    @RequestMapping(value = "/api/v1/show/{alias}")
    public ShowDetailed get(@PathVariable String alias) {
        return showService.get(alias);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/show", method = RequestMethod.POST)
    public CreateResponse create(@RequestBody @Validated ShowToSave show) {
        return showService.create(show);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/show/{alias}", method = RequestMethod.PUT)
    public UpdateResponse update(@PathVariable String alias, @RequestBody @Validated ShowToSave show) {
        return showService.update(alias, show);
    }
    
    @RequestMapping(value = "/api/v1/show/{alias}/contact", method = RequestMethod.POST)
    public OkResponse contact(@PathVariable String alias, @RequestBody @Validated  MailToShow mail) {
        return showService.contact(alias, mail);
    }
}
