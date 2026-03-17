// src/main/java/sk/librasys/domain/catalog/Book.java
package sk.isk.domain.catalog;

import sk.isk.domain.shared.AggregateRoot;

public class Book implements AggregateRoot<ISBN> {

    private final ISBN isbn;
    private String title;
    private String author;
    private String genre;
    private String publisher;
    private int publicationYear;
    private int totalCopies;
    private int availableCopies;

    public Book(
            ISBN isbn,
            String title,
            String author,
            String genre,
            String publisher,
            int publicationYear,
            int totalCopies
    ) {
        if (isbn == null) {
            throw new IllegalArgumentException("ISBN cannot be null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty");
        }
        if (totalCopies < 1) {
            throw new IllegalArgumentException("Total copies must be at least 1");
        }

        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public Book(
            ISBN isbn,
            String title,
            String author,
            String genre,
            String publisher,
            int publicationYear,
            int totalCopies,
            int availableCopies
    ) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;

        validateInvariants();
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void borrowCopy() {
        if (!isAvailable()) {
            throw new BookNotAvailableException(
                    "Book '" + title + "' (ISBN: " + isbn.getValue() + ") is not available. " +
                            "Available: " + availableCopies + "/" + totalCopies
            );
        }
        this.availableCopies--;
    }
    public void returnCopy() {
        if (availableCopies >= totalCopies) {
            throw new IllegalStateException(
                    "Cannot return copy: all copies are already available (" +
                            totalCopies + "/" + totalCopies + ")"
            );
        }
        this.availableCopies++;
    }

    public void addCopies(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Number of copies to add must be at least 1");
        }
        this.totalCopies += count;
        this.availableCopies += count;
    }

    public void removeCopies(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Number of copies to remove must be at least 1");
        }

        int borrowedCopies = totalCopies - availableCopies;

        if (totalCopies - count < borrowedCopies) {
            throw new IllegalArgumentException(
                    "Cannot remove " + count + " copies: " + borrowedCopies +
                            " copies are currently borrowed"
            );
        }

        this.totalCopies -= count;
        this.availableCopies -= count;
    }

    public int getBorrowedCopiesCount() {
        return totalCopies - availableCopies;
    }

    public void updateDetails(
            String title,
            String author,
            String genre,
            String publisher,
            int publicationYear
    ) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty");
        }

        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
    }

    private void validateInvariants() {
        if (availableCopies < 0) {
            throw new IllegalStateException("Available copies cannot be negative");
        }
        if (availableCopies > totalCopies) {
            throw new IllegalStateException(
                    "Available copies (" + availableCopies + ") cannot exceed total copies (" +
                            totalCopies + ")"
            );
        }
    }

    @Override
    public ISBN getId() {
        return isbn;
    }

    public ISBN getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
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