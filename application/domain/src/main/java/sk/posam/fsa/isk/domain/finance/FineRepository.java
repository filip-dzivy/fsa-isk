package sk.posam.fsa.isk.domain.finance;

import sk.posam.fsa.isk.domain.lending.Loan;
import java.util.Optional;

public interface FineRepository {
    Optional<Fine> findPendingByLoan(Loan loan);
    void save(Fine fine);
}
