package sk.posam.fsa.isk.domain.catalog.predicate;

import sk.posam.fsa.isk.domain.catalog.ISBN;

import java.util.function.Predicate;

public class HasRequiredISBNPredicate implements Predicate<ISBN> {
    public static final HasRequiredISBNPredicate INSTANCE = new HasRequiredISBNPredicate();

    public HasRequiredISBNPredicate() {}

    @Override
    public boolean test(ISBN isbn) {return isbn != null && isbn.validate();}
}
