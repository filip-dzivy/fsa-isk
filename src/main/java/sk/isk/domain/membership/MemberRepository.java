// src/main/java/sk/librasys/domain/membership/MemberRepository.java
package sk.isk.domain.membership;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findById(long id);

    Optional<Member> findByEmail(Email email);

    List<Member> findAll();

    Member save(Member member);

    void delete(long id);
}