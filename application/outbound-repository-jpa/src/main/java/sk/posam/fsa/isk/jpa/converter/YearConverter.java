package sk.posam.fsa.isk.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Year;

@Converter
public class YearConverter implements AttributeConverter<Year, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Year a) { return a == null ? null : a.getValue(); }
    @Override
    public Year convertToEntityAttribute(Integer d) { return d == null ? null : Year.of(d); }
}
