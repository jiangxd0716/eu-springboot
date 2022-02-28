package com.eu.frame.common.exception;

/**
 * 全局异常状态码
 */
public enum GlobalExceptionCode {

    /**
     * 请求成功
     */
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "系统异常"),
    REQUEST_ARGUMENT_EXCEPTION(2, "请求参数异常"),
    USER_LOGIN_STATUS_EXCEPTION(3, "当前用户登陆信息未进行初始化"),
    NOT_FOUND_ENUM(4, "未找到对应枚举类型"),
    FEIGN_ERROR(5, "Feign 接口请求异常"),
    JEDIS_POOL_INIT_FAIL(6, "Jedis Pool 初始化失败"),
    HTTP_REQUEST_ERROR(7, "网络请求异常"),
    FILE_UPLOAD_ERROR(8, "文件上传失败"),
    DATA_IS_NULL(9, "数据是空的"),
    NECESSARY_DATA_IS_NULL(10, "某些必填数据为空"),

    METHOD_NOT_SUPPORT(400, "请求类型错误"),
    ILLEGAL_REQUEST(402, "非法请求"),
    SIGN_FAILED(403, "签名校验失败"),
    NOT_FOUND(404, "404找不到资源"),
    REPEAT_REQUEST(405, "重复请求"),

    PHONE_NUMBER_FORMAT_WRONG(1001, "手机号码格式错误"),

    FILE_UPLOAD_FAIL(2001, "文件上传失败"),

    MESSAGE_SEND_ERROR(3001, "短信发送失败"),
    MESSAGE_SEND_FREQUENTLY(3002, "短信发送频繁"),
    MESSAGE_CODE_ERROE(3003, "短信验证码错误"),

    DEL_ERROR(4001, "逻辑删除失败"),
    AREA_ID_NULL(4002, "领域信息为空"),

    USER_NOT_FOUNT(5001, "用户不存在"),
    USER_PASSWORD_ERROR(5002, "用户密码错误"),
    USER_EXIST(5003, "用户名已存在"),
    ;


    /**
     * 异常代码
     */
    private final int code;

    /**
     * 异常信息
     */
    private final String msg;


    GlobalExceptionCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return msg;
    }


    public static GlobalExceptionCode getByCode(int code) {

        GlobalExceptionCode[] values = GlobalExceptionCode.values();

        for (GlobalExceptionCode value : values) {
            if (value.getCode() == code) {
                return value;
            }
        }

        throw new GlobalException(GlobalExceptionCode.NOT_FOUND_ENUM, String.format("未找到 [%s] 对应的异常类型!", code));
    }

}
