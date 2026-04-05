package sk.posam.fsa.isk.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sk.posam.fsa.isk.domain.reservation.ReservationStatus;

@Converter
public class ReservationStatusConverter implements AttributeConverter<ReservationStatus, String> {
    @Override
    public String convertToDatabaseColumn(ReservationStatus a) { return a == null ? null : a.name(); }
    @Override
    public ReservationStatus convertToEntityAttribute(String d) { return d == null ? null : ReservationStatus.valueOf(d); }
}