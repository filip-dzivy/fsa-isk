package sk.isk.domain.catalog.predicate;

import java.time.Year;
import java.util.function.Predicate;

public class HasValidPublicationYearPredicate implements Predicate<Year> {
    public static final HasValidPublicationYearPredicate INSTANCE = new HasValidPublicationYearPredicate();

    private static final Year EARLIEST = Year.of(1500);
    private HasValidPublicationYearPredicate(){};

    @Override
    public boolean test(Year publicationYear){
        return !(publicationYear == null)
            && publicationYear.isAfter(EARLIEST)
            && !publicationYear.isAfter(Year.now());
    }
}
