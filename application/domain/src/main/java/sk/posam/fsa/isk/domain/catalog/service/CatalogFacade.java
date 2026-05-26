package sk.posam.fsa.isk.domain.catalog.service;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;

import java.util.Collection;

public interface CatalogFacade {

    void create(Book book);

    Book find(ISBN isbn);

    Collection<Book> findAll();

    Collection<Book> findByTitle(String title);

    Collection<Book> findByAuthor(String author);

    Collection<Book> findByGenre(BookGenre genre);

    Collection<Book> search(String title, String author, BookGenre genre);

    void delete(ISBN isbn);

    Book addCopies(ISBN isbn, int count);
}
