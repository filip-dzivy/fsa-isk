package sk.posam.fsa.isk.jpa.springrepo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface BookSpringDataRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(ISBN isbn);

    @EntityGraph(attributePaths = "photos")
    Optional<Book> findWithPhotosByIsbn(ISBN isbn);

    Collection<Book> findByTitle(String title);

    Collection<Book> findByAuthor(String author);

    Collection<Book> findByGenre(BookGenre genre);

    @EntityGraph(attributePaths = "photos")
    @Query("SELECT DISTINCT b FROM Book b WHERE " +
            "(:title IS NULL OR b.title = :title) AND " +
            "(:author IS NULL OR b.author = :author) AND " +
            "(:genre IS NULL OR b.genre = :genre)")
    Collection<Book> searchWithPhotos(@Param("title") String title,
                                      @Param("author") String author,
                                      @Param("genre") BookGenre genre);
}