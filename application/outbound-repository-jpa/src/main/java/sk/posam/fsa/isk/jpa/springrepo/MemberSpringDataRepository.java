package sk.posam.fsa.isk.jpa.springrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;

import java.util.Collection;
import java.util.Optional;

public interface MemberSpringDataRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(Email email);
}
