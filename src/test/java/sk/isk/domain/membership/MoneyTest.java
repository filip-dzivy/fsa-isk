package sk.isk.domain.membership;

import org.junit.jupiter.api.Test;
import sk.isk.domain.shared.DomainException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {

    @Test
    void createsMoneyWithCorrectScale() {
        Money m = Money.of(1.5, "EUR");
        assertEquals(new BigDecimal("1.50"), m.getAmount());
        assertEquals("EUR", m.getCurrency());
    }

    @Test
    void zeroMoneyFactory() {
        Money m = Money.zero("EUR");
        assertEquals(BigDecimal.ZERO.setScale(2), m.getAmount());
    }

    @Test
    void additionOfSameCurrency() {
        Money a = Money.of(1.00, "EUR");
        Money b = Money.of(2.50, "EUR");
        assertEquals(Money.of(3.50, "EUR"), a.add(b));
    }

    @Test
    void additionOfDifferentCurrenciesThrows() {
        Money eur = Money.of(1.00, "EUR");
        Money usd = Money.of(1.00, "USD");
        assertThrows(DomainException.class, () -> eur.add(usd));
    }

    @Test
    void negativeAmountThrows() {
        assertThrows(DomainException.class, () -> Money.of(-0.01, "EUR"));
    }

    @Test
    void nullAmountThrows() {
        assertThrows(DomainException.class, () -> new Money(null, "EUR"));
    }

    @Test
    void blankCurrencyThrows() {
        assertThrows(DomainException.class, () -> new Money(BigDecimal.ONE, "  "));
    }

    @Test
    void isGreaterThanZero() {
        assertTrue(Money.of(0.01, "EUR").isGreaterThanZero());
        assertFalse(Money.zero("EUR").isGreaterThanZero());
    }

    @Test
    void equalityAndHashCode() {
        Money a = Money.of(1.00, "EUR");
        Money b = Money.of(1.00, "EUR");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void currencyNormalizedToUppercase() {
        Money m = new Money(BigDecimal.ONE, "eur");
        assertEquals("EUR", m.getCurrency());
    }
}