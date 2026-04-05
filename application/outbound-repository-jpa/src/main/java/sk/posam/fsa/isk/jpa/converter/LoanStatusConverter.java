package sk.posam.fsa.isk.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sk.posam.fsa.isk.domain.lending.LoanStatus;

@Converter
public class LoanStatusConverter implements AttributeConverter<LoanStatus, String> {
    @Override
    public String convertToDatabaseColumn(LoanStatus a) { return a == null ? null : a.name(); }
    @Override
    public LoanStatus convertToEntityAttribute(String d) { return d == null ? null : LoanStatus.valueOf(d); }
}
