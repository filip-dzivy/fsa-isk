package sk.isk.domain.lending.predicate;

import sk.isk.domain.lending.Loan;

import java.util.function.Predicate;

public class IsOverdueLoanPredicate implements Predicate<Loan> {
    public static final IsOverdueLoanPredicate INSTANCE = new IsOverdueLoanPredicate();

    private IsOverdueLoanPredicate () {}

    @Override
    public boolean test(Loan loan){return loan != null && loan.isOverdue();}
}
