package com.strcat.util;

import com.strcat.exception.NotAcceptableException;
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

    private Cipher cipherInit(int mode) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(mode, keySpec, ivParameterSpec);
        return cipher;
    }

    public String encrypt(Long data) {
        try {
            Cipher cipher = cipherInit(Cipher.ENCRYPT_MODE);
            byte[] encodedBytes = cipher.doFinal(data.toString().getBytes());
            byte[] encrypted = Base64.getEncoder().encode(encodedBytes);
            return new String(encrypted, StandardCharsets.UTF_8).replace('/', '_');
        } catch (Exception e) {
            throw new NotAcceptableException();
        }
    }

    public Long decrypt(String encryptedData) {
        try {
            Cipher cipher = cipherInit(Cipher.DECRYPT_MODE);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData.replace('_', '/').getBytes());
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return Long.parseLong(new String(decrypted, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new NotAcceptableException();
        }
    }
}
