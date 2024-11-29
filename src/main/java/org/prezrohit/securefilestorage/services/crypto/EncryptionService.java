package org.prezrohit.securefilestorage.services.crypto;

import org.prezrohit.securefilestorage.util.encryptionutil.EncryptData;
import org.prezrohit.securefilestorage.util.encryptionutil.EncryptKey;
import org.prezrohit.securefilestorage.util.Constants;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
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

    public byte[] encrypt(byte[] fileBytes) throws GeneralSecurityException, IOException {
        File originalKeyFile = new File(Constants.STATIC_RESOURCE_PATH + "symmetricKey/secretKey");
        File encryptedKeyFile = new File(Constants.STATIC_RESOURCE_PATH + "symmetricKey/encryptedSecretKey");
        new EncryptKey(getPublic(Constants.STATIC_RESOURCE_PATH + "bobKeyPair/publicKey", "RSA"), originalKeyFile, encryptedKeyFile, "RSA");

        EncryptData encryptData = new EncryptData("AES");
        return encryptData.encryptFile(fileBytes, getSecretKey(Constants.STATIC_RESOURCE_PATH + "symmetricKey/secretKey", "AES"));

    }

}
