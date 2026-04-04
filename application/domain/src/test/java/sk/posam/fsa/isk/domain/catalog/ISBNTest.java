package sk.posam.fsa.isk.domain.catalog;

import org.junit.jupiter.api.Test;
import sk.posam.fsa.isk.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ISBNTest {

    @Test
    void validIsbn13IsAccepted() {
        ISBN isbn = new ISBN("9780306406157");
        assertEquals("9780306406157", isbn.getValue());
    }

    @Test
    void validIsbn13WithDashesIsNormalized() {
        ISBN isbn = new ISBN("978-0-306-40615-7");
        assertEquals("9780306406157", isbn.getValue());
    }

    @Test
    void validIsbn10IsAccepted() {
        ISBN isbn = new ISBN("0306406152");
        assertEquals("0306406152", isbn.getValue());
    }

    @Test
    void invalidIsbnThrows() {
        assertThrows(DomainException.class, () -> new ISBN("1234567890123"));
    }

    @Test
    void blankIsbnThrows() {
        assertThrows(DomainException.class, () -> new ISBN("  "));
    }

    @Test
    void nullIsbnThrows() {
        assertThrows(DomainException.class, () -> new ISBN(null));
    }

    @Test
    void equalityBasedOnValue() {
        ISBN a = new ISBN("9780306406157");
        ISBN b = new ISBN("978-0-306-40615-7");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}