package sk.posam.fsa.isk.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.member.Membership;
import sk.posam.fsa.isk.rest.dto.MembershipDto;
import sk.posam.fsa.isk.rest.dto.MembershipStatusDto;

@Component
public class MembershipMapper {

    public MembershipDto toDto(Membership entity) {
        if (entity == null) {
            return null;
        }

        MembershipDto dto = new MembershipDto();
        dto.setExpiryDate(entity.getExpiryDate());
        dto.setStatus(MembershipStatusDto.valueOf(entity.getStatus().name()));
        return dto;
    }
}
