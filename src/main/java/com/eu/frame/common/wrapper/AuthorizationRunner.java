package com.eu.frame.common.wrapper;

import com.eu.frame.system.dao.RoleDao;
import com.eu.frame.system.dao.UserDao;
import com.eu.frame.system.dao.UserRoleDao;
import com.eu.frame.system.pojo.po.Role;
import com.eu.frame.system.pojo.po.User;
import com.eu.frame.system.pojo.po.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 服务启动时拉取接口权限缓存
 */
@Slf4j
@Component
public class AuthorizationRunner implements CommandLineRunner {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;


    @Override
    public void run(String... args) {

        // 初始化超级管理员
        this.initAdmin();

    }

    /**
     * 初始化超级管理员
     */
    public void initAdmin() {
        log.info("系统初始化超级管理员角色");
        Role role = new Role();
        role.setId(0L);
        role.setName("admin");
        role.setMark("admin");

        try {
            this.roleDao.insert(role);
        } catch (Exception ignored) {
        }

        log.info("系统初始化超级管理员用户");
        User user = new User();
        user.setId(0L);
        user.setUsername("admin");
        user.setPassword("admin");

        try {
            this.userDao.insert(user);
        } catch (Exception ignored) {
        }

        UserRole ur = new UserRole();
        ur.setId(0L);
        ur.setUserId(0L);
        ur.setRoleId(0L);

        try {
            this.userRoleDao.insert(ur);
        } catch (Exception ignored) {
        }

    }

}
