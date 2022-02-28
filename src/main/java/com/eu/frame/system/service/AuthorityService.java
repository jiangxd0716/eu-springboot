package com.eu.frame.system.service;

import com.eu.frame.system.dao.AuthorityDao;
import com.eu.frame.system.pojo.vo.SelectOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限
 *
 * @author jiangxd
 */
@Service
public class AuthorityService {

    @Autowired
    private AuthorityDao authorityDao;


    /**
     * 根据用户 id 查询其所拥有的全部权限 mark
     *
     * @param userId
     * @return
     */
    public Set<String> selectMarkByUserId(Long userId) {
        return this.authorityDao.selectMarkByUserId(userId).stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * 查询全部权限列表
     *
     * @return
     */
    public List<SelectOption> list() {
        List<SelectOption> menus = this.authorityDao.selectAll(0L);
        menus.forEach(selectOption -> selectOption.setOptions(this.authorityDao.selectAll(selectOption.getValue())));
        return menus;
    }

    /**
     * 查询用户的权限 id
     *
     * @param userId
     * @return
     */
    public List<Long> selectUserAuthority(Long userId) {
        return this.authorityDao.selectIdByUserId(userId);
    }

}