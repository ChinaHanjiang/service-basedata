package hu.tilos.radio.backend;

import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
@EnableScheduling
@EnableEurekaClient
//@EnableWebSecurity
@Configuration
public class BasedataStarter {

    @Value("${spring.application.name}")
    private String appName;

    public static void main(String[] args) {
        SpringApplication.run(BasedataStarter.class, args);
    }

    @PostConstruct
    public void setMdc() {
        MDC.put("application", appName);
    }
}


