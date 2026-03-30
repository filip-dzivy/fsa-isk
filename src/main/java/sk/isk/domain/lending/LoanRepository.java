package sk.isk.domain.lending;

import sk.isk.domain.catalog.ISBN;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    Optional<Loan> findById(long loanId);

    List<Loan> findByMemberId(long memberId);

    List<Loan> findActiveByIsbn(ISBN isbn);

    List<Loan> findOverdueLoans();

    List<Loan> findAll();

    Loan save(Loan loan);
}
