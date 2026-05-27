package sk.posam.fsa.isk.domain.catalog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.BookPhoto;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.catalog.event.BookCopiesAddedEvent;
import sk.posam.fsa.isk.domain.catalog.query.BookView;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.reservation.Reservation;
import sk.posam.fsa.isk.domain.reservation.ReservationRepository;
import sk.posam.fsa.isk.domain.shared.DomainEventPublisher;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.domain.shared.PhotoStoragePort;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock private BookRepository bookRepository;
    @Mock private LoanRepository loanRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private DomainEventPublisher eventPublisher;
    @Mock private PhotoStoragePort photoStoragePort;

    @InjectMocks
    private CatalogService service;

    private ISBN isbn;
    private Book book;

    @BeforeEach
    void setUp() {
        isbn = new ISBN("9780306406157");
        book = new Book(isbn, "Clean Code", "Robert C. Martin",
                BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008), 3);
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    void createSavesWhenNotExisting() {
        when(bookRepository.find(isbn)).thenReturn(Optional.empty());

        service.create(book);

        verify(bookRepository).save(book);
    }

    @Test
    void createDuplicateIsbnThrowsConflict() {
        when(bookRepository.find(isbn)).thenReturn(Optional.of(book));

        DomainException ex = assertThrows(DomainException.class, () -> service.create(book));
        assertEquals(DomainException.Type.CONFLICT, ex.getType());
        verify(bookRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // find
    // -------------------------------------------------------------------------

    @Test
    void findReturnsBook() {
        when(bookRepository.find(isbn)).thenReturn(Optional.of(book));
        assertEquals(book, service.find(isbn));
    }

    @Test
    void findNotFoundThrows() {
        when(bookRepository.find(isbn)).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> service.find(isbn));
        assertEquals(DomainException.Type.NOT_FOUND, ex.getType());
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------

    @Test
    void deleteHappyPathDeletesBookAndItsPhotos() {
        when(bookRepository.find(isbn)).thenReturn(Optional.of(book));
        when(loanRepository.findActiveByBook(book)).thenReturn(List.of());
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of());
        book.addPhoto("https://example.com/1.jpg", "key1", null);
        book.addPhoto("https://example.com/2.jpg", "key2", null);

        service.delete(isbn);

        verify(photoStoragePort).delete("key1");
        verify(photoStoragePort).delete("key2");
        verify(bookRepository).delete(book);
    }

    @Test
    void deleteWithActiveLoansThrowsConflict() {
        when(bookRepository.find(isbn)).thenReturn(Optional.of(book));
        when(loanRepository.findActiveByBook(book)).thenReturn(List.of(mock(Loan.class)));

        DomainException ex = assertThrows(DomainException.class, () -> service.delete(isbn));
        assertEquals(DomainException.Type.CONFLICT, ex.getType());
        verify(bookRepository, never()).delete(any());
        verifyNoInteractions(photoStoragePort);
    }

    @Test
    void deleteWithActiveReservationsThrowsConflict() {
        when(bookRepository.find(isbn)).thenReturn(Optional.of(book));
        when(loanRepository.findActiveByBook(book)).thenReturn(List.of());
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of(mock(Reservation.class)));

        DomainException ex = assertThrows(DomainException.class, () -> service.delete(isbn));
        assertEquals(DomainException.Type.CONFLICT, ex.getType());
        verify(bookRepository, never()).delete(any());
    }

    // -------------------------------------------------------------------------
    // addCopies
    // -------------------------------------------------------------------------

    @Test
    void addCopiesIncreasesCountAndPublishesEvent() {
        when(bookRepository.find(isbn)).thenReturn(Optional.of(book));
        when(bookRepository.findWithPhotos(isbn)).thenReturn(Optional.of(book));

        BookView result = service.addCopies(isbn, 2);

        assertEquals(5, result.book().getTotalCopies());
        assertEquals(5, result.book().getAvailableCopies());
        verify(bookRepository).save(book);
        verify(eventPublisher).publish(any(BookCopiesAddedEvent.class));
    }

    @Test
    void addCopiesWithZeroCountPropagatesValidationError() {
        when(bookRepository.find(isbn)).thenReturn(Optional.of(book));
        assertThrows(DomainException.class, () -> service.addCopies(isbn, 0));
        verify(bookRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    // -------------------------------------------------------------------------
    // updateDescription
    // -------------------------------------------------------------------------

    @Test
    void updateDescriptionSetsTextAndSaves() {
        when(bookRepository.find(isbn)).thenReturn(Optional.of(book));
        when(bookRepository.findWithPhotos(isbn)).thenReturn(Optional.of(book));

        BookView result = service.updateDescription(isbn, "Nový popis");

        assertEquals("Nový popis", result.book().getDescription());
        verify(bookRepository).save(book);
    }

    // -------------------------------------------------------------------------
    // addPhoto
    // -------------------------------------------------------------------------

    @Test
    void addPhotoUploadsAndAttaches() {
        when(bookRepository.find(isbn)).thenReturn(Optional.of(book));
        when(photoStoragePort.upload(any(), any(), any()))
                .thenReturn(new PhotoStoragePort.StoredPhoto("https://example.com/x.jpg", "key1"));

        BookPhoto photo = service.addPhoto(isbn, new byte[]{1, 2, 3}, "image/jpeg", "x.jpg", "Caption");

        assertEquals("https://example.com/x.jpg", photo.getUrl());
        assertEquals("key1", photo.getStorageKey());
        verify(bookRepository).save(book);
        verify(photoStoragePort, never()).delete(any());
    }

    @Test
    void addPhotoRollsBackUploadWhenDomainRejects() {
        when(bookRepository.find(isbn)).thenReturn(Optional.of(book));
        // Fill book to MAX_PHOTOS first
        for (int i = 0; i < Book.MAX_PHOTOS; i++) {
            book.addPhoto("https://example.com/" + i + ".jpg", "k" + i, null);
        }
        when(photoStoragePort.upload(any(), any(), any()))
                .thenReturn(new PhotoStoragePort.StoredPhoto("https://example.com/x.jpg", "rollback-key"));

        assertThrows(DomainException.class,
                () -> service.addPhoto(isbn, new byte[]{}, "image/jpeg", "x.jpg", null));

        verify(photoStoragePort).delete("rollback-key");
        verify(bookRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // removePhoto
    // -------------------------------------------------------------------------

    @Test
    void removePhotoDeletesFromStorageAndSaves() {
        when(bookRepository.find(isbn)).thenReturn(Optional.of(book));
        BookPhoto photo = book.addPhoto("https://example.com/1.jpg", "keyToDelete", null);

        service.removePhoto(isbn, photo.getId());

        verify(bookRepository).save(book);
        verify(photoStoragePort).delete("keyToDelete");
    }

    // -------------------------------------------------------------------------
    // search
    // -------------------------------------------------------------------------

    @Test
    void searchByTitleHasHighestPriority() {
        when(bookRepository.findByTitle("Clean")).thenReturn(List.of(book));
        var result = service.search("Clean", "Martin", BookGenre.TECHNOLOGY);
        assertEquals(List.of(book), result);
        verify(bookRepository, never()).findByAuthor(any());
        verify(bookRepository, never()).findByGenre(any());
    }

    @Test
    void searchByAuthorWhenTitleIsNull() {
        when(bookRepository.findByAuthor("Martin")).thenReturn(List.of(book));
        var result = service.search(null, "Martin", BookGenre.TECHNOLOGY);
        assertEquals(List.of(book), result);
        verify(bookRepository, never()).findByGenre(any());
    }

    @Test
    void searchByGenreWhenTitleAndAuthorAreNull() {
        when(bookRepository.findByGenre(BookGenre.TECHNOLOGY)).thenReturn(List.of(book));
        var result = service.search(null, null, BookGenre.TECHNOLOGY);
        assertEquals(List.of(book), result);
    }

    @Test
    void searchReturnsAllWhenNoFilter() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        var result = service.search(null, null, null);
        assertEquals(List.of(book), result);
    }
}
