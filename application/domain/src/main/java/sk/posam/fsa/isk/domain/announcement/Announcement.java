package sk.posam.fsa.isk.domain.announcement;

import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Announcement {

    public static final int MAX_PHOTOS = 5;

    private long id;
    private String title;
    private String content;
    private LocalDateTime publishedAt;
    private Member author;
    private List<AnnouncementPhoto> photos = new ArrayList<>();

    Announcement() {
        // for JPA
    }

    public Announcement(String title, String content, Member author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.publishedAt = LocalDateTime.now();
        validateForCreation();
    }

    public AnnouncementPhoto addPhoto(String url, String storageKey, String caption) {
        if (photos.size() >= MAX_PHOTOS) {
            throw new DomainException(
                    DomainException.Type.CONFLICT,
                    "Oznam môže mať najviac " + MAX_PHOTOS + " fotiek.");
        }
        int nextPosition = photos.size();
        AnnouncementPhoto photo = new AnnouncementPhoto(url, storageKey, caption, nextPosition);
        photos.add(photo);
        return photo;
    }

    public AnnouncementPhoto removePhoto(long photoId) {
        AnnouncementPhoto removed = photos.stream()
                .filter(p -> p.getId() == photoId)
                .findFirst()
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Fotka s ID " + photoId + " nie je súčasťou oznamu."));
        photos.remove(removed);
        for (int i = 0; i < photos.size(); i++) {
            photos.get(i).setPosition(i);
        }
        return removed;
    }

    public List<AnnouncementPhoto> getPhotos() {
        return photos.stream()
                .sorted(Comparator.comparingInt(AnnouncementPhoto::getPosition))
                .toList();
    }

    public void update(String newTitle, String newContent) {
        this.title = newTitle;
        this.content = newContent;
        validateForCreation();
    }

    private void validateForCreation() {
        require(title != null && !title.isBlank(),
                DomainException.Type.VALIDATION,
                "Nadpis oznamu je povinný.");
        require(content != null && !content.isBlank(),
                DomainException.Type.VALIDATION,
                "Obsah oznamu je povinný.");
        require(author != null,
                DomainException.Type.VALIDATION,
                "Autor oznamu je povinný.");
    }

    private void require(boolean valid, DomainException.Type type, String message) {
        if (!valid) throw new DomainException(type, message);
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public Member getAuthor() { return author; }

    public void setAuthor(Member author) { this.author = author; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Announcement)) return false;
        return Objects.equals(id, ((Announcement) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Announcement{id=" + id + ", title='" + title + "', publishedAt=" + publishedAt + "}";
    }
}