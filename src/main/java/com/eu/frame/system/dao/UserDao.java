package com.eu.frame.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eu.frame.system.pojo.po.User;
import org.springframework.stereotype.Repository;

/**
 * 用户 dao
 */
@Repository
public interface UserDao extends BaseMapper<User> {

}
