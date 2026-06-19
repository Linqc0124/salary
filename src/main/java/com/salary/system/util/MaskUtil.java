package com.salary.system.util;

/**
 * 数据脱敏工具类
 */
public class MaskUtil {
    
    /**
     * 手机号脱敏
     * 显示前3位和后4位，中间的4位用*代替
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    /**
     * 身份证号脱敏
     * 显示前4位和后4位，中间的用*代替
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) {
            return idCard;
        }
        int length = idCard.length();
        return idCard.substring(0, 4) + "**********".substring(0, length - 8) + idCard.substring(length - 4);
    }
    
    /**
     * 地址脱敏
     * 保留前6位，其他字符用*代替
     */
    public static String maskAddress(String address) {
        if (address == null || address.length() <= 6) {
            return address;
        }
        return address.substring(0, 6) + "****";
    }
    
    /**
     * 姓名脱敏
     * 保留姓，其他用*代替
     */
    public static String maskName(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        String stars = "";
        for (int i = 0; i < name.length() - 1; i++) {
            stars += "*";
        }
        return name.substring(0, 1) + stars;
    }
} 