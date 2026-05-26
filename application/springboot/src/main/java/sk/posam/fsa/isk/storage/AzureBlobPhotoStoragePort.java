package sk.posam.fsa.isk.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.specialized.BlockBlobClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.domain.shared.PhotoStoragePort;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * Production storage adapter — uploads bytes to Azure Blob Storage in the configured container.
 * Returns the public blob URL (assumes the container is configured for public read access).
 *
 * Active when {@code storage.azure.enabled=true} and the connection string + container name are provided.
 */
@Component
@ConditionalOnProperty(name = "storage.azure.enabled", havingValue = "true")
public class AzureBlobPhotoStoragePort implements PhotoStoragePort {

    private static final Logger log = LoggerFactory.getLogger(AzureBlobPhotoStoragePort.class);

    private final String connectionString;
    private final String containerName;
    private BlobContainerClient containerClient;

    public AzureBlobPhotoStoragePort(@Value("${storage.azure.connection-string}") String connectionString,
                                     @Value("${storage.azure.container}") String containerName) {
        this.connectionString = connectionString;
        this.containerName = containerName;
    }

    @PostConstruct
    void init() {
        this.containerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
        log.info("Azure Blob photo storage initialised (container={})", containerName);
    }

    @Override
    public StoredPhoto upload(byte[] bytes, String contentType, String originalFilename) {
        String key = UUID.randomUUID() + "-" + sanitize(originalFilename);
        BlobClient blobClient = containerClient.getBlobClient(key);
        BlockBlobClient blockBlobClient = blobClient.getBlockBlobClient();

        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
            blockBlobClient.upload(stream, bytes.length, true);
            blockBlobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));
        } catch (BlobStorageException | java.io.IOException e) {
            log.error("Failed to upload photo to Azure Blob: {}", e.getMessage(), e);
            throw new DomainException(
                    DomainException.Type.CONFLICT,
                    "Nahranie fotky zlyhalo.");
        }

        String url = blobClient.getBlobUrl();
        log.info("Uploaded photo to {}", url);
        return new StoredPhoto(url, key);
    }

    @Override
    public void delete(String storageKey) {
        try {
            containerClient.getBlobClient(storageKey).deleteIfExists();
        } catch (BlobStorageException e) {
            log.warn("Failed to delete blob {}: {}", storageKey, e.getMessage());
        }
    }

    private String sanitize(String name) {
        return name == null ? "file" : name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}