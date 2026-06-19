package com.salary.system.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * SM4国家密码算法工具类
 */
public class SM4Util {
    // 加载BouncyCastle作为安全提供者
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // SM4算法名称
    private static final String ALGORITHM_NAME = "SM4";
    // 加密模式和填充模式
    private static final String ALGORITHM_ECB_PKCS5PADDING = "SM4/ECB/PKCS5Padding";
    // 密钥长度：128位（即16字节）
    private static final int DEFAULT_KEY_SIZE = 128;
    // 默认密钥（在实际应用中应从配置文件中读取或者通过更安全的方式存储）
    private static final String DEFAULT_KEY = "SalarySystemKey1";

    /**
     * 生成SM4密钥
     *
     * @return 16字节的密钥
     */
    public static byte[] generateKey() throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[DEFAULT_KEY_SIZE / 8];
        random.nextBytes(key);
        return key;
    }

    /**
     * SM4加密
     *
     * @param data 待加密数据
     * @return Base64编码的加密结果
     */
    public static String encrypt(String data) {
        try {
            return encrypt(data, DEFAULT_KEY);
        } catch (Exception e) {
            throw new RuntimeException("SM4加密失败", e);
        }
    }

    public static void main(String[] args) {
    }

    /**
     * SM4加密
     *
     * @param data 待加密数据
     * @param key 密钥
     * @return Base64编码的加密结果
     */
    public static String encrypt(String data, String key) throws Exception {
        byte[] keyBytes = Arrays.copyOf(key.getBytes(StandardCharsets.UTF_8), 16);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM_NAME);
        Cipher cipher = Cipher.getInstance(ALGORITHM_ECB_PKCS5PADDING, BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.toBase64String(encrypted);
    }

    /**
     * SM4解密
     *
     * @param encryptedBase64 Base64编码的加密数据
     * @return 解密后的明文
     */
    public static String decrypt(String encryptedBase64) {
        try {
            return decrypt(encryptedBase64, DEFAULT_KEY);
        } catch (Exception e) {
            throw new RuntimeException("SM4解密失败", e);
        }
    }

    /**
     * SM4解密
     *
     * @param encryptedBase64 Base64编码的加密数据
     * @param key 密钥
     * @return 解密后的明文
     */
    public static String decrypt(String encryptedBase64, String key) throws Exception {
        byte[] keyBytes = Arrays.copyOf(key.getBytes(StandardCharsets.UTF_8), 16);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM_NAME);
        Cipher cipher = Cipher.getInstance(ALGORITHM_ECB_PKCS5PADDING, BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] encrypted = Base64.decode(encryptedBase64);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
