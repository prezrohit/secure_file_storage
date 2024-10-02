package org.prezrohit.securefilestorage.services;

import org.prezrohit.securefilestorage.config.StorageProperties;
import org.prezrohit.securefilestorage.exceptions.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        if (properties.getLocation().trim().isEmpty()) {
            throw new StorageException("File upload location cannot be empty");
        }

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
    public void store(MultipartFile multipartFile) {

        // TODO - fetch encrypted symm key from DB and decrypt it, then use it to encrypt the file

        byte[] encryptedByteData = null;
        try {
            encryptedByteData = new EncryptionService().encrypt(multipartFile.getBytes());

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


        /*try {
            if (multipartFile == null || multipartFile.isEmpty()) {
                throw new StorageException("Received empty multipartFile");
            }

            Path destinationFile = this.rootLocation.resolve(Paths.get(multipartFile.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store multipartFile outside current directory");
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }*/
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
        /*
        TODO:
            1. read file from storage
            2. read symm key and private key from DB
            3. decrypt symm key
            4. use the symm key to decrypt the requested file
            5. return the file as resource
         */

        Path filePath = load(fileName);
        try {
            byte[] encryptedFileBytes = Files.readAllBytes(filePath);
            byte[] decryptedFileBytes = new DecryptionService().decrypt(encryptedFileBytes);
            return new InputStreamResource(new ByteArrayInputStream(decryptedFileBytes));

        } catch (IOException e) {
            System.err.println("Exception while reading file: " + e);

        } catch (Exception e) {
            System.err.println("Exception while decrypting file: " + e);
        }

        return null;

        /*try {
            Path file = load(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;

            } else {
                throw new StorageException("Could not read file: " + fileName);
            }

        } catch (MalformedURLException e) {
            throw new StorageException("Could not read file: " + fileName, e);
        }*/
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
