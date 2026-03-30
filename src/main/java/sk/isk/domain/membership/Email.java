package sk.isk.domain.membership;

import sk.isk.domain.membership.predicate.HasCorrectEmailFormatPredicate;
import sk.isk.domain.shared.DomainException;

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
    public String toString() {
        return value;
    }
}