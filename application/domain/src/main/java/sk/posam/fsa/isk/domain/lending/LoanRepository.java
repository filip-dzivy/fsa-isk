package sk.posam.fsa.isk.domain.lending;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.member.Member;

import java.util.Collection;
import java.util.Optional;

public interface LoanRepository {

    Optional<Loan> find(long id);

    Collection<Loan> findByMember(Member member);

    Collection<Loan> findByBook(Book book);

    Collection<Loan> findActiveByBook(Book book);

    Collection<Loan> findAll();

    Collection<Loan> findOverdueLoans();

    Collection<Loan> findUnreturnedLoans();

    void save(Loan loan);
}
