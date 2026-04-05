package sk.posam.fsa.isk.domain.member.service;

import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;

import java.util.Collection;

public interface MemberFacade {

    void create(Member member);

    Member get(long id);

    Member get(Email email);

    Collection<Member> getAll();

    void renewMembership(long id);

    void payFine(long memberId, long fineIndex);

    void waiveFine(long memberId, long fineIndex);
}
