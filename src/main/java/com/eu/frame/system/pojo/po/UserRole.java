package com.eu.frame.system.pojo.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户角色关联表
 *
 * @author jiangxd
 */
@Data
@TableName("t_user_role")
public class UserRole {

    @TableId("id")
    private Long id;

    /**
     * 用户 id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 角色 id
     */
    @TableField("role_id")
    private Long roleId;

}
