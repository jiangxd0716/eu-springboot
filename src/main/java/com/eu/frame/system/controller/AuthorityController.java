package com.eu.frame.system.controller;


import com.eu.frame.system.service.AuthorityService;
import com.eu.frame.common.wrapper.Authority;
import com.eu.frame.system.pojo.vo.SelectOption;
import com.eu.frame.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 权限
 *
 * @author jiangxd
 */
@RestController
@RequestMapping("/authority")
public class AuthorityController {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private UserService userService;


    /**
     * 查询全部权限列表
     *
     * @return
     */
    @GetMapping("")
    @Authority(mark = "authority:list", name = "查询全部权限列表")
    public List<SelectOption> list() {
        return this.authorityService.list();
    }

    /**
     * 查询用户的权限列表 id
     *
     * @param userId 用户 id
     * @return
     */
    @GetMapping("user/{userId}")
    @Authority(mark = "authority:user:list", name = "查询用户的权限列表")
    public List<Long> user(@PathVariable Long userId) {
        return this.authorityService.selectUserAuthority(userId);
    }

//    /**
//     * 修改用户权限
//     *
//     * @param userId
//     * @param authority
//     */
//    @PatchMapping("user/{userId}")
//    public void user(@PathVariable Long userId,
//                     @RequestBody List<Long> authority) {
//
//        UserBo user = this.userService.findById(userId);
//
//        this.authorityService.updateUserAuthority(user, authority);
//    }
}
