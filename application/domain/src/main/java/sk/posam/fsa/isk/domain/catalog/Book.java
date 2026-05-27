package sk.posam.fsa.isk.domain.catalog;
import sk.posam.fsa.isk.domain.catalog.predicate.*;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Book {

    public static final int MAX_PHOTOS = 5;
    public static final int MAX_DESCRIPTION_LENGTH = 2000;

    private Long id;
    private ISBN isbn;
    private String title;
    private String author;
    private BookGenre genre;
    private String publisher;
    private Year publicationYear;
    private int totalCopies;
    private int availableCopies;
    private String description;
    private List<BookPhoto> photos = new ArrayList<>();

    //JPA
    public Book(){}

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

    public void updateDescription(String newDescription) {
        if (newDescription != null && newDescription.length() > MAX_DESCRIPTION_LENGTH) {
            throw new DomainException(
                    DomainException.Type.VALIDATION,
                    "Popis môže mať najviac " + MAX_DESCRIPTION_LENGTH + " znakov.");
        }
        this.description = (newDescription == null || newDescription.isBlank()) ? null : newDescription;
    }

    public BookPhoto addPhoto(String url, String storageKey, String caption) {
        if (photos.size() >= MAX_PHOTOS) {
            throw new DomainException(
                    DomainException.Type.CONFLICT,
                    "Kniha môže mať najviac " + MAX_PHOTOS + " fotiek.");
        }
        BookPhoto photo = new BookPhoto(url, storageKey, caption, photos.size());
        photos.add(photo);
        return photo;
    }

    public BookPhoto removePhoto(long photoId) {
        BookPhoto removed = photos.stream()
                .filter(p -> p.getId() == photoId)
                .findFirst()
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Fotka s ID " + photoId + " nie je súčasťou knihy."));
        photos.remove(removed);
        for (int i = 0; i < photos.size(); i++) {
            photos.get(i).setPosition(i);
        }
        return removed;
    }

    public List<BookPhoto> getPhotos() {
        return photos.stream()
                .sorted(Comparator.comparingInt(BookPhoto::getPosition))
                .toList();
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {return title;}

    public String getAuthor() {return author;}

    public BookGenre getGenre() {return genre;}

    public String getPublisher() {return publisher;}

    public Year getPublicationYear() {return publicationYear;}

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getTotalCopies(){
        return totalCopies;
    }

    public Long getId() {return id;}

    public ISBN getIsbn() {return isbn;}

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