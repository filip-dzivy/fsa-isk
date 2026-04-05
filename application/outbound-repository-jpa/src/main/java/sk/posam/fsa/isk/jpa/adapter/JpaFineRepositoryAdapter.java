package sk.posam.fsa.isk.jpa.adapter;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.finance.FineRepository;
import sk.posam.fsa.isk.domain.finance.FineStatus;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.jpa.springrepo.FineSpringDataRepository;

import java.util.Optional;

@Repository
public class JpaFineRepositoryAdapter implements FineRepository {

    private final FineSpringDataRepository fineSpringDataRepository;
    private final EntityManager entityManager;

    public JpaFineRepositoryAdapter(FineSpringDataRepository fineSpringDataRepository,
                                    EntityManager entityManager) {
        this.fineSpringDataRepository = fineSpringDataRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Fine> findPendingByLoan(Loan loan) {
        return fineSpringDataRepository.findByLoanAndStatus(loan, FineStatus.PENDING);
    }

    @Override
    @Transactional
    public void save(Fine fine) {
        entityManager.merge(fine);
    }
}
