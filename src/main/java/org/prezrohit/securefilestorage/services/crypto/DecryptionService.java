package org.prezrohit.securefilestorage.services.crypto;

import org.prezrohit.securefilestorage.util.decryptUtil.DecryptData;
import org.prezrohit.securefilestorage.util.decryptUtil.DecryptKey;
import org.prezrohit.securefilestorage.util.Constants;
import org.springframework.stereotype.Service;

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

    public byte[] decrypt(byte[] encryptedFileReceived) throws Exception {

        File encryptedKeyReceived = new File(Constants.STATIC_RESOURCE_PATH + "symmetricKey/encryptedSecretKey");
        File decryptedKeyFile = new File(Constants.STATIC_RESOURCE_PATH + "decryptedKey/SecretKey");
        new DecryptKey(getPrivate(Constants.STATIC_RESOURCE_PATH + "bobKeyPair/privateKey", "RSA"),
                encryptedKeyReceived, decryptedKeyFile, "RSA");

        DecryptData decryptData = new DecryptData("AES");
        return decryptData.decryptFile(encryptedFileReceived,
                getSecretKey(Constants.STATIC_RESOURCE_PATH + "decryptedKey/SecretKey", "AES"));
    }

}
