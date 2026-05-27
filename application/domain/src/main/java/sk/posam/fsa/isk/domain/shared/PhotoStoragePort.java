package sk.posam.fsa.isk.domain.shared;

public interface PhotoStoragePort {

    StoredPhoto upload(byte[] bytes, String contentType, String originalFilename);

    void delete(String storageKey);

    record StoredPhoto(String url, String storageKey) {}
}