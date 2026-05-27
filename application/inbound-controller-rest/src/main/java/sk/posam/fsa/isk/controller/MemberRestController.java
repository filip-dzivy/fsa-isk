package sk.posam.fsa.isk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.service.MemberFacade;
import sk.posam.fsa.isk.domain.shared.Transactional;
import sk.posam.fsa.isk.mapper.MemberMapper;
import sk.posam.fsa.isk.rest.api.MembersApi;
import sk.posam.fsa.isk.rest.dto.CreateMemberRequestDto;
import sk.posam.fsa.isk.rest.dto.MemberDto;

import java.util.List;

@RestController
public class MemberRestController implements MembersApi {

    private final MemberFacade memberFacade;
    private final MemberMapper memberMapper;

    public MemberRestController(MemberFacade memberFacade, MemberMapper memberMapper) {
        this.memberFacade = memberFacade;
        this.memberMapper = memberMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        return ResponseEntity.ok(
                memberFacade.findAll().stream()
                        .map(memberMapper::toDto)
                        .toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<MemberDto> getMemberById(Long id) {
        Member member = memberFacade.find(id);
        return ResponseEntity.ok(memberMapper.toDto(member));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> createMember(CreateMemberRequestDto dto) {
        Member member = memberMapper.toMember(dto);
        memberFacade.create(member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> renewMembership(Long id) {
        memberFacade.renewMembership(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> payFine(Long id, Long fineId) {
        memberFacade.payFine(id, fineId);
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> waiveFine(Long id, Long fineId) {
        memberFacade.waiveFine(id, fineId);
        return ResponseEntity.ok().build();
    }
}
