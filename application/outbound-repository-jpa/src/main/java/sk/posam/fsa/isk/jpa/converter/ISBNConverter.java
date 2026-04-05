package sk.posam.fsa.isk.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sk.posam.fsa.isk.domain.catalog.ISBN;

@Converter(autoApply = true)
public class ISBNConverter implements AttributeConverter<ISBN, String> {

    @Override
    public String convertToDatabaseColumn(ISBN a) {return a == null ? null : a.getValue();}

    @Override
    public ISBN convertToEntityAttribute(String d) { return d == null ? null : new ISBN(d); }
}
