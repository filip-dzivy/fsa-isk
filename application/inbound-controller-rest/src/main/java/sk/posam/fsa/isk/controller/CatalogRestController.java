package sk.posam.fsa.isk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.BookPhoto;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.catalog.service.CatalogFacade;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.domain.shared.Transactional;
import sk.posam.fsa.isk.mapper.BookMapper;
import sk.posam.fsa.isk.rest.api.BooksApi;
import sk.posam.fsa.isk.rest.dto.AddCopiesRequestDto;
import sk.posam.fsa.isk.rest.dto.BookDto;
import sk.posam.fsa.isk.rest.dto.BookGenreDto;
import sk.posam.fsa.isk.rest.dto.BookPhotoDto;
import sk.posam.fsa.isk.rest.dto.CreateBookRequestDto;
import sk.posam.fsa.isk.rest.dto.UpdateBookDescriptionRequestDto;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
public class CatalogRestController implements BooksApi {

    private static final Set<String> ALLOWED_PHOTO_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");
    private static final long MAX_PHOTO_SIZE_BYTES = 5L * 1024 * 1024;

    private final CatalogFacade catalogFacade;
    private final BookMapper bookMapper;

    public CatalogRestController(CatalogFacade catalogFacade, BookMapper bookMapper) {
        this.catalogFacade = catalogFacade;
        this.bookMapper = bookMapper;
    }


    @Override
    @Transactional
    public ResponseEntity<Void> createBook(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toBook(createBookRequestDto);
        catalogFacade.create(book);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<BookDto>> getAllBooks(String title, String author, BookGenreDto genre) {
        BookGenre bookGenre = genre != null ? BookGenre.valueOf(genre.name()) : null;
        return ResponseEntity.ok(bookMapper.toDto(catalogFacade.search(title, author, bookGenre)));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<BookDto> getBookByIsbn(String isbn) {
        Book book = catalogFacade.find(new ISBN(isbn));
        return ResponseEntity.ok(bookMapper.toDto(book));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteBook(String isbn) {
        catalogFacade.delete(new ISBN(isbn));
        return ResponseEntity.noContent().build();
    }

    @Override
    @Transactional
    public ResponseEntity<BookDto> addBookCopies(String isbn, AddCopiesRequestDto request) {
        Book book = catalogFacade.addCopies(new ISBN(isbn), request.getCount());
        return ResponseEntity.ok(bookMapper.toDto(book));
    }

    @Override
    @Transactional
    public ResponseEntity<BookDto> updateBookDescription(String isbn, UpdateBookDescriptionRequestDto request) {
        Book book = catalogFacade.updateDescription(new ISBN(isbn), request.getDescription());
        return ResponseEntity.ok(bookMapper.toDto(book));
    }

    @Override
    @Transactional
    public ResponseEntity<BookPhotoDto> uploadBookPhoto(String isbn, MultipartFile file, String caption) {
        if (file == null || file.isEmpty()) {
            throw new DomainException(DomainException.Type.VALIDATION, "Súbor je povinný.");
        }
        if (file.getSize() > MAX_PHOTO_SIZE_BYTES) {
            throw new DomainException(DomainException.Type.VALIDATION, "Maximálna veľkosť fotky je 5 MB.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_PHOTO_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new DomainException(DomainException.Type.VALIDATION,
                    "Povolené sú len JPG, PNG, WebP a GIF.");
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new DomainException(DomainException.Type.VALIDATION, "Súbor sa nepodarilo prečítať.");
        }
        BookPhoto photo = catalogFacade.addPhoto(
                new ISBN(isbn), bytes, contentType, file.getOriginalFilename(), caption);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookMapper.toPhotoDto(photo));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteBookPhoto(String isbn, Long photoId) {
        catalogFacade.removePhoto(new ISBN(isbn), photoId);
        return ResponseEntity.noContent().build();
    }
}
