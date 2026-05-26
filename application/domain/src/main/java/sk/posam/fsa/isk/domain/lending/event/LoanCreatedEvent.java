package sk.posam.fsa.isk.domain.lending.event;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.shared.DomainEvent;

public class LoanCreatedEvent extends DomainEvent {

    private final Member loanedTo;
    private final Book book;

    public LoanCreatedEvent(Member loanedTo, Book book) {
        this.loanedTo = loanedTo;
        this.book = book;
    }

    public Member getLoanedTo() {
        return loanedTo;
    }

    public Book getBook() {
        return book;
    }
}