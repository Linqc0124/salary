package com.salary.system.util;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;

/**
 * SM3国家密码算法工具类
 */
public class SM3Util {

    /**
     * SM3摘要算法
     *
     * @param data 待加密数据
     * @return 加密后的十六进制字符串
     */
    public static String encrypt(String data) {
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        SM3Digest sm3 = new SM3Digest();
        sm3.update(dataBytes, 0, dataBytes.length);
        byte[] result = new byte[sm3.getDigestSize()];
        sm3.doFinal(result, 0);
        return Hex.toHexString(result);
    }

    public static void main(String[] args) {
        System.out.println(encrypt("admin"));
    }


    /**
     * HMAC-SM3算法
     *
     * @param key 密钥
     * @param data 待加密数据
     * @return 加密后的十六进制字符串
     */
    public static String hmac(String key, String data) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

        // HMAC-SM3的ipad和opad常量
        byte[] ipad = new byte[64];
        byte[] opad = new byte[64];

        // 填充密钥
        byte[] keyPadded = new byte[64];
        if (keyBytes.length > 64) {
            SM3Digest sm3 = new SM3Digest();
            sm3.update(keyBytes, 0, keyBytes.length);
            byte[] result = new byte[sm3.getDigestSize()];
            sm3.doFinal(result, 0);
            System.arraycopy(result, 0, keyPadded, 0, result.length);
        } else {
            System.arraycopy(keyBytes, 0, keyPadded, 0, keyBytes.length);
        }

        // 计算K ⊕ ipad和K ⊕ opad
        for (int i = 0; i < 64; i++) {
            ipad[i] = (byte) (keyPadded[i] ^ 0x36);
            opad[i] = (byte) (keyPadded[i] ^ 0x5c);
        }

        // 计算SM3(K ⊕ opad, SM3(K ⊕ ipad, data))
        SM3Digest sm3 = new SM3Digest();
        sm3.update(ipad, 0, ipad.length);
        sm3.update(dataBytes, 0, dataBytes.length);
        byte[] innerHash = new byte[sm3.getDigestSize()];
        sm3.doFinal(innerHash, 0);

        SM3Digest sm3Outer = new SM3Digest();
        sm3Outer.update(opad, 0, opad.length);
        sm3Outer.update(innerHash, 0, innerHash.length);
        byte[] result = new byte[sm3Outer.getDigestSize()];
        sm3Outer.doFinal(result, 0);

        return Hex.toHexString(result);
    }

    /**
     * 验证SM3摘要
     *
     * @param data 原始数据
     * @param sm3Hash SM3摘要
     * @return 是否匹配
     */
    public static boolean verify(String data, String sm3Hash) {
        String newHash = encrypt(data);
        return newHash.equals(sm3Hash);
    }

    /**
     * 验证HMAC-SM3摘要
     *
     * @param key 密钥
     * @param data 原始数据
     * @param hmacSm3Hash HMAC-SM3摘要
     * @return 是否匹配
     */
    public static boolean verifyHmac(String key, String data, String hmacSm3Hash) {
        String newHmac = hmac(key, data);
        return newHmac.equals(hmacSm3Hash);
    }
}
