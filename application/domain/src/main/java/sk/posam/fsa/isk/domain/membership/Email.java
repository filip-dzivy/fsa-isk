package sk.posam.fsa.isk.domain.membership;

import sk.posam.fsa.isk.domain.membership.predicate.HasCorrectEmailFormatPredicate;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.util.Objects;

public final class Email {
    private String value;

    public Email() {};

    public Email(String value){
        require(HasCorrectEmailFormatPredicate.INSTANCE.test(value),
                DomainException.Type.VALIDATION,
                "Neplatný format e-mailu: " + value);
        this.value = value;
    }

    private void require(boolean valid, DomainException.Type type, String message) {
        if (!valid) throw new DomainException(type, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        return Objects.equals(value, ((Email) o).value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value; }
}