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

    private final BlobContainerClient chatContainerClient;

    private final BlobContainerClient messageContainerClient;
    public BlobStorageService(@Value("${azure.blob.connection-string}") final String connectionString,
                              @Value("${azure.blob.avatars.container-name}") final String avatarsContainerName,
                              @Value("${azure.blob.chats.container-name}") final String chatsContainerName,
                              @Value("${azure.blob.messages.container-name}") final String messagesContainerName){
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        avatarContainerClient = blobServiceClient.getBlobContainerClient(avatarsContainerName);
        chatContainerClient = blobServiceClient.createBlobContainerIfNotExists(chatsContainerName);
        messageContainerClient = blobServiceClient.createBlobContainerIfNotExists(messagesContainerName);
    }

    public void saveAvatar(final MultipartFile file, final String imageId) throws IOException {
        avatarContainerClient.getBlobClient(imageId).upload(file.getInputStream());
    }

    public void saveChatImage(final MultipartFile file, final String imageId) throws IOException {
        chatContainerClient.getBlobClient(imageId).upload(file.getInputStream());
    }

    public void saveMessageImage(final MultipartFile file, final String imageId) throws IOException {
        messageContainerClient.getBlobClient(imageId).upload(file.getInputStream());
    }
}