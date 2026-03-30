package sk.isk.domain.catalog.predicate;

import java.util.function.Predicate;

/**
 * Overí, či názov knihy nie je prázdny.
 *
 * Relevantný use-case: UC10 (správa kníh administrátorom).
 */
public class HasRequiredTitlePredicate implements Predicate<String> {

    public static final HasRequiredTitlePredicate INSTANCE = new HasRequiredTitlePredicate();

    private HasRequiredTitlePredicate() {}

    @Override
    public boolean test(String title) {
        return title != null && !title.isBlank();
    }
}