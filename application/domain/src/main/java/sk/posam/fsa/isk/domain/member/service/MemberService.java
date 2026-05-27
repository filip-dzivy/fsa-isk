package sk.posam.fsa.isk.domain.member.service;

import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.member.Membership;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.domain.shared.Transactional;

import java.util.Collection;

public class MemberService implements MemberFacade {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
    public Member find(long id) {
        return memberRepository.find(id)
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Člen s ID " + id + " neexistuje."));
    }

    @Override
    @Transactional(readOnly = true)
    public Member find(Email email) {
        return memberRepository.find(email)
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Člen s emailom " + email + " neexistuje."));
    }

    @Override
    @Transactional
    public Member findOrProvision(Email email, String firstName, String lastName) {
        return memberRepository.find(email).orElseGet(() -> {
            Member member = new Member(0L, email, firstName, lastName, MemberRole.MEMBER);
            member.assignMembership(Membership.createNew());
            memberRepository.save(member);
            return memberRepository.find(email).orElseThrow(() -> new IllegalStateException(
                    "Provisioned member could not be reloaded for email " + email));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
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
