package sk.posam.fsa.isk.domain.member;

import java.util.Collection;
import java.util.Optional;

public interface MemberRepository {

    Optional<Member> find(long id);

    Optional<Member> find(Email email);

    Collection<Member> findAll();

    void save(Member member);

    void delete(long id);
}