package com.strcat.util;

import com.strcat.exception.NotAcceptableException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecureDataUtils {
    private final String key;
    private final String iv;

    public SecureDataUtils(@Value("${jwt.secret}") String secretKey) {
        this.key = secretKey;
        this.iv = secretKey.substring(0, 16);
    }

    public String encrypt(Long data) {
        final String SEPARATOR = ",";
        try {
            Cipher cipher = cipherInit(Cipher.ENCRYPT_MODE);
            String input = data.toString() + SEPARATOR + getCurrentTime();
            byte[] encodedBytes = cipher.doFinal(input.getBytes());
            byte[] encrypted = Base64.getEncoder().encode(encodedBytes);
            return new String(encrypted, StandardCharsets.UTF_8).replace('/', '_');
        } catch (Exception e) {
            throw new NotAcceptableException("암호화에 실패했습니다.");
        }
    }

    public Long decrypt(String encryptedData) {
        try {
            Cipher cipher = cipherInit(Cipher.DECRYPT_MODE);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData.replace('_', '/').getBytes());
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return getIdFromRawDataToLong(new String(decrypted, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new NotAcceptableException("복호화에 실패했습니다.");
        }
    }

    private Cipher cipherInit(int mode) throws Exception {
        final String ALGORITHM = "AES/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(mode, keySpec, ivParameterSpec);
        return cipher;
    }

    private Long getIdFromRawDataToLong(String rawData) {
        return Long.parseLong(rawData.split(",")[0]);
    }

    private String getCurrentTime() {
        return Instant.now().toString();
    }
}
