package sk.isk.domain.catalog;

import sk.isk.domain.shared.DomainException;
import java.util.Objects;

public final class ISBN {

    private final String value;

    public ISBN(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("ISBN nesmie byť prázdne.");
        }
        String normalized = value.replaceAll("[\\s-]", "");
        if (!isValidIsbn10(normalized) && !isValidIsbn13(normalized)) {
            throw new DomainException("Neplatný formát ISBN: " + value);
        }
        this.value = normalized;
    }

    public String getValue() {
        return value;
    }

    public boolean validate() {
        String normalized = value.replaceAll("[\\s-]", "");
        return isValidIsbn10(normalized) || isValidIsbn13(normalized);
    }

    private static boolean isValidIsbn10(String s) {
        if (s.length() != 10) return false;
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
            sum += (s.charAt(i) - '0') * (10 - i);
        }
        char last = s.charAt(9);
        sum += (last == 'X' || last == 'x') ? 10 : (last - '0');
        return sum % 11 == 0;
    }

    private static boolean isValidIsbn13(String s) {
        if (s.length() != 13) return false;
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
            int digit = s.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        return sum % 10 == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ISBN)) return false;
        return value.equals(((ISBN) o).value);
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
