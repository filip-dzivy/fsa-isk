package sk.posam.fsa.isk.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.shared.PhotoStoragePort;

import java.util.UUID;

/**
 * Fallback storage adapter for local dev — does NOT persist bytes anywhere.
 * Returns a fake URL so the rest of the app works (DB row gets created etc.).
 * Active when {@code storage.azure.enabled=false} (default).
 */
@Component
@ConditionalOnProperty(name = "storage.azure.enabled", havingValue = "false", matchIfMissing = true)
public class NoopPhotoStoragePort implements PhotoStoragePort {

    private static final Logger log = LoggerFactory.getLogger(NoopPhotoStoragePort.class);

    @Override
    public StoredPhoto upload(byte[] bytes, String contentType, String originalFilename) {
        String key = "noop/" + UUID.randomUUID() + "-" + sanitize(originalFilename);
        String url = "https://placeholder.invalid/" + key;
        log.info("[noop-photo] would upload {} bytes ({}), pretending it's at {}",
                bytes.length, contentType, url);
        return new StoredPhoto(url, key);
    }

    @Override
    public void delete(String storageKey) {
        log.info("[noop-photo] would delete {}", storageKey);
    }

    private String sanitize(String name) {
        return name == null ? "file" : name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}