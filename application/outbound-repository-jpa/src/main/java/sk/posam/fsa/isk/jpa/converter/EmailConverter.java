package sk.posam.fsa.isk.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sk.posam.fsa.isk.domain.member.Email;

@Converter
public class EmailConverter implements AttributeConverter<Email, String> {
    @Override
    public String convertToDatabaseColumn(Email a) { return a == null ? null : a.toString(); }
    @Override
    public Email convertToEntityAttribute(String d) { return d == null ? null : new Email(d); }
}