package hu.tilos.radio.backend.captcha;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class RecaptchaValidator {

    ReCaptchaImpl reCaptcha = new ReCaptchaImpl();

    @Inject
    @Value("${recaptcha.privatekey}")
    private String privateKey;

    public boolean validate(String remoteAddress, String challenge, String solution) {
        reCaptcha.setPrivateKey(privateKey);
        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddress, challenge, solution);
        return reCaptchaResponse.isValid();
    }

}
