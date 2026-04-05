package sk.posam.fsa.isk.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.reservation.Reservation;
import sk.posam.fsa.isk.rest.dto.BookDto;
import sk.posam.fsa.isk.rest.dto.BookGenreDto;
import sk.posam.fsa.isk.rest.dto.CreateBookRequestDto;
import sk.posam.fsa.isk.rest.dto.ReservationDto;

import java.time.Year;
import java.util.Collection;
import java.util.List;

@Component
public class BookMapper {

    public BookDto toDto(Book entity) {
        if (entity == null) {
            return null;
        }

        BookDto dto = new BookDto();
        dto.setIsbn(entity.getIsbn() != null ? entity.getIsbn().getValue() : null);
        dto.setTitle(entity.getTitle());
        dto.setAuthor(entity.getAuthor());
        dto.setGenre(BookGenreDto.valueOf(entity.getGenre().name()));
        dto.setPublisher(entity.getPublisher());
        dto.setPublicationYear(entity.getPublicationYear().getValue());
        dto.setTotalCopies(entity.getTotalCopies());
        dto.setAvailableCopies(entity.getAvailableCopies());
        return dto;
    }

    public Book toBook(CreateBookRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return new Book(
                new ISBN(dto.getIsbn()),
                dto.getTitle(),
                dto.getAuthor(),
                BookGenre.valueOf(dto.getGenre().name()),
                dto.getPublisher(),
                Year.of(dto.getPublicationYear()),
                dto.getTotalCopies()
        );
    }

    public List<BookDto> toDto(Collection<Book> entities) {
        return entities.stream().map(this::toDto).toList();
    }

}
