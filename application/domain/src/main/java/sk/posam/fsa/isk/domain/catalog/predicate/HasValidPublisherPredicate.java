package sk.posam.fsa.isk.domain.catalog.predicate;

import java.util.function.Predicate;

public class HasValidPublisherPredicate implements Predicate<String> {
    public static final HasValidPublisherPredicate INSTANCE = new HasValidPublisherPredicate();

    private HasValidPublisherPredicate(){};

    @Override
    public boolean test(String publisher) {return publisher != null && !publisher.isBlank();}
}
