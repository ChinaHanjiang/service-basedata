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
    private String captchaChallenge;

    @NotNull
    private String captchaResponse;

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

    public String getCaptchaChallenge() {
        return captchaChallenge;
    }

    public void setCaptchaChallenge(String captchaChallenge) {
        this.captchaChallenge = captchaChallenge;
    }

    public String getCaptchaResponse() {
        return captchaResponse;
    }

    public void setCaptchaResponse(String captchaResponse) {
        this.captchaResponse = captchaResponse;
    }
}
