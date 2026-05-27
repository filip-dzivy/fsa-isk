package sk.posam.fsa.isk.domain.catalog;

import sk.posam.fsa.isk.domain.shared.DomainException;

import java.util.Objects;

public class BookPhoto {

    private long id;
    private String url;
    private String storageKey;
    private String caption;
    private int position;

    BookPhoto() {
        // for JPA
    }

    public BookPhoto(String url, String storageKey, String caption, int position) {
        if (url == null || url.isBlank()) {
            throw new DomainException(DomainException.Type.VALIDATION, "URL fotky je povinná.");
        }
        if (storageKey == null || storageKey.isBlank()) {
            throw new DomainException(DomainException.Type.VALIDATION, "Storage key fotky je povinný.");
        }
        if (position < 0) {
            throw new DomainException(DomainException.Type.VALIDATION, "Pozícia fotky nesmie byť záporná.");
        }
        this.url = url;
        this.storageKey = storageKey;
        this.caption = caption;
        this.position = position;
    }

    public long getId() { return id; }
    public String getUrl() { return url; }
    public String getStorageKey() { return storageKey; }
    public String getCaption() { return caption; }
    public int getPosition() { return position; }

    public void setCaption(String caption) { this.caption = caption; }
    public void setPosition(int position) { this.position = position; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookPhoto)) return false;
        return Objects.equals(id, ((BookPhoto) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
