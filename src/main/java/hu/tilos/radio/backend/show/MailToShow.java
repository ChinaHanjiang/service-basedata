package hu.tilos.radio.backend.show;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MailToShow {

    @NotNull
    @Size(min = 3)
    private String from;

    @NotNull
    @Size(min = 3)
    private String subject;

    @NotNull
    @Size(min = 3)
    private String body;

    @NotNull
    private String captcha;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
