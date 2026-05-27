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

public class LoanTest {

    private Member loanedTo;
    private Member createdBy;
    private Book book;
    private LoanFactory factory;
    private Loan loanNotOverdue;
    private Loan loanOverdue;

    @BeforeEach
    void setUp(){
        loanedTo =  new Member(1L, new Email("jan.novak@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        createdBy =  new Member(2L, new Email("jan.horak@example.sk"), "Jan", "Horak", MemberRole.LIBRARIAN);
        book = new Book(new ISBN("9780306406157"), "Clean Code", "Robert C. Martin", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008),3);
        factory = new LoanFactory();
        loanNotOverdue = factory.createLoan(loanedTo, book, createdBy);
        loanOverdue = factory.createLoan(loanedTo, book, createdBy, LocalDate.now().minusDays(20), 14);
    }

    @Test
    void newLoanIsActiveWithCorrectDueDate(){
        assertEquals(LoanStatus.ACTIVE, loanNotOverdue.getStatus());
        assertEquals(LocalDate.now().plusDays(14), loanNotOverdue.getDueDate());
        assertEquals(0, loanNotOverdue.getRenewalCount());
    }

    @Test
    void bookReturnChangesStatusToReturned() {
        loanNotOverdue.returnBook();
        assertEquals(LoanStatus.RETURNED, loanNotOverdue.getStatus());
        assertEquals(LocalDate.now(), loanNotOverdue.getReturnDate());
    }

    @Test
    void returningAlreadyReturnedLoanThrows(){
        loanNotOverdue.returnBook();
        assertThrows(DomainException.class, loanNotOverdue::returnBook);
    }

    @Test
    void renewExtendsDueDateBy14Days() {
        LocalDate originalDue = loanNotOverdue.getDueDate();
        loanNotOverdue.renew(loanedTo);
        assertEquals(originalDue.plusDays(14), loanNotOverdue.getDueDate());
        assertEquals(1, loanNotOverdue.getRenewalCount());
    }

    @Test
    void renewedBookIsNotAvailableForRenew() {
        loanNotOverdue.renew(loanedTo);
        assertThrows(DomainException.class, () -> loanNotOverdue.renew(loanedTo));
    }

    @Test
    void renewingReturnedLoanThrows() {
        loanNotOverdue.returnBook();
        assertThrows(DomainException.class, () -> loanNotOverdue.renew(loanedTo));
    }

    @Test
    void OverdueWhenPastReturnDate() {
        assertTrue(loanOverdue.isOverdue());
        assertEquals(6, loanOverdue.daysOverdue());
    }

    @Test
    void notOverdueWhenReturnedOnTime() {
        Loan loan1 = factory.createLoan(loanedTo, book, createdBy, LocalDate.now().minusDays(10), 14);
        loan1.returnBook();
        assertFalse(loan1.isOverdue());
        assertEquals(0, loan1.daysOverdue());
    }

    @Test
    void renewingOverdueLoanThrows() {
        assertTrue(loanOverdue.isOverdue());
        assertThrows(DomainException.class, () -> loanOverdue.renew(loanedTo));
    }

    @Test
    void markOverdueSetsOverdueStatus() {
        loanOverdue.markOverdue();
        assertEquals(LoanStatus.OVERDUE, loanOverdue.getStatus());
    }

    @Test
    void daysOverdueIsZeroForActiveLoanWithinDueDate() {
        assertEquals(0, loanNotOverdue.daysOverdue());
    }

    @Test
    void validateForCreationFailsWhenCreatorIsNotLibrarian() {
        Member fakeCreator = new Member(3L, new Email("a@a.sk"), "A", "A", MemberRole.MEMBER);
        Loan loan = new Loan(loanedTo, book, fakeCreator);
        assertThrows(DomainException.class, loan::validateForCreation);
    }

    @Test
    void validateForCreationFailsWhenLoanedToIsNotMember() {
        Member loanedToLibrarian = new Member(4L, new Email("b@b.sk"), "B", "B", MemberRole.LIBRARIAN);
        Loan loan = new Loan(loanedToLibrarian, book, createdBy);
        assertThrows(DomainException.class, loan::validateForCreation);
    }

    @Test
    void validateForCreationFailsWhenBookIsNull() {
        Loan loan = new Loan(loanedTo, null, createdBy);
        assertThrows(DomainException.class, loan::validateForCreation);
    }

    @Test
    void renewByDifferentMemberThrowsForbidden() {
        Member other = new Member(5L, new Email("c@c.sk"), "C", "C", MemberRole.MEMBER);
        DomainException ex = assertThrows(DomainException.class, () -> loanNotOverdue.renew(other));
        assertEquals(DomainException.Type.FORBIDDEN, ex.getType());
    }

    @Test
    void markOverdueDoesNothingForReturnedLoan() {
        loanNotOverdue.returnBook();
        loanNotOverdue.markOverdue();
        assertEquals(LoanStatus.RETURNED, loanNotOverdue.getStatus());
    }

    @Test
    void markOverdueDoesNothingForActiveNonOverdueLoan() {
        loanNotOverdue.markOverdue();
        assertEquals(LoanStatus.ACTIVE, loanNotOverdue.getStatus());
    }

    @Test
    void daysOverdueIsZeroForLoanReturnedBeforeDueDate() {
        Loan loan = factory.createLoan(loanedTo, book, createdBy, LocalDate.now().minusDays(5), 14);
        loan.returnBook();
        assertEquals(0, loan.daysOverdue());
    }
}
