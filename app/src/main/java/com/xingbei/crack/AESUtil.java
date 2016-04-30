package com.xingbei.crack;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// C1215a
public final class AESUtil {
    private static final IvParameterSpec ivSpec = new IvParameterSpec(new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0});
    private Key key;
    private Cipher cipher;

    public AESUtil(byte[] bArr) {
        this(bArr, 128);
    }

    private AESUtil(byte[] bArr, byte b) {
        this.key = new SecretKeySpec(bArr, "AES");
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private AESUtil(byte[] bArr, int i) {
        this(bArr, (byte) 0);
    }

    // m8770a
    public final byte[] encode(byte[] bArr) {
        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.key, ivSpec);
            return this.cipher.doFinal(bArr);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // m8771b
    public final byte[] decode(byte[] bArr) {
        byte[] result = null;
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.key, ivSpec);
            result = this.cipher.doFinal(bArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}