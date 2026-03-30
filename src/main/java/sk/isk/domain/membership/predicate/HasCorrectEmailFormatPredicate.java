package sk.isk.domain.membership.predicate;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class HasCorrectEmailFormatPredicate implements Predicate<String> {

    public static final HasCorrectEmailFormatPredicate INSTANCE = new HasCorrectEmailFormatPredicate();

    private static final Pattern PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private HasCorrectEmailFormatPredicate() {}

    @Override
    public boolean test(String email) {
        return email != null && PATTERN.matcher(email).matches();
    }
}
