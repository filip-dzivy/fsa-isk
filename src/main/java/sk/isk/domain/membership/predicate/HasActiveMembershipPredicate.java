package sk.isk.domain.membership.predicate;

import sk.isk.domain.membership.Membership;

import java.util.function.Predicate;

public class HasActiveMembershipPredicate implements Predicate<Membership> {
    public static final HasActiveMembershipPredicate INSTANCE = new HasActiveMembershipPredicate();

    private HasActiveMembershipPredicate() {};

    @Override
    public boolean test(Membership membership) {return membership != null && membership.isActive();}
}
