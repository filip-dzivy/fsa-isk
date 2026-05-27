package sk.posam.fsa.isk.jpa.springrepo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sk.posam.fsa.isk.domain.announcement.Announcement;

import java.util.List;
import java.util.Optional;

public interface AnnouncementSpringDataRepository extends JpaRepository<Announcement, Long> {

    @EntityGraph(attributePaths = {"photos", "author"})
    Optional<Announcement> findWithPhotosById(long id);

    @EntityGraph(attributePaths = {"photos", "author"})
    @Query("SELECT DISTINCT a FROM Announcement a")
    List<Announcement> findAllWithPhotos();
}