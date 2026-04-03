// src/main/java/sk/librasys/domain/membership/MemberRepository.java
package sk.isk.domain.membership;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Optional<Member> find(long id);

    Optional<Member> find(Email email);

    Collection<Member> findAll();

    void save(Member member);

    void delete(long id);
}