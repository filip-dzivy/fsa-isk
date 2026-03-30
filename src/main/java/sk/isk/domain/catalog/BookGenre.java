package sk.isk.domain.catalog;

public enum BookGenre {

    // Fiction
    FICTION("Fiction"),
    SCIENCE_FICTION("Science Fiction"),
    FANTASY("Fantasy"),
    MYSTERY("Mystery"),
    THRILLER("Thriller"),
    HORROR("Horror"),
    ROMANCE("Romance"),
    HISTORICAL_FICTION("Historical Fiction"),
    ADVENTURE("Adventure"),
    NOVEL("Novel"),

    // Non-fiction
    BIOGRAPHY("Biography"),
    AUTOBIOGRAPHY("Autobiography"),
    HISTORY("History"),
    SCIENCE("Science"),
    TECHNOLOGY("Technology"),
    PHILOSOPHY("Philosophy"),
    PSYCHOLOGY("Psychology"),
    SELF_HELP("Self Help"),
    BUSINESS("Business"),
    POLITICS("Politics"),
    TRUE_CRIME("True Crime"),

    // Other
    POETRY("Poetry"),
    DRAMA("Drama"),
    CHILDREN("Children"),
    YOUNG_ADULT("Young Adult"),
    REFERENCE("Reference"),
    TRAVEL("Travel"),
    COOKBOOK("Cookbook"),
    ART("Art"),
    RELIGION("Religion"),
    LANGUAGE("Language"),
    OTHER("Other");

    private final String label;

    BookGenre(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
