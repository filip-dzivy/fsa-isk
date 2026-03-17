// src/main/java/sk/librasys/domain/catalog/BookRepository.java
package sk.isk.domain.catalog;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Optional<Book> findByIsbn(ISBN isbn);

    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findByGenre(String genre);

    List<Book> findAll();

    Book save(Book book);

    void delete(Book book);

    boolean existsByIsbn(ISBN isbn);
}