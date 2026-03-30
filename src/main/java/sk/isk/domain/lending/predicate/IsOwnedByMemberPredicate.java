package sk.isk.domain.lending.predicate;

import sk.isk.domain.lending.Loan;
import sk.isk.domain.membership.Member;
import sk.isk.domain.membership.MemberRole;

import java.util.function.BiPredicate;

public class IsOwnedByMemberPredicate implements BiPredicate<Loan, Member> {

    public static final IsOwnedByMemberPredicate INSTANCE = new IsOwnedByMemberPredicate();

    private IsOwnedByMemberPredicate() {}

    @Override
    public boolean test(Loan loan, Member member) {
        if (loan == null || member == null) return false;
        return loan.getLoanedTo() != null
                && loan.getLoanedTo().getId() == member.getId()
                && loan.getLoanedTo().getMemberRole() == MemberRole.MEMBER;
    }
}
