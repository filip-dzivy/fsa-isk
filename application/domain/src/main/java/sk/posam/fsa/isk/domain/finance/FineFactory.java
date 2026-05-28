package sk.posam.fsa.isk.domain.finance;

import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.member.Member;

public class FineFactory {
    public Fine createFor(Loan loan, Money dailyRate) {
        long days = loan.daysOverdue();
        Money total = dailyRate.multiply(days);
        String reason = "Oneskorené vrátenie o " + days + " dní";
        return new Fine(total, reason, loan);
    }

    public Fine createFor(Loan loan, Member member, Money dailyRate) {
        Fine fine = createFor(loan, dailyRate);
        fine.member = member;
        return fine;
    }

    public void updateFor(Fine fine, Loan loan, Money dailyRate) {
        Money newAmount = dailyRate.multiply(loan.daysOverdue());
        fine.updateAmount(newAmount);
    }
}
