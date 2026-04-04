package sk.posam.fsa.isk.domain.membership.predicate;

import sk.posam.fsa.isk.domain.membership.Membership;

import java.util.function.Predicate;

public class HasActiveMembershipPredicate implements Predicate<Membership> {
    public static final HasActiveMembershipPredicate INSTANCE = new HasActiveMembershipPredicate();

    private HasActiveMembershipPredicate() {};

    @Override
    public boolean test(Membership membership) {return membership != null && membership.isActive();}
}
