package com.eu.frame.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.eu.frame.common.exception.GlobalException;
import com.eu.frame.common.exception.GlobalExceptionCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信发送类型
 */
@Slf4j
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MessageType {

    LOGIN(1, "登录", "SMS_169960246", "{'code':'%s'}"),
    REGISTER(2, "注册", "SMS_169960246", "{'code':'%s'}"),
    RESET(3, "重置", "SMS_169960246", "{'code':'%s'}"),
    ;


    /**
     * 代码
     */
    @EnumValue
    private final int code;

    /**
     * 内容
     */
    private final String msg;

    /**
     * 短信服务商所提供的模版编号
     * 暂时只有阿里云一方
     */
    private final String templateCode;

    /**
     * 短信服务商 , 即阿里云所要求的参数模版
     */
    private final String templateParams;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getTemplateParams() {
        return templateParams;
    }

    MessageType(int code, String msg, String templateCode, String templateParams) {
        this.code = code;
        this.msg = msg;
        this.templateCode = templateCode;
        this.templateParams = templateParams;
    }

    /**
     * 根据代码获取枚举
     * 不要尝试缓存全部的枚举，该方法用到的频率不会太高，且枚举很少，不会造成资源浪费
     * <p>
     * 使用 @JsonCreator 让 jackson 解析 json 的时候能匹配到该枚举
     * 参考：https://segmentfault.com/q/1010000020636087
     *
     * @param code
     * @return
     */
    @JsonCreator
    public static MessageType getByCode(int code) {

        MessageType[] values = MessageType.values();

        for (MessageType value : values) {
            if (value.getCode() == code) {
                return value;
            }
        }

        throw new GlobalException(GlobalExceptionCode.NOT_FOUND_ENUM, String.format("未找到 [%s] 对应的短信类型!", code));
    }
}
