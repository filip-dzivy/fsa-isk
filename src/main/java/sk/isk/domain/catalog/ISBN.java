// src/main/java/sk/librasys/domain/catalog/ISBN.java
package sk.isk.domain.catalog;

import sk.isk.domain.shared.ValueObject;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ISBN implements ValueObject {

    private static final Pattern ISBN_10_PATTERN = Pattern.compile("^\\d{9}[\\dX]$");
    private static final Pattern ISBN_13_PATTERN = Pattern.compile("^\\d{13}$");

    private final String value;

    public ISBN(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }

        String normalized = value.replaceAll("[\\s-]", "");

        if (!isValid(normalized)) {
            throw new IllegalArgumentException("Invalid ISBN format: " + value);
        }

        this.value = normalized;
    }

    private boolean isValid(String isbn) {
        return ISBN_10_PATTERN.matcher(isbn).matches()
                || ISBN_13_PATTERN.matcher(isbn).matches();
    }

    public String getValue() {
        return value;
    }

    public String getFormatted() {
        if (value.length() == 10) {
            return String.format("%s-%s-%s-%s",
                    value.substring(0, 1),
                    value.substring(1, 5),
                    value.substring(5, 9),
                    value.substring(9));
        } else {
            return String.format("%s-%s-%s-%s-%s",
                    value.substring(0, 3),
                    value.substring(3, 4),
                    value.substring(4, 9),
                    value.substring(9, 12),
                    value.substring(12));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ISBN isbn = (ISBN) obj;
        return Objects.equals(value, isbn.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}