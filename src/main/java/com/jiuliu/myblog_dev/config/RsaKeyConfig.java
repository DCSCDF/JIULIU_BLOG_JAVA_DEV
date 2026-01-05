package com.jiuliu.myblog_dev.config;


import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class RsaKeyConfig {

    private static final int KEY_SIZE = 2048;
    private static final long REFRESH_INTERVAL_HOURS = 3;

    private String publicKeyBase64;
    private String privateKeyBase64;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        generateKeyPair();
        // 每 3 小时刷新一次
        scheduler.scheduleAtFixedRate(this::generateKeyPair,
                REFRESH_INTERVAL_HOURS, REFRESH_INTERVAL_HOURS, TimeUnit.HOURS);
    }

    private synchronized void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(KEY_SIZE);
            KeyPair keyPair = keyGen.generateKeyPair();

            this.publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            this.privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            System.out.println("✅ RSA 密钥已刷新");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无法生成 RSA 密钥对", e);
        }
    }

    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }

    public String getPrivateKeyBase64() {
        return privateKeyBase64;
    }
}