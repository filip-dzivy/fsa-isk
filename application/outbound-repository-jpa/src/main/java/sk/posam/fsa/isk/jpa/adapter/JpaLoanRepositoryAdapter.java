package sk.posam.fsa.isk.jpa.adapter;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.lending.LoanStatus;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.jpa.springrepo.LoanSpringDataRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class JpaLoanRepositoryAdapter implements LoanRepository {

    private final LoanSpringDataRepository loanSpringDataRepository;
    private final EntityManager entityManager;

    public JpaLoanRepositoryAdapter(LoanSpringDataRepository loanSpringDataRepository,
                                    EntityManager entityManager) {
        this.loanSpringDataRepository = loanSpringDataRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Loan> find(long id) {
        return loanSpringDataRepository.findById(id);
    }

    @Override
    public Collection<Loan> findByMember(Member member) {
        return loanSpringDataRepository.findByLoanedTo(member);
    }

    @Override
    public Collection<Loan> findByBook(Book book) {
        return loanSpringDataRepository.findByBook(book);
    }

    @Override
    public Collection<Loan> findActiveByBook(Book book) {
        return loanSpringDataRepository.findByBookAndStatusNot(book, LoanStatus.RETURNED);
    }

    @Override
    public Collection<Loan> findAll() {
        return loanSpringDataRepository.findAll();
    }

    @Override
    public Collection<Loan> findOverdueLoans() {
        return loanSpringDataRepository.findByStatus(LoanStatus.OVERDUE);
    }

    @Override
    public Collection<Loan> findUnreturnedLoans() {
        return loanSpringDataRepository.findByStatusIn(
                java.util.List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
    }

    @Override
    @Transactional
    public void save(Loan loan) {
        attachReferences(loan);
        entityManager.merge(loan);
    }

    private void attachReferences(Loan loan) {
        if (loan.getLoanedTo() != null) {
            loan.setLoanedTo(entityManager.getReference(Member.class, loan.getLoanedTo().getId()));
        }
        if (loan.getCreatedBy() != null) {
            loan.setCreatedBy(entityManager.getReference(Member.class, loan.getCreatedBy().getId()));
        }
        if (loan.getBook() != null) {
            loan.setBook(entityManager.getReference(Book.class, loan.getBook().getId()));
        }
    }
}
