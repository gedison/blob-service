package com.pastelpunk.core.features.blobstorage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobStorageException;
import com.pastelpunk.core.config.CommonConfiguration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlobStorageService {

    private final BlobServiceClient blobServiceClient;

    public BlobStorageService(CommonConfiguration commonConfiguration) {
        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(commonConfiguration.getBlobStorageConfiguration().getEndpoint())
                .sasToken(commonConfiguration.getBlobStorageConfiguration().getSasToken())
                .buildClient();
    }

    public void createContainer(String containerName) {
        try {
            BlobContainerClient containerClient = blobServiceClient.createBlobContainer(containerName);
        } catch (BlobStorageException ex) {
            if (!ex.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
                throw ex;
            }
        }
    }

    public void uploadFile(String containerName, File toUpload) throws FileNotFoundException {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(toUpload.getName());
        blobClient.upload(new FileInputStream(toUpload), toUpload.length());
    }


    public List<String> listBlobs(String containerName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        return containerClient.listBlobs().stream().map(BlobItem::getName).collect(Collectors.toList());
    }

    public void getBlob(String containerName, String blobName, String destination) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.downloadToFile(destination);
    }
}

