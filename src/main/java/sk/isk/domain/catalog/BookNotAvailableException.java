// src/main/java/sk/librasys/domain/catalog/BookNotAvailableException.java
package sk.isk.domain.catalog;

public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(String message) {
        super(message);
    }
}