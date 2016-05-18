package hu.tilos.radio.backend.mongoconverters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class FromLocalDateTime implements Converter<LocalDateTime, Date> {

    @Override
    public Date convert(LocalDateTime source) {
        return new Date(source.atZone(ZoneId.systemDefault()).toEpochSecond()*1000);
    }
}
