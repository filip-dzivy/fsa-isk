package sk.posam.fsa.isk.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sk.posam.fsa.isk.domain.finance.FineStatus;

@Converter
public class FineStatusConverter implements AttributeConverter<FineStatus, String> {
    @Override
    public String convertToDatabaseColumn(FineStatus a) { return a == null ? null : a.name(); }
    @Override
    public FineStatus convertToEntityAttribute(String d) { return d == null ? null : FineStatus.valueOf(d); }
}
