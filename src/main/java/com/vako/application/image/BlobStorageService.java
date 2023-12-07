package com.vako.application.image;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class BlobStorageService {

    private final BlobContainerClient avatarContainerClient;
    public BlobStorageService(@Value("${azure.blob.connection-string}") final String connectionString, @Value("${azure.blob.container-name}") final String containerName){
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        avatarContainerClient = blobServiceClient.getBlobContainerClient(containerName);
    }

    public void saveAvatar(final MultipartFile file, final String imageId) throws IOException {
        avatarContainerClient.getBlobClient(imageId).upload(file.getInputStream());
    }
}