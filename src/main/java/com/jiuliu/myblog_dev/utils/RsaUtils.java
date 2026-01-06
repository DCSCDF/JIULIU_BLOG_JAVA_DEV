package com.jiuliu.myblog_dev.utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RsaUtils {

    private static final Logger log = LoggerFactory.getLogger(RsaUtils.class);

    /**
     * 使用私钥解密（Base64 输入 → 明文）
     */
    public static String decryptByPrivateKey(String encryptedBase64, String privateKeyBase64) throws RuntimeException {
        try {

            log.debug("开始 RSA 私钥解密操作");

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);


            log.debug("RSA 私钥解密成功");
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {

            log.error("RSA 解密失败: {}", e.getMessage(), e);
            throw new RuntimeException("RSA 解密失败", e);
        }
    }


}