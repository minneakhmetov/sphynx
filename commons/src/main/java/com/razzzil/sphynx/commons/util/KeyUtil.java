package com.razzzil.sphynx.commons.util;

import com.razzzil.sphynx.commons.exception.IllegalKeyException;
import lombok.SneakyThrows;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class KeyUtil {

    private static final String TRANSFORMATION = "AES";
    private static final int KEY_SIZE = 256;

    @SneakyThrows
    public static String encrypt(String input, SecretKey key) {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    @SneakyThrows
    public static String decrypt(String cipherText, SecretKey key) throws IllegalKeyException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        try {
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(plainText);
        } catch (BadPaddingException e) {
            throw new IllegalKeyException();
        }
    }

    public static SecretKey deserializeKey(String key){
        byte[] bytes = Base64.getDecoder().decode(key);
        return new SecretKeySpec(bytes, 0, bytes.length, TRANSFORMATION);
    }

    public static String serializeKey(SecretKey secretKey){
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    @SneakyThrows
    public static SecretKey generateKey(){
        KeyGenerator keyGenerator = KeyGenerator.getInstance(TRANSFORMATION);
        keyGenerator.init(KEY_SIZE);
        return keyGenerator.generateKey();
    }

}
