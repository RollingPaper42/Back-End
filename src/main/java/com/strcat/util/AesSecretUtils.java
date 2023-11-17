package com.strcat.util;

import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class AesSecretUtils {
    private final SecretKey secretKey;
    private final String ALGORITHM = "AES";

    public AesSecretUtils(@Value("${jwt.secret}") String secretKey) {
        byte[] encoded = Base64.getEncoder().encode(secretKey.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(encoded);
    }

    public String encrypt(Long data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return new String(cipher.doFinal(data.toString().getBytes(StandardCharsets.UTF_8)));
    }

    public Long decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedData.getBytes());
        return Long.parseLong(new String(decryptedBytes, StandardCharsets.UTF_8));
    }
}
