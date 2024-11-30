package org.prezrohit.securefilestorage.services;

import org.prezrohit.securefilestorage.config.StorageProperties;
import org.prezrohit.securefilestorage.entities.EncryptionKeys;
import org.prezrohit.securefilestorage.exceptions.StorageException;
import org.prezrohit.securefilestorage.services.crypto.DecryptionService;
import org.prezrohit.securefilestorage.services.crypto.EncryptionService;
import org.prezrohit.securefilestorage.services.security.UserService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    private final UserService userService;
    private final EncryptionService encryptionService;
    private final DecryptionService decryptionService;

    public FileSystemStorageService(StorageProperties properties, UserService userService, EncryptionService encryptionService, DecryptionService decryptionService) {
        if (properties.getLocation().trim().isEmpty()) {
            throw new StorageException("File upload location cannot be empty");
        }

        this.userService = userService;
        this.encryptionService = encryptionService;
        this.decryptionService = decryptionService;
        this.rootLocation = Paths.get(properties.getLocation());

        System.out.println(this.rootLocation);

        init();
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);

        } catch (IOException e) {
            throw new StorageException("could not initialize storage: ", e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        EncryptionKeys encryptionKeys = userService.authenticatedUser().getEncryptionKeys();
        store(file, encryptionKeys);
    }

    private void store(MultipartFile multipartFile, EncryptionKeys encryptionKeys) {

        // TODO - fetch encrypted symm key from DB and decrypt it, then use it to encrypt the file

        byte[] encryptedByteData = null;
        try {
            encryptedByteData = encryptionService.encrypt(multipartFile.getBytes(), encryptionKeys);

        } catch (GeneralSecurityException e) {
            System.err.println("Encryption Exception: " + e);

        } catch (IOException e) {
            System.err.println("Exception while reading bytes: " + e);
        }

        String filePath = this.rootLocation + "/" + multipartFile.getOriginalFilename();
        System.out.println("filePath: " + filePath);
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(encryptedByteData);
            fos.flush();

        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e);

        } catch (IOException e) {
            System.err.println("Exception while writing the file (bytes): " + e);
        }

        System.out.println("The file was successfully encrypted and stored in: " + this.rootLocation);
    }

    @Override
    public Stream<Path> loadAllFiles() {
        try {
            return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);

        } catch (IOException e) {
            throw new StorageException("Failed to load stored files", e);
        }
    }

    @Override
    public Path load(String fileName) {
        return rootLocation.resolve(fileName);
    }

    @Override
    public Resource loadAsResource(String fileName) {
        EncryptionKeys encryptionKeys = userService.authenticatedUser().getEncryptionKeys();

        Path filePath = load(fileName);
        try {
            byte[] encryptedFileBytes = Files.readAllBytes(filePath);
            byte[] decryptedFileBytes = decryptionService.decrypt(encryptedFileBytes, encryptionKeys);
            return new InputStreamResource(new ByteArrayInputStream(decryptedFileBytes));

        } catch (IOException e) {
            System.err.println("Exception while reading file: " + e);

        } catch (Exception e) {
            System.err.println("Exception while decrypting file: " + e);
        }

        return null;
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
