package sk.posam.fsa.isk.domain.member.predicate;

import sk.posam.fsa.isk.domain.member.Member;

import java.util.function.Predicate;

public class HasNoUnpaidFinesPredicate implements Predicate<Member> {

    public static final HasNoUnpaidFinesPredicate INSTANCE = new HasNoUnpaidFinesPredicate();

    private HasNoUnpaidFinesPredicate() {}

    @Override
    public boolean test(Member member) {
        return member != null && !member.hasUnpaidFines();
    }
}
