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

    @Value("${encryption.asymmetric-encryption-key-size}")
    private int asymmetricEncryptionKeySize;

    @Value("${encryption.symmetric-encryption-key-size}")
    private int symmetricEncryptionKeySize;

    @Value("${encryption.asymmetric-encryption-algorithm}")
    private String asymmetricEncryptionAlgorithm;

    @Value("${encryption.symmetric-encryption-algorithm}")
    private String symmetricEncryptionAlgorithm;

    public EncryptionKeys generateKeys() throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(symmetricEncryptionAlgorithm);
        keyGenerator.init(symmetricEncryptionKeySize);
        SecretKey symmetricKey = keyGenerator.generateKey();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(asymmetricEncryptionAlgorithm);
        keyPairGenerator.initialize(asymmetricEncryptionKeySize);
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
        Cipher cipher = Cipher.getInstance(asymmetricEncryptionAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(symmetricKey.getEncoded());
    }

    public byte[] decryptSymmetricKey(byte[] encryptedSymmetricKey, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(asymmetricEncryptionAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedSymmetricKey);
    }

    public PrivateKey getPrivateKey(byte[] encodedPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(asymmetricEncryptionAlgorithm);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        return keyFactory.generatePrivate(keySpec);
    }

    public SecretKey getSymmetricKey(byte[] encodedSymmetricKey) {
        return new SecretKeySpec(encodedSymmetricKey, symmetricEncryptionAlgorithm);
    }


}
