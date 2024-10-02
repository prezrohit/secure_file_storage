package org.prezrohit.securefilestorage.decryptUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class DecryptData {

    private final Cipher cipher;

    public DecryptData(String algorithm) throws GeneralSecurityException {
        this.cipher = Cipher.getInstance(algorithm);
    }

    public byte[] decryptFile(byte[] input, SecretKeySpec key) throws GeneralSecurityException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        return this.cipher.doFinal(input);
    }

    private void writeToFile(File output, byte[] toWrite) throws IOException {

        output.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(toWrite);
        fos.flush();
        fos.close();
        System.out.println("The file was successfully decrypted. You can view it in: " + output.getPath());

    }

    public byte[] getFileInBytes(File f) throws IOException {

        FileInputStream fis = new FileInputStream(f);
        byte[] fbytes = new byte[(int) f.length()];
        fis.read(fbytes);
        fis.close();
        return fbytes;
    }

}