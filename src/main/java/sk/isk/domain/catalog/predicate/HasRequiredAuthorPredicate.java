package sk.isk.domain.catalog.predicate;

import java.util.function.Predicate;

/**
 * Overí, či autor knihy nie je prázdny.
 *
 * Relevantný use-case: UC10 (správa kníh administrátorom).
 */
public class HasRequiredAuthorPredicate implements Predicate<String> {

    public static final HasRequiredAuthorPredicate INSTANCE = new HasRequiredAuthorPredicate();

    private HasRequiredAuthorPredicate() {}

    @Override
    public boolean test(String author) {
        return author != null && !author.isBlank();
    }
}
