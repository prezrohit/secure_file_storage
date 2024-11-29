package org.prezrohit.securefilestorage.util.keyUtil;

import lombok.Getter;
import org.prezrohit.securefilestorage.util.Constants;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Getter
public class SymmetricKeyGenerator {

    private final SecretKey secretKey;

    public SymmetricKeyGenerator(int length, String algorithm) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(length);
        secretKey = keyGenerator.generateKey();
    }

    public void writeToFile(String path, byte[] key) throws IOException {

        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();

    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        SymmetricKeyGenerator genSK = new SymmetricKeyGenerator(256, "AES");
        genSK.writeToFile(Constants.STATIC_RESOURCE_PATH + "symmetricKey/secretKey", genSK.getSecretKey().getEncoded());
    }

}