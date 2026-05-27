package sk.posam.fsa.isk.domain.member.predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.finance.Money;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.member.Membership;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MemberPredicateTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member(1L, new Email("jan@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
    }

    // HasActiveMembershipPredicate
    @Test void hasActiveMembership_trueForActive() {
        assertTrue(HasActiveMembershipPredicate.INSTANCE.test(Membership.createNew()));
    }
    @Test void hasActiveMembership_falseForExpired() {
        assertFalse(HasActiveMembershipPredicate.INSTANCE.test(new Membership(LocalDate.now().minusDays(1))));
    }
    @Test void hasActiveMembership_falseForNull() {
        assertFalse(HasActiveMembershipPredicate.INSTANCE.test(null));
    }

    // HasCorrectEmailFormatPredicate
    @Test void emailFormat_trueForValid() {
        assertTrue(HasCorrectEmailFormatPredicate.INSTANCE.test("jan.novak@example.sk"));
    }
    @Test void emailFormat_falseForMissingAt() {
        assertFalse(HasCorrectEmailFormatPredicate.INSTANCE.test("jan.novak.example.sk"));
    }
    @Test void emailFormat_falseForMissingDot() {
        assertFalse(HasCorrectEmailFormatPredicate.INSTANCE.test("jan@example"));
    }
    @Test void emailFormat_falseForSpace() {
        assertFalse(HasCorrectEmailFormatPredicate.INSTANCE.test("jan novak@example.sk"));
    }
    @Test void emailFormat_falseForNull() {
        assertFalse(HasCorrectEmailFormatPredicate.INSTANCE.test(null));
    }

    // HasFirstNamePredicate
    @Test void firstName_trueForNonBlank() { assertTrue(HasFirstNamePredicate.INSTANCE.test("Jan")); }
    @Test void firstName_falseForBlank() { assertFalse(HasFirstNamePredicate.INSTANCE.test("  ")); }
    @Test void firstName_falseForNull() { assertFalse(HasFirstNamePredicate.INSTANCE.test(null)); }

    // HasLastNamePredicate
    @Test void lastName_trueForNonBlank() { assertTrue(HasLastNamePredicate.INSTANCE.test("Novak")); }
    @Test void lastName_falseForBlank() { assertFalse(HasLastNamePredicate.INSTANCE.test("  ")); }
    @Test void lastName_falseForNull() { assertFalse(HasLastNamePredicate.INSTANCE.test(null)); }

    // HasNoUnpaidFinesPredicate
    @Test void hasNoUnpaidFines_trueForMemberWithoutFines() {
        assertTrue(HasNoUnpaidFinesPredicate.INSTANCE.test(member));
    }
    @Test void hasNoUnpaidFines_falseForMemberWithPendingFine() {
        member.addFine(new Fine(Money.of(1.00, "EUR"), "Oneskorenie"));
        assertFalse(HasNoUnpaidFinesPredicate.INSTANCE.test(member));
    }
    @Test void hasNoUnpaidFines_trueAfterAllFinesPaid() {
        Fine fine = new Fine(Money.of(1.00, "EUR"), "Oneskorenie");
        member.addFine(fine);
        fine.pay();
        assertTrue(HasNoUnpaidFinesPredicate.INSTANCE.test(member));
    }
    @Test void hasNoUnpaidFines_falseForNull() {
        assertFalse(HasNoUnpaidFinesPredicate.INSTANCE.test(null));
    }
}
