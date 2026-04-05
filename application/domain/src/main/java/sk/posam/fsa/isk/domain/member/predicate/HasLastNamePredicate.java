package sk.posam.fsa.isk.domain.member.predicate;

import java.util.function.Predicate;

public class HasLastNamePredicate implements Predicate<String> {
    public static final HasLastNamePredicate INSTANCE = new HasLastNamePredicate();

    public HasLastNamePredicate() {};

    @Override
    public boolean test(String lastName) {return lastName != null && !lastName.isBlank(); }
}
