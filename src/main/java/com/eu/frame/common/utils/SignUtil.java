package com.eu.frame.common.utils;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 签名工具类
 *
 * @author jiangxd
 */
@Slf4j
public class SignUtil {

    /**
     * 签名
     *
     * @param params 接口参数
     * @param salt   签名
     * @return 签名
     * @throws Exception
     */
    public static String signTopRequest(Map<String, Object> params, String salt) throws Exception {

        // 第一步：检查参数是否已经排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();

        for (String key : keys) {
            String value = replaceNullStr(params.get(key));
            if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
                if ("sign".equals(key)) {
                    continue;
                }
                query.append(key).append(StringPool.EQUALS).append(value).append(StringPool.AMPERSAND);
            }
        }

        String strtemp = null;
        if (query.length() > 0) {
            strtemp = query.substring(0, query.lastIndexOf(StringPool.AMPERSAND));
        }

        // 第三步：使用MD5加密
        byte[] bytes;
        strtemp = strtemp + salt;
        log.info("请求参数==》【{}】", strtemp);
        bytes = encryptMD5(strtemp);
        // 第四步：把二进制转化为大写的十六进制
        return byte2hex(bytes);
    }

    private static String replaceNullStr(Object str) {
        if (str == null) {
            return "";
        }
        return str.toString();
    }

    /**
     * 加密
     *
     * @param data
     * @return
     */
    private static byte[] encryptMD5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes("UTF-8"));
        return md.digest();
    }

    /**
     * 二进制转化为大写的十六进制
     *
     * @param bytes
     * @return
     */
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }


    public static void main(String args[]) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String str = "12345xxxxxx";

        String s = SecureUtil.md5(str);

        Map<String, Object> map = new HashMap<>();
        map.put("ts", "325435435");
        map.put("username", "admin");
        map.put("password", "admin");
        map.put("salt", "23435");
        map.put("sign", "ALLD99977LJF");

        try {
            String s1 = signTopRequest(map, "23435");

            System.out.println(s1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
