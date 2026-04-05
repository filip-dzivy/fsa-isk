package sk.posam.fsa.isk.domain.finance.service;

import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.finance.Money;

import java.math.BigDecimal;

public class FineService {
    private static final BigDecimal DAILY_RATE = new BigDecimal("0.50");
    private static final String CURRENCY = "EUR";

    public Fine calculate(Loan loan) {
        long days = loan.daysOverdue();
        if (days <= 0) throw new IllegalArgumentException(
                "Výpožička " + loan.getId() + " nie je po termíne.");
        Money amount = new Money(DAILY_RATE.multiply(BigDecimal.valueOf(days)), CURRENCY);
        String reason = "Oneskorené vrátenie o " + days
                + (days == 1 ? " deň" : " dní");
        return new Fine(amount, reason, loan);
    }

    public Money dailyRate() {
        return new Money(DAILY_RATE, CURRENCY);
    }
}
