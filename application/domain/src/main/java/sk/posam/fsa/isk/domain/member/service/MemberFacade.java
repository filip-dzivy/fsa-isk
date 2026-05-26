package sk.posam.fsa.isk.domain.member.service;

import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.member.Member;

import java.util.Collection;

public interface MemberFacade {

    void create(Member member);

    Member findOrProvision(Email email, String firstName, String lastName);

    Member find(long id);

    Member find(Email email);

    Collection<Member> findAll();

    void renewMembership(long id);

    void payFine(long memberId, long fineId);

    void waiveFine(long memberId, long fineId);
}
