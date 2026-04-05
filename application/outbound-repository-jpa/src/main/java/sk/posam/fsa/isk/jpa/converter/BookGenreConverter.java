package sk.posam.fsa.isk.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sk.posam.fsa.isk.domain.catalog.BookGenre;

@Converter
public class BookGenreConverter implements AttributeConverter<BookGenre, String> {
    @Override
    public String convertToDatabaseColumn(BookGenre a) { return a == null ? null : a.name(); }
    @Override
    public BookGenre convertToEntityAttribute(String d) { return d == null ? null : BookGenre.valueOf(d); }
}