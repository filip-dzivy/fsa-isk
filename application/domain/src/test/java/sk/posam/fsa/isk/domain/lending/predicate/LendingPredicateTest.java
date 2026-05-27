package sk.posam.fsa.isk.domain.lending.predicate;

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

import java.time.LocalDate;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class LendingPredicateTest {

    private Member loanedTo;
    private Member createdBy;
    private Book book;
    private LoanFactory factory;
    private Loan activeLoan;
    private Loan overdueLoan;

    @BeforeEach
    void setUp() {
        loanedTo = new Member(1L, new Email("jan@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        createdBy = new Member(2L, new Email("lib@example.sk"), "Lib", "Rarian", MemberRole.LIBRARIAN);
        book = new Book(new ISBN("9780306406157"), "Clean Code", "Robert C. Martin",
                BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008), 3);
        factory = new LoanFactory();
        activeLoan = factory.createLoan(loanedTo, book, createdBy);
        overdueLoan = factory.createLoan(loanedTo, book, createdBy,
                LocalDate.now().minusDays(20), 14);
    }

    // IsActiveLoanPredicate
    @Test void isActive_trueForActiveLoan() { assertTrue(IsActiveLoanPredicate.INSTANCE.test(activeLoan)); }
    @Test void isActive_falseForReturnedLoan() {
        activeLoan.returnBook();
        assertFalse(IsActiveLoanPredicate.INSTANCE.test(activeLoan));
    }
    @Test void isActive_falseForNull() { assertFalse(IsActiveLoanPredicate.INSTANCE.test(null)); }

    // HasRenewalCapacityPredicate
    @Test void hasRenewalCapacity_trueForFreshLoan() { assertTrue(HasRenewalCapacityPredicate.INSTANCE.test(activeLoan)); }
    @Test void hasRenewalCapacity_falseAfterRenewal() {
        activeLoan.renew(loanedTo);
        assertFalse(HasRenewalCapacityPredicate.INSTANCE.test(activeLoan));
    }
    @Test void hasRenewalCapacity_falseForNull() { assertFalse(HasRenewalCapacityPredicate.INSTANCE.test(null)); }

    // IsCreatedByLibrarianPredicate
    @Test void isCreatedByLibrarian_trueForLibrarianCreator() {
        assertTrue(IsCreatedByLibrarianPredicate.INSTANCE.test(activeLoan));
    }
    @Test void isCreatedByLibrarian_falseForMemberCreator() {
        Member memberCreator = new Member(3L, new Email("m@m.sk"), "M", "M", MemberRole.MEMBER);
        Loan loan = new Loan(loanedTo, book, memberCreator);
        assertFalse(IsCreatedByLibrarianPredicate.INSTANCE.test(loan));
    }
    @Test void isCreatedByLibrarian_falseForNull() {
        assertFalse(IsCreatedByLibrarianPredicate.INSTANCE.test(null));
    }

    // IsOverdueLoanPredicate / IsNotOverdueLoanPredicate
    @Test void isOverdue_trueForOverdueLoan() { assertTrue(IsOverdueLoanPredicate.INSTANCE.test(overdueLoan)); }
    @Test void isOverdue_falseForFreshLoan() { assertFalse(IsOverdueLoanPredicate.INSTANCE.test(activeLoan)); }
    @Test void isOverdue_falseForNull() { assertFalse(IsOverdueLoanPredicate.INSTANCE.test(null)); }

    @Test void isNotOverdue_trueForFreshLoan() { assertTrue(IsNotOverdueLoanPredicate.INSTANCE.test(activeLoan)); }
    @Test void isNotOverdue_falseForOverdueLoan() { assertFalse(IsNotOverdueLoanPredicate.INSTANCE.test(overdueLoan)); }
    @Test void isNotOverdue_falseForNull() { assertFalse(IsNotOverdueLoanPredicate.INSTANCE.test(null)); }

    // IsOwnedByMemberPredicate
    @Test void isOwnedByMember_trueWhenMatching() {
        assertTrue(IsOwnedByMemberPredicate.INSTANCE.test(activeLoan, loanedTo));
    }
    @Test void isOwnedByMember_falseForDifferentMember() {
        Member other = new Member(99L, new Email("x@x.sk"), "X", "X", MemberRole.MEMBER);
        assertFalse(IsOwnedByMemberPredicate.INSTANCE.test(activeLoan, other));
    }
    @Test void isOwnedByMember_falseWhenLoanedToIsLibrarian() {
        Member librarianAsBorrower = new Member(4L, new Email("y@y.sk"), "Y", "Y", MemberRole.LIBRARIAN);
        Loan loan = new Loan(librarianAsBorrower, book, createdBy);
        assertFalse(IsOwnedByMemberPredicate.INSTANCE.test(loan, librarianAsBorrower));
    }
    @Test void isOwnedByMember_falseForNullLoan() {
        assertFalse(IsOwnedByMemberPredicate.INSTANCE.test(null, loanedTo));
    }
    @Test void isOwnedByMember_falseForNullMember() {
        assertFalse(IsOwnedByMemberPredicate.INSTANCE.test(activeLoan, null));
    }
}
