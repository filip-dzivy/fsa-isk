package sk.posam.fsa.isk.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.rest.dto.MemberDto;
import sk.posam.fsa.isk.rest.dto.MemberRoleDto;

import java.util.stream.Collectors;

@Component
public class MemberMapper {

    private final FineMapper fineMapper;
    private final MembershipMapper membershipMapper;

    public MemberMapper(FineMapper fineMapper, MembershipMapper membershipMapper) {
        this.fineMapper = fineMapper;
        this.membershipMapper = membershipMapper;
    }

    public MemberDto toDto(Member entity) {
        if (entity == null) {
            return null;
        }

        MemberDto dto = new MemberDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail() != null ? entity.getEmail().toString() : null);
        dto.setMemberRole(MemberRoleDto.valueOf(entity.getMemberRole().name()));
        dto.setMembership(membershipMapper.toDto(entity.getMembership()));
        dto.setFines(entity.getFines().stream()
                .map(fineMapper::toDto)
                .collect(Collectors.toList()));
        return dto;
    }
}
