package com.buildmybridge.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5 加密工具
 */
public class MD5Util {

    /**
     * MD5 加密
     */
    public static String md5(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        return DigestUtils.md5Hex(text);
    }

    /**
     * 验证 MD5 是否匹配
     */
    public static boolean verifyMD5(String text, String hash) {
        if (text == null || hash == null) {
            return false;
        }
        return md5(text).equalsIgnoreCase(hash);
    }
}
