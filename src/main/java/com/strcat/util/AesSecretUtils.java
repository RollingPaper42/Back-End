package com.strcat.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AesSecretUtils {

    private final String key;
    private final String iv;
    private final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public AesSecretUtils(@Value("${jwt.secret}") String secretKey) {
        this.key = secretKey;
        this.iv = secretKey.substring(0, 16);
    }

    public String encrypt(Long data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

        byte[] encodedBytes = cipher.doFinal(data.toString().getBytes());
        byte[] encrypted = Base64.getEncoder().encode(encodedBytes);
        return new String(encrypted).trim();
    }

    public Long decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData.getBytes());
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return Long.parseLong(new String(decrypted, StandardCharsets.UTF_8));
    }
}
