package sk.posam.fsa.isk.domain.catalog.query;

import sk.posam.fsa.isk.domain.catalog.Book;

public record BookView(Book book, int reservedCopies) {
}