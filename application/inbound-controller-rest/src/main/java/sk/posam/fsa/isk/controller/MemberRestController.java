package sk.posam.fsa.isk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.service.MemberFacade;
import sk.posam.fsa.isk.mapper.MemberMapper;
import sk.posam.fsa.isk.rest.api.MembersApi;
import sk.posam.fsa.isk.rest.dto.CreateMemberRequestDto;
import sk.posam.fsa.isk.rest.dto.MemberDto;
import sk.posam.fsa.isk.security.CurrentUserDetailService;

import java.util.List;

@RestController
public class MemberRestController implements MembersApi {

    private final MemberFacade memberFacade;
    private final MemberMapper memberMapper;
    private final CurrentUserDetailService currentUserDetailService;

    public MemberRestController(MemberFacade memberFacade,
                                MemberMapper memberMapper,
                                CurrentUserDetailService currentUserDetailService) {
        this.memberFacade = memberFacade;
        this.memberMapper = memberMapper;
        this.currentUserDetailService = currentUserDetailService;
    }

    @Override
    public ResponseEntity<MemberDto> getCurrentMember() {
        Member me = currentUserDetailService.getFullCurrentMember();
        // Re-fetch cez find(id) aby boli fines eager-loaded (find(email) ich nenačíta).
        Member withFines = memberFacade.find(me.getId());
        return ResponseEntity.ok(memberMapper.toDto(withFines));
    }

    @Override
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        return ResponseEntity.ok(
                memberFacade.findAll().stream()
                        .map(memberMapper::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<MemberDto> getMemberById(Long id) {
        Member member = memberFacade.find(id);
        return ResponseEntity.ok(memberMapper.toDto(member));
    }

    @Override
    public ResponseEntity<Void> createMember(CreateMemberRequestDto dto) {
        Member member = memberMapper.toMember(dto);
        memberFacade.create(member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> renewMembership(Long id) {
        memberFacade.renewMembership(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> payFine(Long id, Long fineId) {
        memberFacade.payFine(id, fineId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> waiveFine(Long id, Long fineId) {
        memberFacade.waiveFine(id, fineId);
        return ResponseEntity.ok().build();
    }
}
