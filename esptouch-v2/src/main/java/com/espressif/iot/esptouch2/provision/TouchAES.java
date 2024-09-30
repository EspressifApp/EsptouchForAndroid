package com.espressif.iot.esptouch2.provision;

import android.util.Log;

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
    private static final String TAG = "TouchAES";

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private final byte[] mKey;
    private final byte[] mIV;
    private final Cipher mEncryptCipher;
    private final Cipher mDecryptCipher;

    TouchAES(byte[] key) {
        this(key, null);
    }

    TouchAES(byte[] key, byte[] iv) {
        mKey = key;

        mIV = new byte[16];
        if (iv != null) {
            System.arraycopy(iv, 0, mIV, 0, Math.min(iv.length, mIV.length));
        }

        mEncryptCipher = createEncryptCipher();
        mDecryptCipher = createDecryptCipher();
    }

    private Cipher createEncryptCipher() {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            SecretKeySpec secretKeySpec = new SecretKeySpec(mKey, "AES");
            IvParameterSpec parameterSpec = new IvParameterSpec(mIV);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException
                e) {
            Log.w(TAG, "createEncryptCipher: ", e);
        }

        return null;
    }

    private Cipher createDecryptCipher() {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            SecretKeySpec secretKeySpec = new SecretKeySpec(mKey, "AES");
            IvParameterSpec parameterSpec = new IvParameterSpec(mIV);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec);

            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException
                e) {
            Log.w(TAG, "createDecryptCipher: ", e);
        }

        return null;
    }

    byte[] encrypt(byte[] content) {
        try {
            return mEncryptCipher.doFinal(content);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Log.w(TAG, "encrypt: ", e);
        }
        return null;
    }

    byte[] decrypt(byte[] content) {
        try {
            return mDecryptCipher.doFinal(content);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Log.w(TAG, "decrypt: ", e);
        }

        return null;
    }
}
