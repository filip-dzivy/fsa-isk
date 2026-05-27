package sk.posam.fsa.isk.domain.catalog.service;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.BookPhoto;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.catalog.event.BookCopiesAddedEvent;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.reservation.ReservationRepository;
import sk.posam.fsa.isk.domain.shared.DomainEventPublisher;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.domain.shared.PhotoStoragePort;

import java.text.CollationElementIterator;
import java.util.Collection;
import java.util.List;

public class CatalogService implements CatalogFacade{

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final DomainEventPublisher eventPublisher;
    private final PhotoStoragePort photoStoragePort;

    public CatalogService(BookRepository bookRepository,
                          LoanRepository loanRepository,
                          ReservationRepository reservationRepository,
                          DomainEventPublisher eventPublisher,
                          PhotoStoragePort photoStoragePort){
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
        this.photoStoragePort = photoStoragePort;
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

    @Override
    public Collection<Book> search(String title, String author, BookGenre genre) {
        if (title != null) return bookRepository.findByTitle(title);
        if (author != null) return bookRepository.findByAuthor(author);
        if (genre != null) return bookRepository.findByGenre(genre);
        return bookRepository.findAll();
    }

    @Override
    public void delete(ISBN isbn) {
        Book book = find(isbn);
        if (!loanRepository.findActiveByBook(book).isEmpty()) {
            throw new DomainException(
                    DomainException.Type.CONFLICT,
                    "Knihu nemožno odstrániť, má aktívne výpožičky.");
        }
        if (!reservationRepository.findActiveByBook(book).isEmpty()) {
            throw new DomainException(
                    DomainException.Type.CONFLICT,
                    "Knihu nemožno odstrániť, má aktívne rezervácie.");
        }
        for (BookPhoto photo : book.getPhotos()) {
            photoStoragePort.delete(photo.getStorageKey());
        }
        bookRepository.delete(book);
    }

    @Override
    public Book addCopies(ISBN isbn, int count) {
        Book book = find(isbn);
        book.addCopies(count);
        bookRepository.save(book);
        eventPublisher.publish(new BookCopiesAddedEvent(book, count));
        return book;
    }

    @Override
    public Book updateDescription(ISBN isbn, String description) {
        Book book = find(isbn);
        book.updateDescription(description);
        bookRepository.save(book);
        return book;
    }

    @Override
    public BookPhoto addPhoto(ISBN isbn, byte[] bytes, String contentType, String originalFilename, String caption) {
        Book book = find(isbn);
        PhotoStoragePort.StoredPhoto stored = photoStoragePort.upload(bytes, contentType, originalFilename);
        BookPhoto photo;
        try {
            photo = book.addPhoto(stored.url(), stored.storageKey(), caption);
        } catch (RuntimeException ex) {
            photoStoragePort.delete(stored.storageKey());
            throw ex;
        }
        bookRepository.save(book);
        return photo;
    }

    @Override
    public void removePhoto(ISBN isbn, long photoId) {
        Book book = find(isbn);
        BookPhoto removed = book.removePhoto(photoId);
        bookRepository.save(book);
        photoStoragePort.delete(removed.getStorageKey());
    }
}
