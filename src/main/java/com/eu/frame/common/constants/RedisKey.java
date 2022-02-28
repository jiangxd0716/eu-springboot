package com.eu.frame.common.constants;

/**
 * 全局的 Redis Key 的前缀或者全名必须维护到该类中
 */
public class RedisKey {

    /**
     * 分布式锁
     * 发送短信验证码使用
     * arg1: 短信验证码类型 code
     * arg2: 电话号码
     */
    public static final String LOCK_SMS_CAPTCHA = "LOCK-SMS-CAPTCHA-%s-%s";

    /**
     * 短信验证码发送标识
     * 用于作为一分钟倒计时的标识
     * arg1: 短信验证码类型 code
     * arg2: 电话号码
     */
    public static final String KEY_SMS_CAPTCHA_SEND_MARK = "KEY-SMS-CAPTCHA-SEND-MARK-%s-%s";

    /**
     * 某手机号某种类型的短信验证码的 key
     * arg1: 短信验证码类型 code
     * arg2: 电话号码
     */
    public static final String KEY_SMS_CAPTCHA = "KEY-SMS-CAPTCHA-%s-%s";

    /**
     * 某手机号某种类型的短信验证码的 key
     * arg1: 短信验证码类型 code
     * arg2: 电话号码
     */
    public static final String KEY_WECHAT_ACCESS_TOKEN = "KEY_WECHAT_ACCESS_TOKEN";

    /**
     * 接口签名key
     */
    public static final String KEY_API_SIGN = "KEY_API_SIGN";

}
