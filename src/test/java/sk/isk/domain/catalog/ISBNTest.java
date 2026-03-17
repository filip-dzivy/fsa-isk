// src/test/java/sk/librasys/domain/catalog/ISBNTest.java
package sk.isk.domain.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ISBN Value Object Tests")
class ISBNTest {

    @Test
    @DisplayName("Should create valid ISBN-10")
    void shouldCreateValidIsbn10() {
        String isbnString = "0306406152";

        ISBN isbn = new ISBN(isbnString);

        assertThat(isbn.getValue()).isEqualTo("0306406152");
    }

    @Test
    @DisplayName("Should create valid ISBN-13")
    void shouldCreateValidIsbn13() {
        String isbnString = "9780306406157";

        ISBN isbn = new ISBN(isbnString);

        assertThat(isbn.getValue()).isEqualTo("9780306406157");
    }

    @Test
    @DisplayName("Should normalize ISBN by removing spaces and hyphens")
    void shouldNormalizeIsbn() {
        String isbnWithFormatting = "978-0-306-40615-7";

        ISBN isbn = new ISBN(isbnWithFormatting);

        assertThat(isbn.getValue()).isEqualTo("9780306406157");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "123",
            "12345678901234",
            "abc123456789",
            "978-0-306-4061"
    })
    @DisplayName("Should throw exception for invalid ISBN formats")
    void shouldThrowExceptionForInvalidFormats(String invalidIsbn) {
        assertThatThrownBy(() -> new ISBN(invalidIsbn))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ISBN format");
    }

    @Test
    @DisplayName("Should throw exception for null ISBN")
    void shouldThrowExceptionForNullIsbn() {
        assertThatThrownBy(() -> new ISBN(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ISBN cannot be null");
    }

    @Test
    @DisplayName("Should format ISBN-10 correctly")
    void shouldFormatIsbn10() {
        ISBN isbn = new ISBN("0306406152");

        String formatted = isbn.getFormatted();

        assertThat(formatted).isEqualTo("0-3064-0615-2");
    }

    @Test
    @DisplayName("Should format ISBN-13 correctly")
    void shouldFormatIsbn13() {
        ISBN isbn = new ISBN("9780306406157");

        String formatted = isbn.getFormatted();

        assertThat(formatted).isEqualTo("978-0-3064-0615-7");
    }

    @Test
    @DisplayName("Should be equal when ISBN values are the same")
    void shouldBeEqualWhenValuesAreSame() {
        ISBN isbn1 = new ISBN("9780306406157");
        ISBN isbn2 = new ISBN("978-0-306-40615-7"); // with formatting

        assertThat(isbn1).isEqualTo(isbn2);
        assertThat(isbn1.hashCode()).isEqualTo(isbn2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when ISBN values are different")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        ISBN isbn1 = new ISBN("9780306406157");
        ISBN isbn2 = new ISBN("9780306406158");

        assertThat(isbn1).isNotEqualTo(isbn2);
    }
}