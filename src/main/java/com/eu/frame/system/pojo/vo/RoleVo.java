package com.eu.frame.system.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * 角色
 *
 * @author jiangxd
 */
@Data
public class RoleVo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色备注
     */
    private String mark;

    /**
     * 角色关联的权限标识
     */
    private List<String> authority;

}
