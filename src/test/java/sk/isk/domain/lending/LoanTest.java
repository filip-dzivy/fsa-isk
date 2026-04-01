package sk.isk.domain.lending;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.isk.domain.catalog.ISBN;
import sk.isk.domain.membership.Email;
import sk.isk.domain.membership.Member;
import sk.isk.domain.membership.MemberRole;
import sk.isk.domain.shared.DomainException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LoanTest {

    private Member loanedTo;
    private Member createdBy;
    private ISBN isbn;
    private LoanFactory factory;
    private Loan loanNotOverdue;
    private Loan loanOverdue;

    @BeforeEach
    void setUp(){
        loanedTo =  new Member(1L, new Email("jan.novak@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        createdBy =  new Member(2L, new Email("jan.horak@example.sk"), "Jan", "Horak", MemberRole.LIBRARIAN);
        isbn = new ISBN("9780306406157");
        factory = new LoanFactory();
        loanNotOverdue = factory.createLoan(loanedTo, isbn, createdBy);
        loanOverdue = factory.createLoan(loanedTo, isbn, createdBy, LocalDate.now().minusDays(20), 14);
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
        loanNotOverdue.renew();
        assertEquals(originalDue.plusDays(14), loanNotOverdue.getDueDate());
        assertEquals(1, loanNotOverdue.getRenewalCount());
    }

    @Test
    void renewedBookIsNotAvailableForRenew() {
        loanNotOverdue.renew();
        assertThrows(DomainException.class, loanNotOverdue::renew);
    }

    @Test
    void renewingReturnedLoanThrows() {
        loanNotOverdue.returnBook();
        assertThrows(DomainException.class, loanNotOverdue::renew);
    }

    @Test
    void OverdueWhenPastReturnDate() {
        assertTrue(loanOverdue.isOverdue());
        assertEquals(6, loanOverdue.daysOverdue());
    }

    @Test
    void notOverdueWhenReturnedOnTime() {
        Loan loan1 = factory.createLoan(loanedTo, isbn, createdBy, LocalDate.now().minusDays(10), 14);
        loan1.returnBook();
        assertFalse(loan1.isOverdue());
        assertEquals(0, loan1.daysOverdue());
    }

    @Test
    void renewingOverdueLoanThrows() {
        assertTrue(loanOverdue.isOverdue());
        assertThrows(DomainException.class, loanOverdue::renew);
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

}
