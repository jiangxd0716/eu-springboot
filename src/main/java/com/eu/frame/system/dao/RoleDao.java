package com.eu.frame.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eu.frame.system.pojo.po.Role;
import com.eu.frame.system.pojo.vo.RoleVo;
import com.eu.frame.system.pojo.vo.SelectOption;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色
 *
 * @author jiangxd
 */
@Repository
public interface RoleDao extends BaseMapper<Role> {

    /**
     * 根据角色标识查询已存在的数量
     *
     * @param mark
     * @return
     */
    @Select(" SELECT COUNT(*) FROM t_role WHERE mark = #{mark} ")
    Integer getCountByMark(@Param("mark") String mark);

    /**
     * 根据用户id查询用户所拥有的全部角色 id
     *
     * @param userId
     * @return
     */
    @Select(" SELECT r.id FROM t_role r RIGHT JOIN t_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId} ")
    List<String> selectIdByUserId(@Param("userId") Long userId);

    /**
     * 查询全部角色列表
     *
     * @return
     */
    @Select(" SELECT id AS value,name AS label FROM t_role ")
    List<SelectOption> selectAll();

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    Page<RoleVo> selectPageList(Page<RoleVo> page, @Param("roleName") String roleName);

}