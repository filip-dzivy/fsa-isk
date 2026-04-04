package sk.posam.fsa.isk.domain.membership.predicate;

import java.util.function.Predicate;

public class HasFirstNamePredicate implements Predicate<String> {
    public static final HasFirstNamePredicate INSTANCE = new HasFirstNamePredicate();

    public HasFirstNamePredicate() {};

    @Override
    public boolean test(String firstName) {return firstName != null && !firstName.isBlank(); }
}
