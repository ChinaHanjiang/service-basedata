package hu.tilos.radio.backend.author;

import hu.tilos.radio.backend.auth.Session;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.jwt.JwtToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
public class AuthorController {

    @Inject
    private AuthorService authorService;

    @RequestMapping(value = "/api/v1/author")
    public List<AuthorListElement> list() {
        return authorService.list();
    }

    @RequestMapping(value = "/api/v1/author/{alias}")
    public AuthorDetailed get(@PathVariable String alias) {
        return authorService.get(alias, getCurrentSession());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/api/v1/author", method = RequestMethod.POST)
    public CreateResponse create(@RequestBody AuthorToSave author) {
        return authorService.create(author);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/author/{alias}", method = RequestMethod.PUT)
    public UpdateResponse update(@PathVariable String alias, @RequestBody AuthorToSave author) {
        return authorService.update(alias, author);
    }

    public Session getCurrentSession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtToken) {
            JwtToken authToken = (JwtToken) auth;
            return authToken.getSession();
        }
        return null;
    }

}
