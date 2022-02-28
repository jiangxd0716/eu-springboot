package com.eu.frame.system.pojo.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色权限关系表
 *
 * @author jiangxd
 */
@Data
@TableName("t_role_authority")
public class RoleAuthority {

    @TableId("id")
    private Long id;

    /**
     * 角色 id
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 权限 id
     */
    @TableField("authority_id")
    private Long authorityId;

}
