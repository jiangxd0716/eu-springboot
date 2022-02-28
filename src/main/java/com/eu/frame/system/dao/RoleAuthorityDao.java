package com.eu.frame.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eu.frame.system.pojo.po.RoleAuthority;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色权限关联表
 *
 * @author jiangxd
 */
@Repository
public interface RoleAuthorityDao extends BaseMapper<RoleAuthority> {

    /**
     * 根据角色 id 查询该角色所关联的全部权限 id
     *
     * @param roleId
     * @return
     */
    @Select(" SELECT authority_id FROM t_role_authority WHERE role_id = #{roleId} ")
    List<Long> selectAuthorityIdByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色 id 删除其关联的全部权限
     *
     * @param roleId
     */
    @Delete(" DELETE FROM t_role_authority WHERE role_id = #{roleId} ")
    void deleteByRoleId(@Param("roleId") Long roleId);

}
