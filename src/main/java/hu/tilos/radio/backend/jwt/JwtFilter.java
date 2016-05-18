package hu.tilos.radio.backend.jwt;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;


public class JwtFilter extends GenericFilterBean {

    AuthenticationManager authenticationManager;

    public JwtFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req;
        req = (HttpServletRequest) request;

        String stringToken = req.getHeader("Authorization");


        if (stringToken != null && !stringToken.equals("")) {
            try {
                stringToken = stringToken.replaceAll("Bearer ", "");
                SignedJWT sjwt = SignedJWT.parse(stringToken);
                JwtToken token = new JwtToken(sjwt);
                Authentication auth = authenticationManager.authenticate((Authentication) token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        chain.doFilter(request, response);

    }
}