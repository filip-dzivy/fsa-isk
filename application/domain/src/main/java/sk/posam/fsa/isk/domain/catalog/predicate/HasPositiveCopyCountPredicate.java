package sk.posam.fsa.isk.domain.catalog.predicate;

import java.util.function.Predicate;

/**
 * Overí, či je počet kópií kladné číslo.
 *
 * <p>Relevantný use-case: UC10 (správa kníh administrátorom), RQ01.</p>
 */
public class HasPositiveCopyCountPredicate implements Predicate<Integer> {

    public static final HasPositiveCopyCountPredicate INSTANCE = new HasPositiveCopyCountPredicate();

    private HasPositiveCopyCountPredicate() {}

    @Override
    public boolean test(Integer count) {
        return count != null && count > 0;
    }
}