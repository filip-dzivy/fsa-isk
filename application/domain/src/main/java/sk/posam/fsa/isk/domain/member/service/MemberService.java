package sk.posam.fsa.isk.domain.member.service;

import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.member.Membership;
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
        member.assignMembership(Membership.createNew());
        memberRepository.save(member);
    }

    @Override
    public Member find(long id) {
        return memberRepository.find(id)
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Člen s ID " + id + " neexistuje."));
    }

    @Override
    public Member find(Email email) {
        return memberRepository.find(email)
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Člen s emailom " + email + " neexistuje."));
    }

    @Override
    public Collection<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public void renewMembership(long id) {
        Member member = find(id);
        if (member.getMembership() == null){
            member.assignMembership(Membership.createNew());
        } else {
            member.renewMembership();
        }
        memberRepository.save(member);
    }

    @Override
    public void payFine(long memberId, long fineId) {
        Member member = find(memberId);
        Fine fine = member.getFines().stream()
                .filter(f -> f.getId() == fineId)
                .findFirst()
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Pokuta nenájdená."));
        fine.pay();
        memberRepository.save(member);
    }

    @Override
    public void waiveFine(long memberId, long fineId) {
        Member member = find(memberId);
        Fine fine = member.getFines().stream()
                .filter(f -> f.getId() == fineId)
                .findFirst()
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Pokuta nenájdená."));
        fine.waive();
        memberRepository.save(member);
    }
}
