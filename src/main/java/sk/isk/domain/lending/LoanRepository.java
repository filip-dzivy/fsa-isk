package sk.isk.domain.lending;

import sk.isk.domain.catalog.Book;
import sk.isk.domain.catalog.ISBN;
import sk.isk.domain.membership.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    Optional<Loan> find(long id);

    Collection<Loan> findByMember(Member member);

    Collection<Loan> findByBook(Book book);

    Collection<Loan> findAll();

    Collection<Loan> findOverdueLoans();

    void save(Loan loan);
}
