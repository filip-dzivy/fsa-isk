package sk.isk.domain.lending.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.isk.domain.catalog.Book;
import sk.isk.domain.catalog.BookGenre;
import sk.isk.domain.catalog.ISBN;
import sk.isk.domain.lending.Loan;
import sk.isk.domain.lending.LoanFactory;
import sk.isk.domain.membership.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

public class FineServiceTest {

    private FineService fineService;
    private LoanFactory factory;
    private Member loanedTo;
    private Member createdBy;
    private ISBN isbn;
    private Book book;

    @BeforeEach
    void setUp() {
        fineService = new FineService();
        factory = new LoanFactory();
        loanedTo = new Member(1L, new Email("jan.novak@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        createdBy = new Member(2L, new Email("jan.horak@example.sk"), "Jan", "Horak", MemberRole.LIBRARIAN);
        isbn = new ISBN("9780306406157");
        book = new Book(isbn, "Clean Code", "Robert C. Martin", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008),3);
    }

    @Test
    void calculatesFineFor1DayOverdue() {
        Loan loan = factory.createLoan(loanedTo, book, createdBy,
                LocalDate.now().minusDays(15), 14);
        loan.returnBook();

        Fine fine = fineService.calculate(loan);
        assertEquals(new BigDecimal("0.50"), fine.getAmount().getAmount());
        assertEquals("EUR", fine.getAmount().getCurrency());
        assertEquals(FineStatus.PENDING, fine.getStatus());
    }

    @Test
    void calculatesFineFor10DaysOverdue() {
        Loan loan = factory.createLoan(loanedTo, book, createdBy,
                LocalDate.now().minusDays(24), 14);
        loan.returnBook();

        Fine fine = fineService.calculate(loan);
        assertEquals(new BigDecimal("5.00"), fine.getAmount().getAmount());
    }

    @Test
    void calculatesFineForActiveLoanStillOverdue() {
        Loan loan = factory.createLoan(loanedTo, book, createdBy,
                LocalDate.now().minusDays(20), 14);
        assertTrue(loan.isOverdue());

        Fine fine = fineService.calculate(loan);
        assertEquals(new BigDecimal("3.00"), fine.getAmount().getAmount());
    }

    @Test
    void throwsWhenLoanIsNotOverdue() {
        Loan loan = new Loan(loanedTo, book, createdBy);
        assertThrows(IllegalArgumentException.class, () -> fineService.calculate(loan));
    }

    @Test
    void fineReasonContainsDayCount() {
        Loan loan = factory.createLoan(loanedTo, book, createdBy,
                LocalDate.now().minusDays(15), 14);
        loan.returnBook();

        Fine fine = fineService.calculate(loan);
        assertTrue(fine.getReason().contains("1"));
    }
}