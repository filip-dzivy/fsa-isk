package sk.posam.fsa.isk.jpa.springrepo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface MemberSpringDataRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(Email email);

    @EntityGraph(attributePaths = "fines")
    Optional<Member> findWithFinesById(long id);

    @EntityGraph(attributePaths = "fines")
    @Query("SELECT DISTINCT m FROM Member m")
    List<Member> findAllWithFines();
}
