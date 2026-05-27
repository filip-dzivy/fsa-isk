package sk.posam.fsa.isk.domain.lending;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.time.LocalDate;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class LoanFactoryTest {

    private LoanFactory factory;
    private Member loanedTo;
    private Member createdBy;
    private Book book;

    @BeforeEach
    void setUp() {
        factory = new LoanFactory();
        loanedTo = new Member(1L, new Email("jan@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        createdBy = new Member(2L, new Email("lib@example.sk"), "Lib", "Rarian", MemberRole.LIBRARIAN);
        book = new Book(new ISBN("9780306406157"), "Clean Code", "Robert C. Martin",
                BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008), 3);
    }

    @Test
    void createsValidLoan() {
        Loan loan = factory.createLoan(loanedTo, book, createdBy);
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertEquals(LocalDate.now().plusDays(14), loan.getDueDate());
    }

    @Test
    void createsLoanWithCustomDuration() {
        Loan loan = factory.createLoan(loanedTo, book, createdBy, LocalDate.now(), 7);
        assertEquals(LocalDate.now().plusDays(7), loan.getDueDate());
    }

    @Test
    void rejectsLoanCreatedByNonLibrarian() {
        Member memberCreator = new Member(3L, new Email("m@m.sk"), "M", "M", MemberRole.MEMBER);
        assertThrows(DomainException.class,
                () -> factory.createLoan(loanedTo, book, memberCreator));
    }

    @Test
    void rejectsLoanForPrivilegedLoanee() {
        Member loanedToLibrarian = new Member(4L, new Email("b@b.sk"), "B", "B", MemberRole.LIBRARIAN);
        assertThrows(DomainException.class,
                () -> factory.createLoan(loanedToLibrarian, book, createdBy));
    }

    @Test
    void rejectsLoanWithNullBook() {
        assertThrows(DomainException.class,
                () -> factory.createLoan(loanedTo, null, createdBy));
    }
}
