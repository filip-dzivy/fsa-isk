package sk.posam.fsa.isk.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sk.posam.fsa.isk.domain.member.MemberRole;

@Converter
public class MemberRoleConverter implements AttributeConverter<MemberRole, String> {
    @Override
    public String convertToDatabaseColumn(MemberRole a) { return a == null ? null : a.name(); }
    @Override
    public MemberRole convertToEntityAttribute(String d) { return d == null ? null : MemberRole.valueOf(d); }
}
