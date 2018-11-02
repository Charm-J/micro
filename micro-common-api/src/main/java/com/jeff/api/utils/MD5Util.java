package com.jeff.api.utils;

import java.security.MessageDigest;

public class MD5Util {

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /*
     * 先转为utf-8
     * */
    public static String MD5Encoding(String s) {
        byte[] btInput = null;
        try {
            btInput = s.getBytes("UTF-8");
        } catch (Exception e) {
        }
        return MD5(btInput, 32);
    }


    public static String MD5(String s) {
        byte[] btInput = s.getBytes();
        return MD5(btInput, 32);
    }

    public static String MD5_16(String str) {
        byte[] btInput = str.getBytes();
        return MD5(btInput, 16);
    }

    private static String MD5(byte[] btInput, int length) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // MessageDigest mdInst = MessageDigest.getInstance("SHA-1");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            String result = new String(str);
            return length == 16 ? result.substring(8, 24) : result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
