package sk.posam.fsa.isk.domain.lending.event;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.shared.DomainEvent;

public class BookReturnedEvent extends DomainEvent {

    private final Book book;

    public BookReturnedEvent(Book book) {
        this.book = book;
    }

    public Book getBook() {
        return book;
    }
}
