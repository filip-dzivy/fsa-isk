package sk.posam.fsa.isk.domain.finance;

import org.junit.jupiter.api.Test;
import sk.posam.fsa.isk.domain.shared.DomainException;

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
    void payMutatesFineStatusToPaid() {
        Fine fine = new Fine(AMOUNT, REASON);
        fine.pay();
        assertEquals(FineStatus.PAID, fine.getStatus());
        assertTrue(fine.isPaid());
    }

    @Test
    void payingAlreadyPaidFineThrows() {
        Fine fine = new Fine(AMOUNT, REASON);
        fine.pay();
        assertThrows(DomainException.class, fine::pay);
    }

    @Test
    void waiveMutatesFineStatusToWaived() {
        Fine fine = new Fine(AMOUNT, REASON);
        fine.waive();
        assertEquals(FineStatus.WAIVED, fine.getStatus());
        assertTrue(fine.isPaid());
    }

    @Test
    void waivingPaidFineThrows() {
        Fine fine = new Fine(AMOUNT, REASON);
        fine.pay();
        assertThrows(DomainException.class, fine::waive);
    }

    @Test
    void zeroAmountThrows() {
        assertThrows(DomainException.class,
                () -> new Fine(Money.zero("EUR"), REASON));
    }

    @Test
    void blankReasonThrows() {
        assertThrows(DomainException.class,
                () -> new Fine(AMOUNT, "  "));
    }
}