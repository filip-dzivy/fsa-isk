package sk.posam.fsa.isk.domain.announcement.service;

import sk.posam.fsa.isk.domain.announcement.Announcement;
import sk.posam.fsa.isk.domain.announcement.AnnouncementPhoto;
import sk.posam.fsa.isk.domain.announcement.AnnouncementRepository;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.domain.shared.PhotoStoragePort;
import sk.posam.fsa.isk.domain.shared.Transactional;

import java.util.Comparator;
import java.util.List;

public class AnnouncementService implements AnnouncementFacade {

    private final AnnouncementRepository announcementRepository;
    private final PhotoStoragePort photoStoragePort;

    public AnnouncementService(AnnouncementRepository announcementRepository,
                               PhotoStoragePort photoStoragePort) {
        this.announcementRepository = announcementRepository;
        this.photoStoragePort = photoStoragePort;
    }

    @Override
    @Transactional
    public Announcement create(String title, String content, Member author) {
        Announcement announcement = new Announcement(title, content, author);
        return announcementRepository.save(announcement);
    }

    @Override
    @Transactional
    public Announcement update(long id, String title, String content) {
        Announcement announcement = find(id);
        announcement.update(title, content);
        return announcementRepository.save(announcement);
    }

    @Override
    @Transactional(readOnly = true)
    public Announcement find(long id) {
        return announcementRepository.findWithPhotos(id)
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Oznam s ID " + id + " neexistuje."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Announcement> findAll() {
        return announcementRepository.findAllWithPhotos().stream()
                .sorted(Comparator.comparing(Announcement::getPublishedAt).reversed())
                .toList();
    }

    @Override
    @Transactional
    public void delete(long id) {
        Announcement announcement = find(id);
        for (AnnouncementPhoto photo : announcement.getPhotos()) {
            photoStoragePort.delete(photo.getStorageKey());
        }
        announcementRepository.delete(announcement);
    }

    @Override
    @Transactional
    public AnnouncementPhoto addPhoto(long announcementId, byte[] bytes, String contentType,
                                      String originalFilename, String caption) {
        Announcement announcement = find(announcementId);
        PhotoStoragePort.StoredPhoto stored = photoStoragePort.upload(bytes, contentType, originalFilename);
        AnnouncementPhoto photo;
        try {
            photo = announcement.addPhoto(stored.url(), stored.storageKey(), caption);
        } catch (RuntimeException ex) {
            // Roll back the upload if the domain refused to accept it
            photoStoragePort.delete(stored.storageKey());
            throw ex;
        }
        announcementRepository.save(announcement);
        return photo;
    }

    @Override
    @Transactional
    public void removePhoto(long announcementId, long photoId) {
        Announcement announcement = find(announcementId);
        AnnouncementPhoto removed = announcement.removePhoto(photoId);
        announcementRepository.save(announcement);
        photoStoragePort.delete(removed.getStorageKey());
    }
}