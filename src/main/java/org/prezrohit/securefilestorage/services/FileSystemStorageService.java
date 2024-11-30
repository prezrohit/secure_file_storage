package org.prezrohit.securefilestorage.services;

import org.prezrohit.securefilestorage.config.StorageProperties;
import org.prezrohit.securefilestorage.entities.User;
import org.prezrohit.securefilestorage.exceptions.StorageException;
import org.prezrohit.securefilestorage.services.crypto.DecryptionService;
import org.prezrohit.securefilestorage.services.crypto.EncryptionKeysService;
import org.prezrohit.securefilestorage.services.crypto.EncryptionService;
import org.prezrohit.securefilestorage.services.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    private final UserService userService;
    private final EncryptionService encryptionService;
    private final EncryptionKeysService encryptionKeysService;

    public FileSystemStorageService(StorageProperties properties, @Autowired UserService userService, EncryptionService encryptionService, EncryptionKeysService encryptionKeysService) {
        if (properties.getLocation().trim().isEmpty()) {
            throw new StorageException("File upload location cannot be empty");
        }

        this.userService = userService;
        this.encryptionService = encryptionService;
        this.encryptionKeysService = encryptionKeysService;
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
    public void store(MultipartFile[] files) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        User authenticatedUser = userService.authenticatedUser();
        System.out.println("store() files called");
        byte[] encryptedSymmetricKey = authenticatedUser.getEncryptionKeys().getSymmetricKey();
        PrivateKey privateKey = encryptionKeysService.getPrivateKey(authenticatedUser.getEncryptionKeys().getPrivateKey());

        byte[] decryptedSymmetricKey = encryptionKeysService.decryptKey(encryptedSymmetricKey, privateKey);
        SecretKey symmetricKey = encryptionKeysService.getSymmetricKey(decryptedSymmetricKey);
        Arrays.stream(files).forEach(file -> store(file, symmetricKey));
    }

    private void store(MultipartFile multipartFile, SecretKey symmetricKey) {

        System.out.println("store() file called");
        // TODO - fetch encrypted symm key from DB and decrypt it, then use it to encrypt the file

        byte[] encryptedByteData = null;
        try {
            encryptedByteData = encryptionService.encrypt(multipartFile.getBytes(), symmetricKey);

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
