package sk.posam.fsa.isk.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.rest.dto.CreateMemberRequestDto;
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

    public MemberDto toDtoWithoutFines(Member entity) {
        if (entity == null) return null;

        MemberDto dto = new MemberDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail() != null ? entity.getEmail().toString() : null);
        dto.setMemberRole(MemberRoleDto.valueOf(entity.getMemberRole().name()));
        dto.setMembership(membershipMapper.toDto(entity.getMembership()));
        return dto;
    }

    public Member toMember(CreateMemberRequestDto dto) {
        if (dto == null) return null;
        return new Member(
                0L,
                new Email(dto.getEmail()),
                dto.getFirstName(),
                dto.getLastName(),
                MemberRole.valueOf(dto.getMemberRole().name())
        );
    }
}
