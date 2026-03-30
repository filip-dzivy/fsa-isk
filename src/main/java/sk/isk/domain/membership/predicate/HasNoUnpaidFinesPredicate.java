package sk.isk.domain.membership.predicate;

import sk.isk.domain.membership.Member;

import java.util.function.Predicate;

public class HasNoUnpaidFinesPredicate implements Predicate<Member> {

    public static final HasNoUnpaidFinesPredicate INSTANCE = new HasNoUnpaidFinesPredicate();

    private HasNoUnpaidFinesPredicate() {}

    @Override
    public boolean test(Member member) {
        return member != null && !member.hasUnpaidFines();
    }
}
