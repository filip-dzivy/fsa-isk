package sk.posam.fsa.isk.domain.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    private ISBN isbn;
    private Book book;

    @BeforeEach
    void setUp() {
        isbn = new ISBN("9780306406157");
        book = new Book(isbn, "Clean Code", "Robert C. Martin", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008),3);
    }

    @Test
    void newBookIsAvailable() {
        assertTrue(book.isAvailable());
        assertEquals(3, book.getAvailableCopies());
        assertEquals(3, book.getTotalCopies());
    }

    @Test
    void borrowCopyDecreasesAvailableCopies(){
        book.borrowCopy();
        assertEquals(2, book.getAvailableCopies());
        assertTrue(book.isAvailable());
    }

    @Test
    void returnCopyIncreasesAvailableCopies(){
        book.borrowCopy();
        book.returnCopy();
        assertEquals(3, book.getAvailableCopies());
        assertTrue(book.isAvailable());
    }

    @Test
    void borrowingLastCopyMakesBookUnavailable() {
        book.borrowCopy();
        book.borrowCopy();
        book.borrowCopy();
        assertFalse(book.isAvailable());
    }

    @Test
    void borrowingUnavailableBookThrowsException(){
        book.borrowCopy();
        book.borrowCopy();
        book.borrowCopy();
        assertThrows(DomainException.class, book::borrowCopy);
    }

    @Test
    void returningMoreThanTotalThrowsException(){
        assertThrows(DomainException.class, book::returnCopy);
    }

    @Test
    void addCopiesIncreasesTotalAndAvailable() {
        book.borrowCopy();
        book.borrowCopy();
        book.borrowCopy();
        book.addCopies(2);
        assertTrue(book.isAvailable());
        assertEquals(2, book.getAvailableCopies());
        assertEquals(5, book.getTotalCopies());
    }

    @Test
    void addingZeroOrNegativeCopyCountThrowsException(){
        assertThrows(DomainException.class, () -> book.addCopies(0));
        assertThrows(DomainException.class, () -> book.addCopies(-2));
    }

    @Test
    void constructorValidatesTitle() {
        assertThrows(DomainException.class, () -> new Book(isbn, "", "Author", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008),1));
    }

    @Test
    void constructorValidatesAuthor() {
        assertThrows(DomainException.class, () -> new Book(isbn, "Clean Code", "", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008),1));
    }

    @Test
    void constructorValidatesTotalCopies() {
        assertThrows(DomainException.class, () -> new Book(isbn, "Clean Code", "Author", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008),0));
    }

    @Test
    void constructorValidatesPublisher() {
        assertThrows(DomainException.class, () -> new Book(isbn, "Clean Code", "Author", BookGenre.TECHNOLOGY, "", Year.of(2008),1));
    }

    @Test
    void constructorValidatesPublicationYear() {
        assertThrows(DomainException.class, () -> new Book(isbn, "Clean Code", "Author", BookGenre.TECHNOLOGY, "Prentice Hall", null,1));
    }

    @Test
    void equalityIsBasedOnISBN(){
        Book a = new Book(isbn, "bookA", "bookA", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008),1);
        Book b = new Book(isbn, "bookb", "bookb", BookGenre.SCIENCE, "PublisherB", Year.of(2018),10);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        Book c = new Book(new ISBN("0306406152"), "bookA", "bookA", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008),1);
        assertNotEquals(a, c);
    }

    @Test
    void nullGenreDefaultsToOther() {
        Book b = new Book(isbn, "Title", "Author", null, "Pub", Year.of(2008), 1);
        assertEquals(BookGenre.OTHER, b.getGenre());
    }

    @Test
    void constructorValidatesIsbn() {
        assertThrows(DomainException.class,
                () -> new Book(null, "Title", "Author", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008), 1));
    }

    @Test
    void updateDescriptionAcceptsValidText() {
        book.updateDescription("Krátky popis knihy.");
        assertEquals("Krátky popis knihy.", book.getDescription());
    }

    @Test
    void updateDescriptionTreatsNullAndBlankAsCleared() {
        book.updateDescription("Niečo");
        book.updateDescription(null);
        assertNull(book.getDescription());

        book.updateDescription("Niečo iné");
        book.updateDescription("   ");
        assertNull(book.getDescription());
    }

    @Test
    void updateDescriptionTooLongThrows() {
        String tooLong = "x".repeat(Book.MAX_DESCRIPTION_LENGTH + 1);
        assertThrows(DomainException.class, () -> book.updateDescription(tooLong));
    }

    @Test
    void addPhotoHappyPathSetsPosition() {
        BookPhoto p1 = book.addPhoto("https://example.com/1.jpg", "key1", "obal");
        BookPhoto p2 = book.addPhoto("https://example.com/2.jpg", "key2", null);
        assertEquals(0, p1.getPosition());
        assertEquals(1, p2.getPosition());
        assertEquals(2, book.getPhotos().size());
    }

    @Test
    void addPhotoOverLimitThrows() {
        for (int i = 0; i < Book.MAX_PHOTOS; i++) {
            book.addPhoto("https://example.com/" + i + ".jpg", "key" + i, null);
        }
        DomainException ex = assertThrows(DomainException.class,
                () -> book.addPhoto("https://example.com/x.jpg", "keyX", null));
        assertEquals(DomainException.Type.CONFLICT, ex.getType());
    }

    @Test
    void removePhotoNotFoundThrows() {
        book.addPhoto("https://example.com/1.jpg", "key1", null);
        DomainException ex = assertThrows(DomainException.class, () -> book.removePhoto(999L));
        assertEquals(DomainException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void getPhotosReturnsSortedByPosition() {
        BookPhoto p1 = book.addPhoto("https://example.com/1.jpg", "key1", null);
        BookPhoto p2 = book.addPhoto("https://example.com/2.jpg", "key2", null);
        p1.setPosition(5);
        p2.setPosition(1);
        java.util.List<BookPhoto> photos = book.getPhotos();
        // Equals on BookPhoto is id-based and all in-memory photos share id=0,
        // so we verify ordering by url instead.
        assertEquals("https://example.com/2.jpg", photos.get(0).getUrl());
        assertEquals("https://example.com/1.jpg", photos.get(1).getUrl());
    }
}