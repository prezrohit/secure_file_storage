package org.prezrohit.securefilestorage.exceptions;

// TODO - to be used when the file is requested and not found in the storage
public class StorageFileNotFoundException extends StorageException {
    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
