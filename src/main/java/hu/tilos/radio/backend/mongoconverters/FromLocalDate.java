package hu.tilos.radio.backend.mongoconverters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class FromLocalDate implements Converter<LocalDate, Date> {

    @Override
    public Date convert(LocalDate source) {
        return new Date(source.atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
    }
}
