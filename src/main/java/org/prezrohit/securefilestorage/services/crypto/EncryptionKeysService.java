package org.prezrohit.securefilestorage.services.crypto;

import org.prezrohit.securefilestorage.entities.EncryptionKeys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

@Service
public class EncryptionKeysService {

    // TODO
    //  make encryption algorithms and key length as external properties
    //  and access them using @Value here

    @Value("${encryption.rsa-key-size}")
    private int rsaKeySize = 4096;

    public EncryptionKeys generateKeys() throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey symmetricKey = keyGenerator.generateKey();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(rsaKeySize);
        KeyPair pair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        byte[] encryptedSymmetricKey = encryptSymmetricKey(symmetricKey, publicKey);

        // TODO
        //  find a way to encrypt private key and then store it

        return new EncryptionKeys()
                .setSymmetricKey(encryptedSymmetricKey)
                .setPublicKey(publicKey.getEncoded())
                .setPrivateKey(privateKey.getEncoded());
    }

    private byte[] encryptSymmetricKey(SecretKey symmetricKey, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(symmetricKey.getEncoded());
    }

    public byte[] decryptKey(byte[] encryptedSymmetricKey, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedSymmetricKey);
    }

    public PrivateKey getPrivateKey(byte[] encodedPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        return keyFactory.generatePrivate(keySpec);
    }

    public SecretKey getSymmetricKey(byte[] encodedSymmetricKey) {
        return new SecretKeySpec(encodedSymmetricKey, "AES");
    }


}
