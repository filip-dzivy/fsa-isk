package sk.posam.fsa.isk.domain.announcement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.shared.DomainException;

import static org.junit.jupiter.api.Assertions.*;

public class AnnouncementTest {

    private Member author;

    @BeforeEach
    void setUp() {
        author = new Member(10L, new Email("lib@example.sk"), "Lib", "Rarian", MemberRole.LIBRARIAN);
    }

    @Test
    void newAnnouncementCarriesProvidedData() {
        Announcement a = new Announcement("Otváracie hodiny", "Knižnica je otvorená...", author);
        assertEquals("Otváracie hodiny", a.getTitle());
        assertEquals("Knižnica je otvorená...", a.getContent());
        assertEquals(author, a.getAuthor());
        assertNotNull(a.getPublishedAt());
    }

    @Test
    void blankTitleThrows() {
        assertThrows(DomainException.class,
                () -> new Announcement(" ", "obsah", author));
    }

    @Test
    void nullTitleThrows() {
        assertThrows(DomainException.class,
                () -> new Announcement(null, "obsah", author));
    }

    @Test
    void blankContentThrows() {
        assertThrows(DomainException.class,
                () -> new Announcement("Nadpis", "  ", author));
    }

    @Test
    void nullAuthorThrows() {
        assertThrows(DomainException.class,
                () -> new Announcement("Nadpis", "obsah", null));
    }

    @Test
    void updateChangesTitleAndContent() {
        Announcement a = new Announcement("Old", "Old content", author);
        a.update("New", "New content");
        assertEquals("New", a.getTitle());
        assertEquals("New content", a.getContent());
    }

    @Test
    void updateValidatesNewValues() {
        Announcement a = new Announcement("Old", "Old content", author);
        assertThrows(DomainException.class, () -> a.update("", "New content"));
        assertThrows(DomainException.class, () -> a.update("New", ""));
    }

    @Test
    void addPhotoAssignsPositionsInOrder() {
        Announcement a = new Announcement("T", "C", author);
        AnnouncementPhoto p1 = a.addPhoto("https://example.com/1.jpg", "k1", null);
        AnnouncementPhoto p2 = a.addPhoto("https://example.com/2.jpg", "k2", null);
        assertEquals(0, p1.getPosition());
        assertEquals(1, p2.getPosition());
        assertEquals(2, a.getPhotos().size());
    }

    @Test
    void addPhotoOverLimitThrows() {
        Announcement a = new Announcement("T", "C", author);
        for (int i = 0; i < Announcement.MAX_PHOTOS; i++) {
            a.addPhoto("https://example.com/" + i + ".jpg", "k" + i, null);
        }
        DomainException ex = assertThrows(DomainException.class,
                () -> a.addPhoto("https://example.com/x.jpg", "kx", null));
        assertEquals(DomainException.Type.CONFLICT, ex.getType());
    }

    @Test
    void removePhotoNotFoundThrows() {
        Announcement a = new Announcement("T", "C", author);
        a.addPhoto("https://example.com/1.jpg", "k1", null);
        DomainException ex = assertThrows(DomainException.class, () -> a.removePhoto(999L));
        assertEquals(DomainException.Type.NOT_FOUND, ex.getType());
    }
}
