package sk.isk.domain.lending.predicate;

import sk.isk.domain.lending.Loan;
import sk.isk.domain.lending.LoanStatus;

import java.util.function.Predicate;

public class IsActiveLoanPredicate implements Predicate<Loan> {

    public static final IsActiveLoanPredicate INSTANCE = new IsActiveLoanPredicate();

    private IsActiveLoanPredicate() {}

    @Override
    public boolean test(Loan loan) {
        return loan != null && loan.getStatus() == LoanStatus.ACTIVE;
    }
}
