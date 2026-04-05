package sk.posam.fsa.isk.domain.catalog;

import java.util.Collection;
import java.util.Optional;

public interface BookRepository {

    Optional<Book> find(ISBN isbn);

    Collection<Book> findByTitle(String title);

    Collection<Book> findByAuthor(String author);

    Collection<Book> findByGenre(BookGenre genre);

    Collection<Book> findAll();

    void save(Book book);

    void delete(Book book);

}