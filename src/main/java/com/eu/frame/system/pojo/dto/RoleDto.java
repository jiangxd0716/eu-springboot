package com.eu.frame.system.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色
 *
 * @author jiangxd
 */
@Data
public class RoleDto {

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
    private List<Long> authority;

}
