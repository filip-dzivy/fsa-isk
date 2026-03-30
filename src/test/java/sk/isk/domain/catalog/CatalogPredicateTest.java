package sk.isk.domain.catalog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.isk.domain.catalog.predicate.*;

import java.time.Year;

public class CatalogPredicateTest {

    private static final ISBN ISBN_VALID = new ISBN("9780306406157");

    @Test
    void hasValidPublisher_trueForNonBlank() {
        Assertions.assertTrue(HasValidPublisherPredicate.INSTANCE.test("Prentice Hall"));
    }

    @Test
    void hasValidPublisher_falseForBlank() {
        Assertions.assertFalse(HasValidPublisherPredicate.INSTANCE.test("   "));
    }

    @Test
    void hasValidPublisher_falseForEmpty() {
        Assertions.assertFalse(HasValidPublisherPredicate.INSTANCE.test(""));
    }

    @Test
    void hasValidPublisher_falseForNull() {
        Assertions.assertFalse(HasValidPublisherPredicate.INSTANCE.test(null));
    }

    @Test
    void hasRequiredISBN_trueForValidIsbn13() {
        ISBN isbn = new ISBN("9780306406157");
        Assertions.assertTrue(HasRequiredISBNPredicate.INSTANCE.test(isbn));
    }

    @Test
    void hasRequiredISBN_trueForValidIsbn10() {
        ISBN isbn = new ISBN("0306406152");
        Assertions.assertTrue(HasRequiredISBNPredicate.INSTANCE.test(isbn));
    }

    @Test
    void hasRequiredISBN_falseForNull() {
        Assertions.assertFalse(HasRequiredISBNPredicate.INSTANCE.test(null));
    }

    @Test
    void hasValidPublicationYear_trueForRecentYear() {
        Assertions.assertTrue(HasValidPublicationYearPredicate.INSTANCE.test(Year.of(2008)));
    }

    @Test
    void hasValidPublicationYear_trueForEarliestBoundary() {
        Assertions.assertTrue(HasValidPublicationYearPredicate.INSTANCE.test(Year.of(1501)));
    }

    @Test
    void hasValidPublicationYear_falseForYearBeforeEarliest() {
        Assertions.assertFalse(HasValidPublicationYearPredicate.INSTANCE.test(Year.of(1499)));
    }

    @Test
    void hasValidPublicationYear_falseForFutureYear() {
        Assertions.assertFalse(HasValidPublicationYearPredicate.INSTANCE.test(Year.now().plusYears(1)));
    }

    @Test
    void hasValidPublicationYear_falseForNull() {
        Assertions.assertFalse(HasValidPublicationYearPredicate.INSTANCE.test(null));
    }

    @Test
    void hasAvailableCopy_trueWhenCopiesRemain() {
        Book book = new Book(ISBN_VALID, "bookA", "bookA", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008),2);
        Assertions.assertTrue(HasAvailableCopyPredicate.INSTANCE.test(book));
    }

    @Test
    void hasAvailableCopy_falseWhenAllBorrowed() {
        Book book = new Book(ISBN_VALID, "bookA", "bookA", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008),1);
        book.borrowCopy();
        Assertions.assertFalse(HasAvailableCopyPredicate.INSTANCE.test(book));
    }

    @Test
    void hasAvailableCopy_falseForNull() {
        Assertions.assertFalse(HasAvailableCopyPredicate.INSTANCE.test(null));
    }

    @Test
    void hasRequiredTitle_trueForNonBlankTitle() {
        Assertions.assertTrue(HasRequiredTitlePredicate.INSTANCE.test("Clean Code"));
    }

    @Test
    void hasRequiredTitle_falseForBlank() {
        Assertions.assertFalse(HasRequiredTitlePredicate.INSTANCE.test("  "));
    }

    @Test
    void hasRequiredTitle_falseForNull() {
        Assertions.assertFalse(HasRequiredTitlePredicate.INSTANCE.test(null));
    }

    @Test
    void hasRequiredAuthor_trueForNonBlank() {
        Assertions.assertTrue(HasRequiredAuthorPredicate.INSTANCE.test("Robert C. Martin"));
    }

    @Test
    void hasRequiredAuthor_falseForEmpty() {
        Assertions.assertFalse(HasRequiredAuthorPredicate.INSTANCE.test(""));
    }

    @Test
    void hasPositiveCopyCount_trueForPositive() {
        Assertions.assertTrue(HasPositiveCopyCountPredicate.INSTANCE.test(1));
        Assertions.assertTrue(HasPositiveCopyCountPredicate.INSTANCE.test(100));
    }

    @Test
     void hasPositiveCopyCount_falseForZero() {
        Assertions.assertFalse(HasPositiveCopyCountPredicate.INSTANCE.test(0));
    }

    @Test
    void hasPositiveCopyCount_falseForNegative() {
        Assertions.assertFalse(HasPositiveCopyCountPredicate.INSTANCE.test(-1));
    }

    @Test
    void hasPositiveCopyCount_falseForNull() {
        Assertions.assertFalse(HasPositiveCopyCountPredicate.INSTANCE.test(null));
    }
}
