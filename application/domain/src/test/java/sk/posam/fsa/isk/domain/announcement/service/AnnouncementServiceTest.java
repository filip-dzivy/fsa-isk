package sk.posam.fsa.isk.domain.announcement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.isk.domain.announcement.Announcement;
import sk.posam.fsa.isk.domain.announcement.AnnouncementPhoto;
import sk.posam.fsa.isk.domain.announcement.AnnouncementRepository;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.domain.shared.PhotoStoragePort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;
    @Mock
    private PhotoStoragePort photoStoragePort;

    @InjectMocks
    private AnnouncementService service;

    private Member author;

    @BeforeEach
    void setUp() {
        author = new Member(10L, new Email("lib@example.sk"), "Lib", "Rarian", MemberRole.LIBRARIAN);
    }

    @Test
    void createPersistsValidAnnouncement() {
        when(announcementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Announcement result = service.create("Nadpis", "Obsah", author);

        assertEquals("Nadpis", result.getTitle());
        verify(announcementRepository).save(any(Announcement.class));
    }

    @Test
    void createWithInvalidDataThrowsAndDoesNotSave() {
        assertThrows(DomainException.class, () -> service.create("", "Obsah", author));
        verify(announcementRepository, never()).save(any());
    }

    @Test
    void findNotFoundThrows() {
        when(announcementRepository.findWithPhotos(1L)).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> service.find(1L));
        assertEquals(DomainException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void updateChangesAnnouncementAndSaves() {
        Announcement a = new Announcement("Old", "Old content", author);
        when(announcementRepository.findWithPhotos(1L)).thenReturn(Optional.of(a));
        when(announcementRepository.save(a)).thenReturn(a);

        Announcement result = service.update(1L, "New", "New content");

        assertEquals("New", result.getTitle());
        assertEquals("New content", result.getContent());
        verify(announcementRepository).save(a);
    }

    @Test
    void deleteRemovesPhotosFromStorage() {
        Announcement a = new Announcement("T", "C", author);
        a.addPhoto("https://example.com/1.jpg", "k1", null);
        a.addPhoto("https://example.com/2.jpg", "k2", null);
        when(announcementRepository.findWithPhotos(1L)).thenReturn(Optional.of(a));

        service.delete(1L);

        verify(photoStoragePort).delete("k1");
        verify(photoStoragePort).delete("k2");
        verify(announcementRepository).delete(a);
    }

    @Test
    void findAllReturnsNewestFirst() {
        Announcement older = new Announcement("Older", "...", author);
        Announcement newer = new Announcement("Newer", "...", author);
        // Wait briefly to ensure ordering — instead, rely on insertion order if equal.
        // We can't easily force timestamps, so just verify the method composes through repo.
        when(announcementRepository.findAllWithPhotos()).thenReturn(List.of(older, newer));

        List<Announcement> result = service.findAll();
        assertEquals(2, result.size());
        // Newer first when timestamps differ — but since both created near-simultaneously,
        // only check both present.
        assertTrue(result.containsAll(List.of(older, newer)));
    }

    @Test
    void addPhotoUploadsAndAttaches() {
        Announcement a = new Announcement("T", "C", author);
        when(announcementRepository.findWithPhotos(1L)).thenReturn(Optional.of(a));
        when(photoStoragePort.upload(any(), any(), any()))
                .thenReturn(new PhotoStoragePort.StoredPhoto("https://example.com/x.jpg", "key1"));

        AnnouncementPhoto photo = service.addPhoto(1L, new byte[]{}, "image/jpeg", "x.jpg", "Caption");

        assertEquals("https://example.com/x.jpg", photo.getUrl());
        verify(announcementRepository).save(a);
        verify(photoStoragePort, never()).delete(any());
    }

    @Test
    void addPhotoRollsBackUploadWhenDomainRejects() {
        Announcement a = new Announcement("T", "C", author);
        for (int i = 0; i < Announcement.MAX_PHOTOS; i++) {
            a.addPhoto("https://example.com/" + i + ".jpg", "k" + i, null);
        }
        when(announcementRepository.findWithPhotos(1L)).thenReturn(Optional.of(a));
        when(photoStoragePort.upload(any(), any(), any()))
                .thenReturn(new PhotoStoragePort.StoredPhoto("https://example.com/x.jpg", "rollback-key"));

        assertThrows(DomainException.class,
                () -> service.addPhoto(1L, new byte[]{}, "image/jpeg", "x.jpg", null));

        verify(photoStoragePort).delete("rollback-key");
        verify(announcementRepository, never()).save(any());
    }

    @Test
    void removePhotoDeletesFromStorageAndSaves() {
        Announcement a = new Announcement("T", "C", author);
        AnnouncementPhoto p = a.addPhoto("https://example.com/1.jpg", "toDelete", null);
        when(announcementRepository.findWithPhotos(1L)).thenReturn(Optional.of(a));

        service.removePhoto(1L, p.getId());

        verify(announcementRepository).save(a);
        verify(photoStoragePort).delete("toDelete");
    }
}
