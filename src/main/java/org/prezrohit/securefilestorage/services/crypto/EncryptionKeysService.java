package org.prezrohit.securefilestorage.services.crypto;

import org.prezrohit.securefilestorage.entities.EncryptionKeys;
import org.prezrohit.securefilestorage.repositories.EncryptionKeysRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;

@Service
public class EncryptionKeysService {

    // TODO
    //  make encryption algorithms and key length as external properties
    //  and access them using @Value here

    @Value("${encryption.rsa-key-size}")
    private int rsaKeySize = 4096;

    private final EncryptionKeysRepository encryptionKeysRepository;

    public EncryptionKeysService(EncryptionKeysRepository encryptionKeysRepository) {
        this.encryptionKeysRepository = encryptionKeysRepository;
    }

    public EncryptionKeys generateKeys() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(rsaKeySize);
        KeyPair pair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        // TODO
        //  keep symm key encrypted by public key in the object,
        //  keep private key encrypted by password in the object

        EncryptionKeys encryptionKeys = new EncryptionKeys()
                .setSymmetricKey(secretKey.getEncoded())
                .setPrivateKey(privateKey.getEncoded())
                .setPublicKey(publicKey.getEncoded());

        // saveEncryptionKeys(encryptionKeys);
        return encryptionKeys;
    }

    private void saveEncryptionKeys(EncryptionKeys encryptionKeys) {
        encryptionKeysRepository.save(encryptionKeys);
    }
}
