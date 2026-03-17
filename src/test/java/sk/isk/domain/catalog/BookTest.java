// src/test/java/sk/librasys/domain/catalog/BookTest.java
package sk.isk.domain.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Book Aggregate Tests")
class BookTest {

    private ISBN isbn;
    private Book book;

    @BeforeEach
    void setUp() {
        isbn = new ISBN("9780306406157");
        book = new Book(
                isbn,
                "Clean Code",
                "Robert C. Martin",
                "Programming",
                "Prentice Hall",
                2008,
                5
        );
    }

    @Test
    @DisplayName("Should create book with all copies available")
    void shouldCreateBookWithAllCopiesAvailable() {
        assertThat(book.getIsbn()).isEqualTo(isbn);
        assertThat(book.getTitle()).isEqualTo("Clean Code");
        assertThat(book.getAuthor()).isEqualTo("Robert C. Martin");
        assertThat(book.getTotalCopies()).isEqualTo(5);
        assertThat(book.getAvailableCopies()).isEqualTo(5);
        assertThat(book.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when creating book with null ISBN")
    void shouldThrowExceptionForNullIsbn() {
        assertThatThrownBy(() -> new Book(
                null,
                "Title",
                "Author",
                "Genre",
                "Publisher",
                2020,
                1
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ISBN cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when creating book with empty title")
    void shouldThrowExceptionForEmptyTitle() {
        assertThatThrownBy(() -> new Book(
                isbn,
                "",
                "Author",
                "Genre",
                "Publisher",
                2020,
                1
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Title cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when creating book with zero copies")
    void shouldThrowExceptionForZeroCopies() {
        assertThatThrownBy(() -> new Book(
                isbn,
                "Title",
                "Author",
                "Genre",
                "Publisher",
                2020,
                0
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total copies must be at least 1");
    }

    @Test
    @DisplayName("Should borrow a copy successfully")
    void shouldBorrowCopySuccessfully() {
        book.borrowCopy();

        assertThat(book.getAvailableCopies()).isEqualTo(4);
        assertThat(book.getBorrowedCopiesCount()).isEqualTo(1);
        assertThat(book.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should borrow multiple copies")
    void shouldBorrowMultipleCopies() {
        book.borrowCopy();
        book.borrowCopy();
        book.borrowCopy();

        assertThat(book.getAvailableCopies()).isEqualTo(2);
        assertThat(book.getBorrowedCopiesCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should throw exception when borrowing from unavailable book")
    void shouldThrowExceptionWhenBorrowingUnavailableBook() {
        for (int i = 0; i < 5; i++) {
            book.borrowCopy();
        }

        assertThat(book.isAvailable()).isFalse();
        assertThatThrownBy(() -> book.borrowCopy())
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("not available")
                .hasMessageContaining("Available: 0/5");
    }

    @Test
    @DisplayName("Should return a copy successfully")
    void shouldReturnCopySuccessfully() {
        book.borrowCopy();
        book.borrowCopy();

        book.returnCopy();

        assertThat(book.getAvailableCopies()).isEqualTo(4);
        assertThat(book.getBorrowedCopiesCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw exception when returning more copies than borrowed")
    void shouldThrowExceptionWhenReturningTooManyCopies() {

        assertThatThrownBy(() -> book.returnCopy())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("all copies are already available");
    }

    @Test
    @DisplayName("Should add copies successfully")
    void shouldAddCopiesSuccessfully() {
        book.addCopies(3);

        assertThat(book.getTotalCopies()).isEqualTo(8);
        assertThat(book.getAvailableCopies()).isEqualTo(8);
    }

    @Test
    @DisplayName("Should add copies to partially borrowed book")
    void shouldAddCopiesToPartiallyBorrowedBook() {
        book.borrowCopy();
        book.borrowCopy();

        book.addCopies(3);

        assertThat(book.getTotalCopies()).isEqualTo(8);
        assertThat(book.getAvailableCopies()).isEqualTo(6); // 3 were available + 3 added
        assertThat(book.getBorrowedCopiesCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should throw exception when adding zero or negative copies")
    void shouldThrowExceptionWhenAddingInvalidCopies() {
        assertThatThrownBy(() -> book.addCopies(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be at least 1");

        assertThatThrownBy(() -> book.addCopies(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should remove copies successfully")
    void shouldRemoveCopiesSuccessfully() {
        book.removeCopies(2);

        assertThat(book.getTotalCopies()).isEqualTo(3);
        assertThat(book.getAvailableCopies()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should throw exception when removing borrowed copies")
    void shouldThrowExceptionWhenRemovingBorrowedCopies() {
        book.borrowCopy();
        book.borrowCopy();
        book.borrowCopy();

        assertThatThrownBy(() -> book.removeCopies(3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("3 copies are currently borrowed");
    }

    @Test
    @DisplayName("Should update book details successfully")
    void shouldUpdateBookDetails() {
        book.updateDetails(
                "Clean Code: Updated",
                "Robert C. Martin",
                "Software Engineering",
                "New Publisher",
                2020
        );

        assertThat(book.getTitle()).isEqualTo("Clean Code: Updated");
        assertThat(book.getGenre()).isEqualTo("Software Engineering");
        assertThat(book.getPublisher()).isEqualTo("New Publisher");
        assertThat(book.getPublicationYear()).isEqualTo(2020);
        assertThat(book.getTotalCopies()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should maintain invariants after reconstruction")
    void shouldMaintainInvariantsAfterReconstruction() {
        assertThatThrownBy(() -> new Book(
                isbn,
                "Title",
                "Author",
                "Genre",
                "Publisher",
                2020,
                5,
                6
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Available copies")
                .hasMessageContaining("cannot exceed total copies");
    }

    @Test
    @DisplayName("Should be equal when ISBNs are the same")
    void shouldBeEqualWhenIsbnsAreSame() {
        Book book2 = new Book(
                isbn,
                "Different Title",
                "Different Author",
                "Different Genre",
                "Different Publisher",
                2021,
                10
        );

        assertThat(book).isEqualTo(book2);
        assertThat(book.hashCode()).isEqualTo(book2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when ISBNs are different")
    void shouldNotBeEqualWhenIsbnsAreDifferent() {
        ISBN differentIsbn = new ISBN("9780306406158");
        Book book2 = new Book(
                differentIsbn,
                "Clean Code",
                "Robert C. Martin",
                "Programming",
                "Prentice Hall",
                2008,
                5
        );

        assertThat(book).isNotEqualTo(book2);
    }
}