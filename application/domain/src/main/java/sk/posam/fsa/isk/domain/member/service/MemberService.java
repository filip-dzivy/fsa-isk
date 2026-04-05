package sk.posam.fsa.isk.domain.member.service;

import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Fine;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.util.Collection;

public class MemberService implements MemberFacade {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void create(Member member) {
        memberRepository.find(member.getEmail())
                .ifPresent(existing -> {
                    throw new DomainException(
                            DomainException.Type.CONFLICT,
                            "Člen s emailom " + member.getEmail() + " už existuje.");
                });
        memberRepository.save(member);
    }

    @Override
    public Member get(long id) {
        return memberRepository.find(id)
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Člen s ID " + id + " neexistuje."));
    }

    @Override
    public Member get(Email email) {
        return memberRepository.find(email)
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Člen s emailom " + email + " neexistuje."));
    }

    @Override
    public Collection<Member> getAll() {
        return memberRepository.findAll();
    }

    @Override
    public void renewMembership(long id) {
        Member member = get(id);
        member.renewMembership();
        memberRepository.save(member);
    }

    @Override
    public void payFine(long memberId, long fineIndex) {
        Member member = get(memberId);
        Fine fine = member.getFines().get((int) fineIndex);
        member.payFine(fine);
        memberRepository.save(member);
    }

    @Override
    public void waiveFine(long memberId, long fineIndex) {
        Member member = get(memberId);
        Fine fine = member.getFines().get((int) fineIndex);
        member.waiveFine(fine);
        memberRepository.save(member);
    }
}
