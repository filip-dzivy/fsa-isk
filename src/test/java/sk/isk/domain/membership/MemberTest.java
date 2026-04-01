package sk.isk.domain.membership;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.isk.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.*;

public class MemberTest {
    private Member member;
    private Member librarian;

    @BeforeEach
    void setUp(){
        member = new Member(1L, new Email("jan.novak@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        member.assignMembership(Membership.createNew());
        librarian = new Member(2L, new Email("jan.novak@example.sk"), "Jan", "Novak", MemberRole.LIBRARIAN);
    }

    @Test
    void memberWithActiveMembershipAndNoFinesCanBorrow() {
        assertTrue(member.canBorrow());
    }

    @Test
    void memberWithoutMembershipCannotBorrow(){
        Member m = new Member(3L, new Email("x@x.sk"), "X", "x", MemberRole.MEMBER);
        assertFalse(m.canBorrow());
    }

    @Test
    void memberWithExpiredMembershipCannotBorrow() {
        member.assignMembership(
                new Membership(java.time.LocalDate.now().minusDays(1)));
        assertFalse(member.canBorrow());
    }

    @Test
    void memberWithUnpaidFineCannotBorrow() {
        Fine fine = new Fine(Money.of(1.00, "EUR"), "Oneskorené vrátenie o 10 dní");
        member.addFine(fine);
        assertFalse(member.canBorrow());
        assertTrue(member.hasUnpaidFines());
    }

    @Test
    void memberCanBorrowAfterPayingFine() {
        Fine fine = new Fine(Money.of(0.50, "EUR"), "Oneskorené vrátenie o 5 dní");
        member.addFine(fine);
        member.payFine(fine);
        assertFalse(member.hasUnpaidFines());
        assertTrue(member.canBorrow());
    }

    @Test
    void payingNonexistentFineThrows() {
        Fine fine = new Fine(Money.of(2.00, "EUR"), "Iná pokuta");
        assertThrows(DomainException.class, () -> member.payFine(fine));
    }

    @Test
    void renewMembershipWorks() {
        member.renewMembership();
        assertTrue(member.getMembership().isActive());
    }

    @Test
    void renewMembershipWithoutAssignedMembershipThrows() {
        Member m = new Member(5L, new Email("y@y.sk"), "Y", "Y", MemberRole.MEMBER);
        assertThrows(DomainException.class, m::renewMembership);
    }

    @Test
    void constructorValidatesBlankFirstName() {
        assertThrows(DomainException.class,
                () -> new Member(5L, new Email("a@b.sk"), " ", "Novak", MemberRole.MEMBER));
    }

    @Test
    void constructorValidatesBlankLastName() {
        assertThrows(DomainException.class,
                () -> new Member(5L, new Email("a@b.sk"), "Pavol", "   ", MemberRole.MEMBER));
    }

}
