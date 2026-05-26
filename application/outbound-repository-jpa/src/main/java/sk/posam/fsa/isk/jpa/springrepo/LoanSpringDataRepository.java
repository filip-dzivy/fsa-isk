package sk.posam.fsa.isk.jpa.springrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanStatus;
import sk.posam.fsa.isk.domain.member.Member;

import java.util.Collection;

public interface LoanSpringDataRepository extends JpaRepository<Loan, Long> {
    Collection<Loan> findByLoanedTo(Member member);

    Collection<Loan> findByBook(Book book);

    Collection<Loan> findByBookAndStatusNot(Book book, LoanStatus status);

    Collection<Loan> findByStatus(LoanStatus status);

    Collection<Loan> findByStatusIn(java.util.List<LoanStatus> statuses);
}
