package org.prezrohit.securefilestorage.services;

import org.prezrohit.securefilestorage.services.crypto.DecryptionService;
import org.prezrohit.securefilestorage.services.crypto.EncryptionService;

public class TestEncryptionDecryption {

    private static final boolean encrypt = false;

    public static void main(String[] args) throws Exception {
        if (encrypt) {
            EncryptionService encryptionService = new EncryptionService();
//            encryptionService.encrypt();

        } else {
            DecryptionService service = new DecryptionService();
//            service.decrypt();
        }
    }
}
