package sk.isk.domain.lending.predicate;

import sk.isk.domain.lending.Loan;
import sk.isk.domain.membership.MemberRole;

import java.util.function.Predicate;

public class IsCreatedByLibrarianPredicate implements Predicate<Loan> {

    public static final IsCreatedByLibrarianPredicate INSTANCE = new IsCreatedByLibrarianPredicate();

    private IsCreatedByLibrarianPredicate() {}

    @Override
    public boolean test(Loan loan) {
        return loan != null
                && loan.getCreatedBy() != null
                && loan.getCreatedBy().getMemberRole() == MemberRole.LIBRARIAN;
    }
}
