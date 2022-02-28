package com.eu.frame.system.service;

import cn.hutool.core.util.RandomUtil;
import com.eu.frame.common.wrapper.GlobalResponseWrapper;
import com.eu.frame.common.exception.GlobalExceptionCode;
import com.eu.frame.common.redis.JedisClient;
import com.eu.frame.common.constants.RedisKey;
import com.eu.frame.common.utils.AliyunUtil;
import com.eu.frame.system.enums.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 短信验证码 service
 */
@Slf4j
@Service
public class CaptchaService {

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private AliyunUtil aliyunUtil;

    /**
     * 短信发送时间间隔
     */
    @Value("${message.send.interval}")
    private Integer messageSendInterval;

    /**
     * 短信验证码有效期
     */
    @Value("${captcha.expiration}")
    private Integer captchaExpiration;


    /**
     * 短信验证码发送
     *
     * @param phoneNumber
     * @param messageType
     * @return
     */
    public GlobalResponseWrapper send(String phoneNumber, MessageType messageType) {

        //检查短信验证码是否存在 , 若存在则反馈短信验证码发送频繁
        boolean captchaSendMarkExists = this.jedisClient.operate(jedis -> jedis.exists(String.format(RedisKey.KEY_SMS_CAPTCHA_SEND_MARK, messageType.getCode(), phoneNumber)));
        if (captchaSendMarkExists) {
            return new GlobalResponseWrapper(GlobalExceptionCode.MESSAGE_SEND_FREQUENTLY);
        }

        //得到一个长度为 6 的随机数字符串
        String captcha = RandomUtil.randomNumbers(6);

        //若短信发送失败则直接反馈
        boolean isSend = this.aliyunUtil.sendMessage(phoneNumber, messageType, String.format(messageType.getTemplateParams(), captcha));
        if (!isSend) {
            return new GlobalResponseWrapper(GlobalExceptionCode.MESSAGE_SEND_ERROR);
        }

        //将短信验证码发送标识和短信验证法存入 redis 并设置过期时间
        CaptchaService.this.jedisClient.operate(jedis -> {
            jedis.setex(String.format(RedisKey.KEY_SMS_CAPTCHA_SEND_MARK, messageType.getCode(), phoneNumber), CaptchaService.this.messageSendInterval, captcha);
            jedis.setex(String.format(RedisKey.KEY_SMS_CAPTCHA, messageType.getCode(), phoneNumber), CaptchaService.this.captchaExpiration, captcha);
            return null;    //此函数要求必须存在返回值 , 此处返回一个 null
        });

        return new GlobalResponseWrapper();
    }


    /**
     * 检查短信验证码是否正确
     *
     * @param phoneNumber
     * @param captcha
     * @param messageType
     * @return
     */
    public boolean check(String phoneNumber, String captcha, MessageType messageType) {

        String oldCaptcha = this.jedisClient.operate(jedis -> jedis.get(String.format(RedisKey.KEY_SMS_CAPTCHA, messageType.getCode(), phoneNumber)));

        if (captcha.equals(oldCaptcha))
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }


}
