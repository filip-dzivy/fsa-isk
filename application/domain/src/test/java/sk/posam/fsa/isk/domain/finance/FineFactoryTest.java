package sk.posam.fsa.isk.domain.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanFactory;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class FineFactoryTest {

    private FineFactory factory;
    private LoanFactory loanFactory;
    private Member loanedTo;
    private Member createdBy;
    private Book book;
    private Money dailyRate;

    @BeforeEach
    void setUp() {
        factory = new FineFactory();
        loanFactory = new LoanFactory();
        loanedTo = new Member(1L, new Email("jan@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        createdBy = new Member(2L, new Email("lib@example.sk"), "Lib", "Rarian", MemberRole.LIBRARIAN);
        book = new Book(new ISBN("9780306406157"), "Clean Code", "Robert C. Martin",
                BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008), 3);
        dailyRate = Money.of(0.50, "EUR");
    }

    @Test
    void createForOverdueLoanReturnsPendingFine() {
        Loan loan = loanFactory.createLoan(loanedTo, book, createdBy,
                LocalDate.now().minusDays(20), 14);

        Fine fine = factory.createFor(loan, dailyRate);

        assertEquals(FineStatus.PENDING, fine.getStatus());
        assertEquals(new BigDecimal("3.00"), fine.getAmount().getAmount());
        assertEquals("EUR", fine.getAmount().getCurrency());
        assertEquals(loan, fine.getLoan());
        assertTrue(fine.getReason().contains("6"));
    }

    @Test
    void updateForRecalculatesAmount() {
        Loan loan = loanFactory.createLoan(loanedTo, book, createdBy,
                LocalDate.now().minusDays(15), 14);
        Fine fine = factory.createFor(loan, dailyRate);
        assertEquals(new BigDecimal("0.50"), fine.getAmount().getAmount());

        // Simulate growth — recreate at a "10 days overdue" point.
        Loan moreOverdue = loanFactory.createLoan(loanedTo, book, createdBy,
                LocalDate.now().minusDays(24), 14);
        factory.updateFor(fine, moreOverdue, dailyRate);
        assertEquals(new BigDecimal("5.00"), fine.getAmount().getAmount());
    }
}
