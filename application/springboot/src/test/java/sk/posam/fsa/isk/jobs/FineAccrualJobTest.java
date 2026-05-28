package sk.posam.fsa.isk.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.finance.FineFactory;
import sk.posam.fsa.isk.domain.finance.FineRepository;
import sk.posam.fsa.isk.domain.finance.Money;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanFactory;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FineAccrualJobTest {

    @Mock LoanRepository loanRepository;
    @Mock FineRepository fineRepository;

    private FineAccrualJob job;
    private Member borrower;
    private Member librarian;
    private Book book;
    private final Money dailyRate = Money.of(0.50, "EUR");

    @BeforeEach
    void setUp() {
        FineFactory fineFactory = new FineFactory();
        job = new FineAccrualJob(loanRepository, fineRepository, fineFactory, dailyRate);

        borrower = new Member(1L, new Email("jan@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        librarian = new Member(2L, new Email("lib@example.sk"), "Lib", "Rar", MemberRole.LIBRARIAN);
        book = new Book(new ISBN("9780306406157"), "Clean Code", "Robert C. Martin",
                BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008), 3);
    }

    @Test
    void accruesFineForOverdueLoanWhenNoneExists() {
        Loan overdue = new LoanFactory().createLoan(borrower, book, librarian,
                LocalDate.now().minusDays(20), 14);  // 6 days overdue
        overdue.markOverdue();
        when(loanRepository.findOverdueLoans()).thenReturn(List.of(overdue));
        when(fineRepository.findPendingByLoan(overdue)).thenReturn(Optional.empty());

        int processed = job.accrueOverdueFines();

        assertEquals(1, processed);
        ArgumentCaptor<Fine> fineCaptor = ArgumentCaptor.forClass(Fine.class);
        verify(fineRepository).save(fineCaptor.capture());
        assertEquals(new BigDecimal("3.00"), fineCaptor.getValue().getAmount().getAmount());
    }

    @Test
    void updatesExistingPendingFineInsteadOfCreatingNew() {
        Loan overdue = new LoanFactory().createLoan(borrower, book, librarian,
                LocalDate.now().minusDays(25), 14);  // 11 days overdue
        overdue.markOverdue();
        Fine existing = new Fine(Money.of(2.50, "EUR"), "stub", overdue);
        when(loanRepository.findOverdueLoans()).thenReturn(List.of(overdue));
        when(fineRepository.findPendingByLoan(overdue)).thenReturn(Optional.of(existing));

        job.accrueOverdueFines();

        assertEquals(new BigDecimal("5.50"), existing.getAmount().getAmount());
        verify(fineRepository).save(existing);
    }

    @Test
    void skipsWhenNoOverdueLoans() {
        when(loanRepository.findOverdueLoans()).thenReturn(List.of());

        int processed = job.accrueOverdueFines();

        assertEquals(0, processed);
        verifyNoInteractions(fineRepository);
    }
}
