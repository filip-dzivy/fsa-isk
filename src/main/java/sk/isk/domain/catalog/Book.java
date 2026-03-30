package sk.isk.domain.catalog;
import sk.isk.domain.catalog.predicate.*;
import sk.isk.domain.shared.DomainException;

import java.time.Year;
import java.util.Objects;

public class Book {

    private final ISBN isbn;
    private String title;
    private String author;
    private BookGenre genre;
    private String publisher;
    private Year publicationYear;
    private int totalCopies;
    private int availableCopies;

    //JPA
    public Book(){this.isbn = null;}

    public Book(
            ISBN isbn,
            String title,
            String author,
            BookGenre genre,
            String publisher,
            Year publicationYear,
            int totalCopies
    ){
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = Objects.requireNonNullElse(genre, BookGenre.OTHER);
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        validateForCreation();
        this.availableCopies = totalCopies;
    }

    public void addCopies(int count){
        require(HasPositiveCopyCountPredicate.INSTANCE.test(count),
                DomainException.Type.VALIDATION,
                "Počet pridávaných kópii musí byť kladný");
        totalCopies += count;
        availableCopies += count;
    }

    public void borrowCopy(){
        require(HasAvailableCopyPredicate.INSTANCE.test(this),
                DomainException.Type.CONFLICT,
                "Kniha" + isbn + "nie je dostupná na vypožičanie.");
        availableCopies--;
    }

    public void returnCopy(){
        require(availableCopies < totalCopies,
                DomainException.Type.CONFLICT,
                "Počet dostupných kópií by presiahol celkový počet.");
        availableCopies++;
    }

    public boolean isAvailable(){
        return availableCopies > 0;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getTotalCopies(){
        return totalCopies;
    }

    public void validateForCreation() {
        require(HasRequiredISBNPredicate.INSTANCE.test(isbn),
                DomainException.Type.VALIDATION,
                "ISBN je povinný údaj.");
        require(HasRequiredTitlePredicate.INSTANCE.test(title),
                DomainException.Type.VALIDATION,
                "Názov knihy je povinný údaj.");
        require(HasRequiredAuthorPredicate.INSTANCE.test(author),
                DomainException.Type.VALIDATION,
                "Autor knihy je povinný údaj.");
        require(HasPositiveCopyCountPredicate.INSTANCE.test(totalCopies),
                DomainException.Type.VALIDATION,
                "Počet kópií musí byť aspoň 1.");
        require(HasValidPublisherPredicate.INSTANCE.test(publisher),
                DomainException.Type.VALIDATION,
                "Vydavateľ je povinný údaj.");
        require(HasValidPublicationYearPredicate.INSTANCE.test(publicationYear),
                DomainException.Type.VALIDATION,
                "Rok vydania je povinný údaj a musí byť validný");

    }

    private void require(boolean valid, DomainException.Type type, String message) {
        if (!valid) throw new DomainException(type, message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn=" + isbn.getValue() +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", available=" + availableCopies + "/" + totalCopies +
                '}';
    }
}