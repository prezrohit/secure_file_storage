package org.prezrohit.securefilestorage.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    void store(MultipartFile file);

    Stream<Path> loadAllFiles();

    Path load(String fileName);

    Resource loadAsResource(String fileName);

    void deleteAll();
}
