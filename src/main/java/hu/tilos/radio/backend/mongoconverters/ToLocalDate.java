package hu.tilos.radio.backend.mongoconverters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class ToLocalDate implements Converter<Date, LocalDate> {

    @Override
    public LocalDate convert(Date source) {
        return source == null ? null : LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }
}
