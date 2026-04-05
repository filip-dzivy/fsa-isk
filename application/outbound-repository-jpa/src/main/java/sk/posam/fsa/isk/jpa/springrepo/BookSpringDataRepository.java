package sk.posam.fsa.isk.jpa.springrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.ISBN;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface BookSpringDataRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(ISBN isbn);

    Optional<Book> findByTitle(String title);

    Collection<Book> findByAuthor(String author);

    Collection<Book> findByGenre(String genre);
}
