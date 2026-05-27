package sk.posam.fsa.isk.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.BookPhoto;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.catalog.query.BookView;
import sk.posam.fsa.isk.rest.dto.BookDto;
import sk.posam.fsa.isk.rest.dto.BookGenreDto;
import sk.posam.fsa.isk.rest.dto.BookPhotoDto;
import sk.posam.fsa.isk.rest.dto.CreateBookRequestDto;

import java.time.Year;
import java.util.Collection;
import java.util.List;

@Component
public class BookMapper {

    public BookDto toDto(BookView view) {
        if (view == null) return null;
        return fillBaseFields(view.book(), view.reservedCopies(), view.book().getPhotos());
    }

    public List<BookDto> toDto(Collection<BookView> views) {
        if (views == null || views.isEmpty()) return List.of();
        return views.stream().map(this::toDto).toList();
    }

    public BookDto toDtoBasic(Book entity) {
        if (entity == null) return null;
        return fillBaseFields(entity, 0, List.of());
    }

    private BookDto fillBaseFields(Book entity, int reservedCopies, List<BookPhoto> photos) {
        BookDto dto = new BookDto();
        dto.setIsbn(entity.getIsbn() != null ? entity.getIsbn().getValue() : null);
        dto.setTitle(entity.getTitle());
        dto.setAuthor(entity.getAuthor());
        dto.setGenre(BookGenreDto.valueOf(entity.getGenre().name()));
        dto.setPublisher(entity.getPublisher());
        dto.setPublicationYear(entity.getPublicationYear().getValue());
        dto.setTotalCopies(entity.getTotalCopies());
        dto.setAvailableCopies(entity.getAvailableCopies());
        dto.setReservedCopies(reservedCopies);
        dto.setDescription(entity.getDescription());
        dto.setPhotos(photos.stream().map(this::toPhotoDto).toList());
        return dto;
    }

    public BookPhotoDto toPhotoDto(BookPhoto photo) {
        if (photo == null) return null;
        BookPhotoDto dto = new BookPhotoDto();
        dto.setId(photo.getId());
        dto.setUrl(photo.getUrl());
        dto.setCaption(photo.getCaption());
        dto.setPosition(photo.getPosition());
        return dto;
    }

    public Book toBook(CreateBookRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Book book = new Book(
                new ISBN(dto.getIsbn()),
                dto.getTitle(),
                dto.getAuthor(),
                BookGenre.valueOf(dto.getGenre().name()),
                dto.getPublisher(),
                Year.of(dto.getPublicationYear()),
                dto.getTotalCopies()
        );
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            book.updateDescription(dto.getDescription());
        }
        return book;
    }
}