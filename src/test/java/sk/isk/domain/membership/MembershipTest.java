package sk.isk.domain.membership;

import org.junit.jupiter.api.Test;

import javax.sound.midi.MetaMessage;
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
}
