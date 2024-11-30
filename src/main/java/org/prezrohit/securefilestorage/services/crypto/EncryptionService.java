package org.prezrohit.securefilestorage.services.crypto;

import org.prezrohit.securefilestorage.entities.EncryptionKeys;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
public class EncryptionService {

    private final EncryptionKeysService encryptionKeysService;

    public EncryptionService(EncryptionKeysService encryptionKeysService) {
        this.encryptionKeysService = encryptionKeysService;
    }

    public PrivateKey getPrivate(String filename, String algorithm) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(spec);
    }

    public PublicKey getPublic(String filename, String algorithm) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }

    public SecretKey getSecretKey(String filename, String algorithm) throws IOException {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        return new SecretKeySpec(keyBytes, algorithm);
    }

    public byte[] encrypt(byte[] fileBytes, EncryptionKeys encryptionKeys) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        PrivateKey privateKey = encryptionKeysService.getPrivateKey(encryptionKeys.getPrivateKey());
        byte[] encryptedSymmetricKey = encryptionKeys.getSymmetricKey();
        byte[] decryptedSymmetricKey = encryptionKeysService.decryptSymmetricKey(encryptedSymmetricKey, privateKey);
        SecretKey symmetricKey = encryptionKeysService.getSymmetricKey(decryptedSymmetricKey);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
        return cipher.doFinal(fileBytes);
    }

}
