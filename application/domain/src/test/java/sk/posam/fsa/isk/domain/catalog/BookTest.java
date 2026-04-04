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
    }
}