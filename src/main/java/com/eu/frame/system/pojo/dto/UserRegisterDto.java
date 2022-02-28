package com.eu.frame.system.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户注册dto
 */
@Data
public class UserRegisterDto {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 登录密码
     */
    @NotBlank(message = "登录密码不能为空")
    private String password;

}
