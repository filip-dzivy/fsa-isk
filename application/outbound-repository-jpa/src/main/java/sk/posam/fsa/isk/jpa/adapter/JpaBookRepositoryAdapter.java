package sk.posam.fsa.isk.jpa.adapter;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.jpa.springrepo.BookSpringDataRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class JpaBookRepositoryAdapter implements BookRepository {

    private final BookSpringDataRepository bookSpringDataRepository;
    private final EntityManager entityManager;

    public JpaBookRepositoryAdapter(BookSpringDataRepository bookSpringDataRepository, EntityManager entityManager){
        this.bookSpringDataRepository = bookSpringDataRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Book> find(ISBN isbn) {
        return bookSpringDataRepository.findByIsbn(isbn);
    }

    @Override
    public Optional<Book> findByTitle(String title) {
        return bookSpringDataRepository.findByTitle(title);
    }

    @Override
    public Collection<Book> findByAuthor(String author) {
        return bookSpringDataRepository.findByAuthor(author);
    }

    @Override
    public Collection<Book> findByGenre(String genre) {
        return bookSpringDataRepository.findByGenre(genre);
    }

    @Override
    public Collection<Book> findAll() {
        return bookSpringDataRepository.findAll();
    }

    @Override
    @Transactional
    public void save(Book book) {
        entityManager.merge(book);
    }

    @Override
    public void delete(Book book) {
        Book managed = entityManager.merge(book);
        entityManager.remove(managed);
    }
}
