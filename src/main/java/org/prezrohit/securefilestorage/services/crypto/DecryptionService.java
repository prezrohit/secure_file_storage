package org.prezrohit.securefilestorage.services.crypto;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
public class DecryptionService {
    private PrivateKey getPrivate(String filename, String algorithm) throws Exception {

        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(spec);

    }

    private PublicKey getPublic(String filename, String algorithm) throws Exception {

        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }

    private SecretKeySpec getSecretKey(String filename, String algorithm) throws IOException {

        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        return new SecretKeySpec(keyBytes, algorithm);

    }

    public byte[] decrypt(byte[] encryptedFileReceived, SecretKey symmetricKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, symmetricKey);
        return cipher.doFinal(encryptedFileReceived);
    }

}
