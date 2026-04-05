package sk.posam.fsa.isk.jpa.springrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;

import java.util.Optional;

public interface MemberSpringDataRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(Email email);
}
