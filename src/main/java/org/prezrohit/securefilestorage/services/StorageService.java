package org.prezrohit.securefilestorage.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    void store(MultipartFile file) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;

    Stream<Path> loadAllFiles();

    Path load(String fileName);

    Resource loadAsResource(String fileName);

    void deleteAll();
}
