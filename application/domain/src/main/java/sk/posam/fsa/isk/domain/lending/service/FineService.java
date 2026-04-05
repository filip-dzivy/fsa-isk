package sk.posam.fsa.isk.domain.lending.service;

import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.member.Fine;
import sk.posam.fsa.isk.domain.member.Money;

import java.math.BigDecimal;

public class FineService {
    private static final BigDecimal DAILY_RATE = new BigDecimal("0.50");
    private static final String CURRENCY =  "EUR";

    public Fine calculate(Loan loan) {
        long daysOverdue = loan.daysOverdue();
        if (daysOverdue <= 0) {
            throw new IllegalArgumentException(
                    "Výpožička " + loan.getId() + " nie je po termíne — pokuta sa nevypočíta.");
        }
        Money amount = new Money(DAILY_RATE.multiply(BigDecimal.valueOf(daysOverdue)), CURRENCY);
        String reason = "Oneskorené vrátenie o " + daysOverdue
                + (daysOverdue == 1 ? " deň" : " dní");
        return new Fine(amount, reason);
    }

    public Money dailyRate() {
        return new Money(DAILY_RATE, CURRENCY);
    }
}
