package sk.posam.fsa.isk.domain.member;

import org.junit.jupiter.api.Test;
import sk.posam.fsa.isk.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.*;

public class EmailTest {

    @Test
    void validEmailIsAccepted() {
        Email e = new Email("jan.novak@example.sk");
        assertEquals("jan.novak@example.sk", e.toString());
    }

    @Test
    void nullEmailThrows() {
        assertThrows(DomainException.class, () -> new Email(null));
    }

    @Test
    void blankEmailThrows() {
        assertThrows(DomainException.class, () -> new Email("   "));
    }

    @Test
    void emailWithoutAtSignThrows() {
        assertThrows(DomainException.class, () -> new Email("jan.novak.example.sk"));
    }

    @Test
    void emailWithoutDotInDomainThrows() {
        assertThrows(DomainException.class, () -> new Email("jan@example"));
    }

    @Test
    void emailWithSpaceThrows() {
        assertThrows(DomainException.class, () -> new Email("jan novak@example.sk"));
    }

    @Test
    void emailWithMultipleAtSignsThrows() {
        assertThrows(DomainException.class, () -> new Email("jan@@example.sk"));
    }

    @Test
    void equalityIsValueBased() {
        Email a = new Email("jan.novak@example.sk");
        Email b = new Email("jan.novak@example.sk");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void differentEmailsAreNotEqual() {
        Email a = new Email("jan.novak@example.sk");
        Email b = new Email("eva.kovacova@example.sk");
        assertNotEquals(a, b);
    }
}
