package sk.posam.fsa.isk.jpa.springrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.posam.fsa.isk.domain.announcement.Announcement;

public interface AnnouncementSpringDataRepository extends JpaRepository<Announcement, Long> {
}