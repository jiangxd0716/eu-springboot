package com.eu.frame.system.pojo.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色表
 *
 * @author jiangxd
 */
@Data
@TableName("t_role")
public class Role {

    @TableId("id")
    private Long id;

    /**
     * 角色名称
     */
    @TableField("name")
    private String name;

    /**
     * 角色标识
     */
    @TableField("mark")
    private String mark;

}
