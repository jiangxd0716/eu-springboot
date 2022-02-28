package com.eu.frame.system.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * key-value
 *
 * @author jiangxd
 */
@Data
public class SelectOption {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long value;

    /**
     * name
     */
    private String label;

    /**
     * 二级菜单
     */
    private List<SelectOption> options;

}
