package sk.isk.domain.lending.predicate;

import sk.isk.domain.lending.Loan;

import java.util.function.Predicate;

public class HasRenewalCapacityPredicate implements Predicate<Loan> {
    public static final HasRenewalCapacityPredicate INSTANCE = new HasRenewalCapacityPredicate();

    private static final int MAX_RENEWALS = 1;

    private HasRenewalCapacityPredicate() {};

    @Override
    public boolean test(Loan loan) {return loan != null && loan.getRenewalCount() < MAX_RENEWALS;}

}
