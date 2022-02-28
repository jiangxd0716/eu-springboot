package com.eu.frame.system.controller;

import com.eu.frame.common.exception.GlobalException;
import com.eu.frame.common.exception.GlobalExceptionCode;
import com.eu.frame.common.utils.RegexUtil;
import com.eu.frame.common.wrapper.GlobalResponseWrapper;
import com.eu.frame.system.enums.MessageType;
import com.eu.frame.system.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 短信验证码功能
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;


    /**
     * 发送短信验证码
     *
     * @param captchaType 短信类型 1登录 2注册 3重置
     * @param phoneNumber 手机号码
     */
    @GetMapping("")
    public GlobalResponseWrapper send(@NotBlank(message = "手机号码不能为空") @RequestParam("phoneNumber") String phoneNumber,
                                      @NotNull(message = "短信类型不能为空") @RequestParam("captchaType") Integer captchaType) {

        //检查手机号是否合法
        this.checkPhoneNumber(phoneNumber);

        //发送短信验证码
        return this.captchaService.send(phoneNumber, MessageType.getByCode(captchaType));
    }

    /**
     * 手机号检查
     *
     * @param phoneNumber
     */
    private void checkPhoneNumber(String phoneNumber) {
        if (!RegexUtil.phoneNumber(phoneNumber)) {
            throw new GlobalException(GlobalExceptionCode.PHONE_NUMBER_FORMAT_WRONG);
        }
    }

}
