package sk.posam.fsa.isk.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanFactory;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.lending.LoanStatus;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRole;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanStatusJobTest {

    @Mock LoanRepository loanRepository;

    private LoanStatusJob job;
    private Member borrower;
    private Member librarian;
    private Book book;

    @BeforeEach
    void setUp() {
        job = new LoanStatusJob(loanRepository);

        borrower = new Member(1L, new Email("jan@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        librarian = new Member(2L, new Email("lib@example.sk"), "Lib", "Rar", MemberRole.LIBRARIAN);
        book = new Book(new ISBN("9780306406157"), "Clean Code", "Robert C. Martin",
                BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008), 3);
    }

    @Test
    void marksOverdueLoansAsOverdue() {
        Loan overdue = new LoanFactory().createLoan(borrower, book, librarian,
                LocalDate.now().minusDays(20), 14);
        when(loanRepository.findUnreturnedLoans()).thenReturn(List.of(overdue));

        int processed = job.updateLoanStatuses();

        assertEquals(1, processed);
        assertEquals(LoanStatus.OVERDUE, overdue.getStatus());
        verify(loanRepository).save(overdue);
    }

    @Test
    void skipsLoansThatAreNotOverdue() {
        Loan notOverdue = new LoanFactory().createLoan(borrower, book, librarian,
                LocalDate.now().minusDays(2), 14);
        when(loanRepository.findUnreturnedLoans()).thenReturn(List.of(notOverdue));

        int processed = job.updateLoanStatuses();

        assertEquals(0, processed);
        assertEquals(LoanStatus.ACTIVE, notOverdue.getStatus());
        verify(loanRepository, never()).save(any());
    }
}
