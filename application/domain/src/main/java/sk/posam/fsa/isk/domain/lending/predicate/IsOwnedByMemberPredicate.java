package sk.posam.fsa.isk.domain.lending.predicate;

import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.membership.Member;
import sk.posam.fsa.isk.domain.membership.MemberRole;

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
