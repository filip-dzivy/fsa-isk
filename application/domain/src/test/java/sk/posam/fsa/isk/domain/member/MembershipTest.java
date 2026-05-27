package sk.posam.fsa.isk.domain.member;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MembershipTest {

    @Test
    void newMembershipIsActive(){
        Membership m = Membership.createNew();
        assertTrue(m.isActive());
    }

    @Test
    void expiredMembershipIsNotActive(){
        Membership m = new Membership(LocalDate.now().minusDays(1));
        assertFalse(m.isActive());
    }

    @Test
    void membershipExpiringSoonWithin30Days() {
        Membership m = new Membership(LocalDate.now().plusDays(15));
        assertTrue(m.isActive());
        assertTrue(m.isExpiringSoon());
    }

    @Test
    void renewActiveMembershipExtendsFromCurrentExpiry(){
        LocalDate expiry = LocalDate.now().plusDays(10);
        Membership m = new Membership(expiry);
        Membership renewed = m.renew();
        assertEquals(expiry.plusMonths(12), renewed.getExpiryDate());
        assertEquals(MembershipStatus.ACTIVE, renewed.getStatus());
    }

    @Test
    void renewExpiredMembershipExtendsFromToday(){
        Membership m = new Membership(LocalDate.now().minusDays(5));
        Membership renewed = m.renew();
        assertTrue(renewed.isActive());
        assertTrue(renewed.getExpiryDate().isAfter(LocalDate.now().plusMonths(11)));
    }

    @Test
    void isExpiringSoonFalseWhenFarFromExpiry() {
        Membership m = new Membership(LocalDate.now().plusDays(60));
        assertTrue(m.isActive());
        assertFalse(m.isExpiringSoon());
    }

    @Test
    void isExpiringSoonFalseForExpiredMembership() {
        Membership m = new Membership(LocalDate.now().minusDays(1));
        assertFalse(m.isActive());
        assertFalse(m.isExpiringSoon());
    }

    @Test
    void isExpiringSoonTrueOnBoundaryDay() {
        // Expiry exactly at +30 days falls inside the warning window (!isAfter today+30).
        Membership m = new Membership(LocalDate.now().plusDays(30));
        assertTrue(m.isExpiringSoon());
    }

    @Test
    void equalsAndHashCodeBasedOnExpiryAndStatus() {
        LocalDate expiry = LocalDate.now().plusDays(10);
        Membership a = new Membership(expiry);
        Membership b = new Membership(expiry);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void nullExpiryDateThrows() {
        assertThrows(NullPointerException.class, () -> new Membership(null));
    }
}
