package sk.posam.fsa.isk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.catalog.service.CatalogFacade;
import sk.posam.fsa.isk.mapper.BookMapper;
import sk.posam.fsa.isk.rest.api.BooksApi;
import sk.posam.fsa.isk.rest.dto.BookDto;
import sk.posam.fsa.isk.rest.dto.BookGenreDto;
import sk.posam.fsa.isk.rest.dto.CreateBookRequestDto;

import java.util.List;

@RestController
public class CatalogRestController implements BooksApi {
    private final CatalogFacade catalogFacade;
    private final BookMapper bookMapper;

    public CatalogRestController(CatalogFacade catalogFacade, BookMapper bookMapper) {
        this.catalogFacade = catalogFacade;
        this.bookMapper = bookMapper;
    }


    @Override
    public ResponseEntity<Void> createBook(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toBook(createBookRequestDto);
        catalogFacade.create(book);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<List<BookDto>> getAllBooks(String title, String author, BookGenreDto genre) {
        BookGenre bookGenre = genre != null ? BookGenre.valueOf(genre.name()) : null;
        return ResponseEntity.ok(bookMapper.toDto(catalogFacade.search(title, author, bookGenre)));
    }

    @Override
    public ResponseEntity<BookDto> getBookByIsbn(String isbn) {
        Book book = catalogFacade.find(new ISBN(isbn));
        return ResponseEntity.ok(bookMapper.toDto(book));
    }
}
