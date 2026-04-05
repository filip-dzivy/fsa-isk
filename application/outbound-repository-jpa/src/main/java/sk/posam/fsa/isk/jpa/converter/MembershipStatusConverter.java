package sk.posam.fsa.isk.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sk.posam.fsa.isk.domain.member.MembershipStatus;

@Converter
public class MembershipStatusConverter implements AttributeConverter<MembershipStatus, String> {
    @Override
    public String convertToDatabaseColumn(MembershipStatus a) { return a == null ? null : a.name(); }
    @Override
    public MembershipStatus convertToEntityAttribute(String d) { return d == null ? null : MembershipStatus.valueOf(d); }
}
