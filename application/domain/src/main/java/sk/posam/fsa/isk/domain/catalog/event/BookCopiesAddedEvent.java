package sk.posam.fsa.isk.domain.catalog.event;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.shared.DomainEvent;

public class BookCopiesAddedEvent extends DomainEvent {

    private final Book book;
    private final int addedCount;

    public BookCopiesAddedEvent(Book book, int addedCount) {
        this.book = book;
        this.addedCount = addedCount;
    }

    public Book getBook() {
        return book;
    }

    public int getAddedCount() {
        return addedCount;
    }
}
