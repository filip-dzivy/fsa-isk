package sk.posam.fsa.isk.domain.announcement;

import java.util.Collection;
import java.util.Optional;

public interface AnnouncementRepository {

    Optional<Announcement> find(long id);

    Optional<Announcement> findWithPhotos(long id);

    Collection<Announcement> findAll();

    Collection<Announcement> findAllWithPhotos();

    Announcement save(Announcement announcement);

    void delete(Announcement announcement);
}