package sk.isk.domain.membership;

import org.junit.jupiter.api.Test;
import sk.isk.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.*;

public class FineTest {

    private static final Money AMOUNT = Money.of(1.40, "EUR");
    private static final String REASON = "Oneskorené vrátenie o 14 dní";

    @Test
    void newFineIsPending() {
        Fine fine = new Fine(AMOUNT, REASON);
        assertEquals(FineStatus.PENDING, fine.getStatus());
        assertFalse(fine.isPaid());
    }

    @Test
    void payReturnsPaidFine() {
        Fine fine = new Fine(AMOUNT, REASON);
        Fine paid = fine.pay();
        assertEquals(FineStatus.PAID, paid.getStatus());
        assertTrue(paid.isPaid());
    }

    @Test
    void originalFineIsImmutableAfterPay() {
        Fine fine = new Fine(AMOUNT, REASON);
        fine.pay();
        // original must remain PENDING (value object — pay() returns new instance)
        assertEquals(FineStatus.PENDING, fine.getStatus());
    }

    @Test
    void payingAlreadyPaidFineThrows() {
        Fine paid = new Fine(AMOUNT, REASON).pay();
        assertThrows(DomainException.class, paid::pay);
    }

    @Test
    void waiveReturnedWaivedFine() {
        Fine fine = new Fine(AMOUNT, REASON);
        Fine waived = fine.waive();
        assertEquals(FineStatus.WAIVED, waived.getStatus());
        assertTrue(waived.isPaid());
    }

    @Test
    void waivingPaidFineThrows() {
        Fine paid = new Fine(AMOUNT, REASON).pay();
        assertThrows(DomainException.class, paid::waive);
    }

    @Test
    void zeroAmountThrows() {
        assertThrows(DomainException.class, () -> new Fine(Money.zero("EUR"), REASON));
    }

    @Test
    void blankReasonThrows() {
        assertThrows(DomainException.class, () -> new Fine(AMOUNT, "  "));
    }
}