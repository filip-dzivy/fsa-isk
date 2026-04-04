package sk.posam.fsa.isk.domain.lending;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.membership.Member;

import java.util.Collection;
import java.util.Optional;

public interface LoanRepository {

    Optional<Loan> find(long id);

    Collection<Loan> findByMember(Member member);

    Collection<Loan> findByBook(Book book);

    Collection<Loan> findAll();

    Collection<Loan> findOverdueLoans();

    void save(Loan loan);
}
