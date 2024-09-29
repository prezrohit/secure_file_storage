package org.prezrohit.securefilestorage.keyUtil;

import lombok.Getter;
import org.prezrohit.securefilestorage.util.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

public class AsymmetricKeyGenerator {

    private final KeyPairGenerator keyGen;
    @Getter
    private PrivateKey privateKey;
    @Getter
    private PublicKey publicKey;

    public AsymmetricKeyGenerator(int keyLength) throws NoSuchAlgorithmException {
        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(keyLength);
        createKeys();
    }

    private void createKeys() {
        KeyPair pair = this.keyGen.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
    }

    private void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        AsymmetricKeyGenerator gk_Alice;
        AsymmetricKeyGenerator gk_Bob;

        gk_Alice = new AsymmetricKeyGenerator(4096);
        gk_Alice.createKeys();
        gk_Alice.writeToFile(Constants.STATIC_RESOURCE_PATH + "aliceKeyPair/publicKey", gk_Alice.getPublicKey().getEncoded());
        gk_Alice.writeToFile(Constants.STATIC_RESOURCE_PATH + "aliceKeyPair/privateKey", gk_Alice.getPrivateKey().getEncoded());

        gk_Bob = new AsymmetricKeyGenerator(4096);
        gk_Bob.createKeys();
        gk_Bob.writeToFile(Constants.STATIC_RESOURCE_PATH + "bobKeyPair/publicKey", gk_Bob.getPublicKey().getEncoded());
        gk_Bob.writeToFile(Constants.STATIC_RESOURCE_PATH + "bobKeyPair/privateKey", gk_Bob.getPrivateKey().getEncoded());
    }

}
