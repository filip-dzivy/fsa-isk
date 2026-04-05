package sk.posam.fsa.isk.jpa.springrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.finance.FineStatus;
import sk.posam.fsa.isk.domain.lending.Loan;

import java.util.Optional;

public interface FineSpringDataRepository extends JpaRepository<Fine, Long> {
    Optional<Fine> findByLoanAndStatus(Loan loan, FineStatus status);
}