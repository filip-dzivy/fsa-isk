package sk.posam.fsa.isk.domain.shared;

/**
 * Cross-context port for binary photo storage. Implementations:
 * - cloud: Azure Blob Storage adapter
 * - dev: noop adapter that returns a placeholder URL
 *
 * Returns a stable URL the client can use as &lt;img src=...&gt;, plus an opaque
 * storage key so the adapter can later delete the underlying object.
 */
public interface PhotoStoragePort {

    StoredPhoto upload(byte[] bytes, String contentType, String originalFilename);

    void delete(String storageKey);

    record StoredPhoto(String url, String storageKey) {}
}