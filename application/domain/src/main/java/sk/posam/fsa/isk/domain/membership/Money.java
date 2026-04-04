package sk.posam.fsa.isk.domain.membership;

import sk.posam.fsa.isk.domain.shared.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money {

    private final BigDecimal amount;
    private final String     currency;

    public Money(BigDecimal amount, String currency) {
        if (amount == null)     throw new DomainException("Suma nesmie byť null.");
        if (amount.compareTo(BigDecimal.ZERO) < 0) throw new DomainException("Suma nesmie byť záporná.");
        if (currency == null || currency.isBlank()) throw new DomainException("Mena nesmie byť prázdna.");
        this.amount   = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency.toUpperCase();
    }

    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), currency);
    }

    public boolean isGreaterThanZero() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private void assertSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new DomainException("Nemožno sčítať rôzne meny: " + currency + " a " + other.currency);
        }
    }

    public BigDecimal getAmount()  { return amount; }
    public String     getCurrency(){ return currency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money m = (Money) o;
        return amount.equals(m.amount) && currency.equals(m.currency);
    }

    @Override
    public int hashCode() { return Objects.hash(amount, currency); }

    @Override
    public String toString() { return amount + " " + currency; }
}
