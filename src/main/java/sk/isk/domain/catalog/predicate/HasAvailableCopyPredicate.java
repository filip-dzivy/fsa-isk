package sk.isk.domain.catalog.predicate;

import sk.isk.domain.catalog.Book;

import java.util.function.Predicate;

/**
 * Overí, či má kniha aspoň jednu dostupnú kópiu.
 *
 * use-case: UC02 (vytvorenie výpožičky).
 */

public class HasAvailableCopyPredicate implements Predicate<Book> {
    public static final HasAvailableCopyPredicate INSTANCE = new HasAvailableCopyPredicate();

    private HasAvailableCopyPredicate() {}

    @Override
    public boolean test(Book book) {return book != null && book.getAvailableCopies() > 0;}
}
