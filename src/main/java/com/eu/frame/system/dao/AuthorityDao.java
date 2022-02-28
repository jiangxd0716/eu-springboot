package com.eu.frame.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eu.frame.system.pojo.po.Authority;
import com.eu.frame.system.pojo.vo.SelectOption;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 权限
 *
 * @author jiangxd
 */
@Repository
public interface AuthorityDao extends BaseMapper<Authority> {

    /**
     * 根据用户 id 查询该用户全部未禁用的权限标识
     *
     * @param userId
     * @return
     */
    @Select(" SELECT a.mark FROM t_user_role ur LEFT JOIN t_role_authority ra ON ur.role_id = ra.role_id LEFT JOIN t_authority a ON ra.authority_id = a.id WHERE ur.user_id = #{userId} ")
    Set<String> selectMarkByUserId(@Param("userId") Long userId);

    /**
     * 根据上级菜单id查看权限列表
     *
     * @return
     */
    @Select(" SELECT id AS value,name AS label FROM t_authority WHERE parent_id = ${parentId} ")
    List<SelectOption> selectAll(@Param("parentId") Long parentId);

    /**
     * 根据用户id查询用户所拥有的全部权限  id
     *
     * @param userId
     * @return
     */
    @Select(" SELECT a.id FROM t_user_role ur LEFT JOIN t_role_authority ra ON ur.role_id = ra.role_id LEFT JOIN t_authority a ON ra.authority_id = a.id WHERE ur.user_id = #{userId} ")
    List<Long> selectIdByUserId(@Param("userId") Long userId);

}
