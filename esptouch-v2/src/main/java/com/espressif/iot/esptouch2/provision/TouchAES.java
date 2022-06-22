package com.espressif.iot.esptouch2.provision;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class TouchAES {
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final byte[] IV = new byte[16];

    private final byte[] mKey;
    private Cipher mEncryptCipher;
    private Cipher mDecryptCipher;

    TouchAES(byte[] key) {
        mKey = key;

        mEncryptCipher = createEncryptCipher();
        mDecryptCipher = createDecryptCipher();
    }

    private Cipher createEncryptCipher() {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            SecretKeySpec secretKeySpec = new SecretKeySpec(mKey, "AES");
            IvParameterSpec parameterSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException
                e) {
            e.printStackTrace();
        }

        return null;
    }

    private Cipher createDecryptCipher() {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            SecretKeySpec secretKeySpec = new SecretKeySpec(mKey, "AES");
            IvParameterSpec parameterSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec);

            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException
                e) {
            e.printStackTrace();
        }

        return null;
    }

    byte[] encrypt(byte[] content) {
        try {
            return mEncryptCipher.doFinal(content);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    byte[] decrypt(byte[] content) {
        try {
            return mDecryptCipher.doFinal(content);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }
}
