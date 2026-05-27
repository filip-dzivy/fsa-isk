package sk.posam.fsa.isk.jpa.adapter;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.posam.fsa.isk.domain.announcement.Announcement;
import sk.posam.fsa.isk.domain.announcement.AnnouncementRepository;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.jpa.springrepo.AnnouncementSpringDataRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class JpaAnnouncementRepositoryAdapter implements AnnouncementRepository {

    private final AnnouncementSpringDataRepository springDataRepository;
    private final EntityManager entityManager;

    public JpaAnnouncementRepositoryAdapter(AnnouncementSpringDataRepository springDataRepository,
                                            EntityManager entityManager) {
        this.springDataRepository = springDataRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Announcement> find(long id) {
        return springDataRepository.findById(id);
    }

    @Override
    public Optional<Announcement> findWithPhotos(long id) {
        return springDataRepository.findWithPhotosById(id);
    }

    @Override
    public Collection<Announcement> findAll() {
        return springDataRepository.findAll();
    }

    @Override
    public Collection<Announcement> findAllWithPhotos() {
        return springDataRepository.findAllWithPhotos();
    }

    @Override
    @Transactional
    public Announcement save(Announcement announcement) {
        if (announcement.getAuthor() != null) {
            announcement.setAuthor(entityManager.getReference(Member.class, announcement.getAuthor().getId()));
        }
        Announcement persisted;
        if (announcement.getId() == 0L) {
            entityManager.persist(announcement);
            persisted = announcement;
        } else {
            persisted = entityManager.merge(announcement);
        }
        Hibernate.initialize(persisted.getAuthor());
        Hibernate.initialize(persisted.getPhotos());
        return persisted;
    }

    @Override
    public void delete(Announcement announcement) {
        Announcement managed = entityManager.merge(announcement);
        entityManager.remove(managed);
    }
}