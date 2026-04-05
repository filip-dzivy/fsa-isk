package sk.posam.fsa.isk.domain.catalog.service;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.text.CollationElementIterator;
import java.util.Collection;
import java.util.List;

public class CatalogService implements CatalogFacade{

    private final BookRepository bookRepository;

    public CatalogService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    @Override
    public void create(Book book) {
        bookRepository.find(book.getIsbn())
                .ifPresent(existing -> {
                    throw new DomainException(
                            DomainException.Type.CONFLICT,
                            "Kniha s ISBN " + book.getIsbn() + " už existuje.");
                });
        bookRepository.save(book);
    }

    @Override
    public Book find(ISBN isbn) {
        return bookRepository.find(isbn)
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Kniha s ISBN " + isbn + " neexistuje."));
    }

    @Override
    public Collection<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Collection<Book> findByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    @Override
    public Collection<Book> findByGenre(BookGenre genre) {
        return bookRepository.findByGenre(BookGenre.valueOf(genre.name()));
    }

    @Override
    public Collection<Book> findByTitle(String title) { return bookRepository.findByTitle(title);}
}
