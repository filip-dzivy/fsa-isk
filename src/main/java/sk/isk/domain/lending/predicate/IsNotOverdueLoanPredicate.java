package sk.isk.domain.lending.predicate;

import sk.isk.domain.lending.Loan;

import java.util.function.Predicate;

public class IsNotOverdueLoanPredicate implements Predicate<Loan> {

    public static final IsNotOverdueLoanPredicate INSTANCE = new IsNotOverdueLoanPredicate();

    private IsNotOverdueLoanPredicate() {}

    @Override
    public boolean test(Loan loan) {
        return loan != null && !loan.isOverdue();
    }
}
