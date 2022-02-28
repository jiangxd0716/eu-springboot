package com.eu.frame.system.pojo.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限表
 *
 * @author jiangxd
 */
@Data
@TableName("t_authority")
public class Authority {

    @TableId("id")
    private Long id;

    /**
     * 权限名称
     */
    @TableField("name")
    private String name;

    /**
     * 权限标识
     */
    @TableField("mark")
    private String mark;

}
