package sk.posam.fsa.isk.domain.catalog;

import java.util.Collection;
import java.util.Optional;

public interface BookRepository {

    Optional<Book> find(ISBN isbn);

    Optional<Book> findByTitle(String title);

    Collection<Book> findByAuthor(String author);

    Collection<Book> findByGenre(String genre);

    Collection<Book> findAll();

    void save(Book book);

    void delete(Book book);

}