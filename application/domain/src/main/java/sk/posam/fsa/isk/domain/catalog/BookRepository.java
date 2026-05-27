package sk.posam.fsa.isk.domain.catalog;

import java.util.Collection;
import java.util.Optional;

public interface BookRepository {

    Optional<Book> find(ISBN isbn);

    Optional<Book> findWithPhotos(ISBN isbn);

    Collection<Book> findByTitle(String title);

    Collection<Book> findByAuthor(String author);

    Collection<Book> findByGenre(BookGenre genre);

    Collection<Book> findAll();

    Collection<Book> searchWithPhotos(String title, String author, BookGenre genre);

    Book save(Book book);

    void delete(Book book);

}