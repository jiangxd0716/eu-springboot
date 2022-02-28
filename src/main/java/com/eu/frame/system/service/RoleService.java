package com.eu.frame.system.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eu.frame.system.dao.UserRoleDao;
import com.eu.frame.system.pojo.dto.RoleDto;
import com.eu.frame.system.pojo.po.Role;
import com.eu.frame.system.pojo.po.RoleAuthority;
import com.eu.frame.system.pojo.po.UserRole;
import com.eu.frame.system.pojo.vo.RoleVo;
import com.eu.frame.common.exception.GlobalException;
import com.eu.frame.common.exception.GlobalExceptionCode;
import com.eu.frame.system.dao.RoleAuthorityDao;
import com.eu.frame.system.dao.RoleDao;
import com.eu.frame.system.pojo.vo.SelectOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色
 *
 * @author jiangxd
 */
@Slf4j
@Service
public class RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RoleAuthorityDao roleAuthorityDao;

    @Autowired
    private AuthorityService authorityService;


    /**
     * 根据角色id获取角色信息
     *
     * @param roleId
     * @return
     */
    public Role findById(Long roleId) {
        return this.roleDao.selectById(roleId);
    }

    /**
     * 查询全部角色列表
     *
     * @return
     */
    public List<SelectOption> list() {
        return this.roleDao.selectAll();
    }

    /**
     * 分页列表查询
     *
     * @param page
     * @param roleName
     * @return
     */
    public void list(Page<RoleVo> page, String roleName) {
        this.roleDao.selectPageList(page, roleName);

        for (RoleVo roleVo : page.getRecords()) {
            List<Long> authorityIds = this.roleAuthorityDao.selectAuthorityIdByRoleId(roleVo.getId());
            if (authorityIds != null && !authorityIds.isEmpty()) {
                List<String> strings = new ArrayList<>();
                for (Long l : authorityIds
                ) {
                    strings.add(String.valueOf(l));
                }
                roleVo.setAuthority(strings);
            }

        }
    }

    /**
     * 新增
     *
     * @param dto
     */
    @Transactional
    public void add(RoleDto dto) {

        Role role = new Role();
        role.setMark(dto.getMark());
        role.setName(dto.getName());

        try {
            this.roleDao.insert(role);
        } catch (Exception e) { //此处极大可能是因为唯一索引约束导致的异常
            e.printStackTrace();
            throw new GlobalException(GlobalExceptionCode.ERROR, "角色新增失败!");
        }

        //插入角色关联的权限
        if (!CollectionUtil.isEmpty(dto.getAuthority())) {
            RoleAuthority roleAuthority;
            for (Long authorityId : dto.getAuthority()) {
                roleAuthority = new RoleAuthority();
                roleAuthority.setRoleId(role.getId());
                roleAuthority.setAuthorityId(authorityId);
                this.roleAuthorityDao.insert(roleAuthority);
            }
        }

    }

    /**
     * 修改
     */
    @Transactional
    public void edit(Long roleId, RoleDto dto) {

        //查询并检查旧数据
        Role role = this.roleDao.selectById(roleId);
        if (role == null) {
            throw new GlobalException(GlobalExceptionCode.ERROR, "角色不存在");
        }

        role.setName(dto.getName());

        try {
            this.roleDao.updateById(role);
        } catch (Exception e) { //此处极大可能是因为唯一索引约束导致的异常
            e.printStackTrace();
            throw new GlobalException(GlobalExceptionCode.ERROR, "角色信息修改失败!");
        }

        //删除角色关联的全部权限
        this.roleAuthorityDao.deleteByRoleId(role.getId());

        //重新插入角色关联的权限
        if (!CollectionUtil.isEmpty(dto.getAuthority())) {
            RoleAuthority roleAuthority;
            for (Long authorityId : dto.getAuthority()) {
                roleAuthority = new RoleAuthority();
                roleAuthority.setRoleId(role.getId());
                roleAuthority.setAuthorityId(authorityId);
                this.roleAuthorityDao.insert(roleAuthority);
            }
        }

    }

    /**
     * 根据主键 id 删除一条记录
     */
    @Transactional
    public void remove(Long id) {

        // 删除角色信息
        this.roleDao.deleteById(id);

        // 删除角色关联的全部权限
        this.roleAuthorityDao.deleteByRoleId(id);
    }

    /**
     * 查询用户的角色 id
     *
     * @param userId
     * @return
     */
    public List<String> selectUserRole(Long userId) {
        return this.roleDao.selectIdByUserId(userId);
    }

    /**
     * 查询用户的角色信息
     *
     * @param userId
     * @return
     */
    public List<SelectOption> selectUserRoleInfo(Long userId) {
        return this.userRoleDao.selectUserRoleInfo(userId);
    }

    /**
     * 更新用户角色
     *
     * @param userId
     * @param role
     */
    @Transactional
    public void updateUserRole(Long userId, List<Long> role) {

        // 删除用户全部的权限
        this.userRoleDao.deleteByUserId(userId);

        // 循环将新的权限入库
        for (Long roleId : role) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            this.userRoleDao.insert(userRole);
        }

    }


}
