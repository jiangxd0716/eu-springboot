package com.eu.frame.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eu.frame.system.pojo.po.UserRole;
import com.eu.frame.system.pojo.vo.SelectOption;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色关联
 *
 * @author jiangxd
 */
@Repository
public interface UserRoleDao extends BaseMapper<UserRole> {

    /**
     * 根据用户 id 删除其全部的角色
     *
     * @param userId
     */
    @Delete(" DELETE FROM t_user_role WHERE user_id = #{userId} ")
    void deleteByUserId(@Param("userId") Long userId);


    @Select("select b.id as value,b.name as label from t_user_role a left join t_role b on a.role_id = b.id where a.user_id = #{userId}")
    List<SelectOption> selectUserRoleInfo(@Param("userId") Long userId);

}
