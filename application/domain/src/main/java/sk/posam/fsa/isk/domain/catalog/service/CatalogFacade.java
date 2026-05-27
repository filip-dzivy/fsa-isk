package sk.posam.fsa.isk.domain.catalog.service;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.BookPhoto;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.catalog.query.BookView;

import java.util.Collection;

public interface CatalogFacade {

    void create(Book book);

    Book find(ISBN isbn);

    BookView findDetail(ISBN isbn);

    Collection<Book> findAll();

    Collection<Book> findByTitle(String title);

    Collection<Book> findByAuthor(String author);

    Collection<Book> findByGenre(BookGenre genre);

    Collection<Book> search(String title, String author, BookGenre genre);

    Collection<BookView> searchForListing(String title, String author, BookGenre genre);

    void delete(ISBN isbn);

    BookView addCopies(ISBN isbn, int count);

    BookView updateDescription(ISBN isbn, String description);

    BookPhoto addPhoto(ISBN isbn, byte[] bytes, String contentType, String originalFilename, String caption);

    void removePhoto(ISBN isbn, long photoId);
}
