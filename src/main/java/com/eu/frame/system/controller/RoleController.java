package com.eu.frame.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eu.frame.system.pojo.dto.RoleDto;
import com.eu.frame.system.pojo.vo.RoleVo;
import com.eu.frame.system.pojo.vo.SelectOption;
import com.eu.frame.system.service.RoleService;
import com.eu.frame.common.wrapper.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色
 *
 * @author jiangxd
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;


    /**
     * 查询全部角色列表
     *
     * @return
     */
    @GetMapping("list")
    @Authority(mark = "role:list", name = "查询全部角色列表")
    public List<SelectOption> list() {
        return this.roleService.list();
    }

    /**
     * 分页列表查询
     *
     * @param pageSize  每页查询多少条
     * @param pageIndex 要查询第多少页 , 从 1 开始
     * @param roleName  角色名称
     * @return
     */
    @PostMapping("{pageSize}/{pageIndex}")
    @Authority(mark = "role:list", name = "分页列表查询")
    public Page<RoleVo> list(@PathVariable Integer pageIndex,
                             @PathVariable Integer pageSize,
                             @RequestParam(required = false) String roleName) {
        Page<RoleVo> page = new Page<>(pageIndex, pageSize);
        this.roleService.list(page, roleName);
        return page;
    }

    /**
     * 新增
     *
     * @param dto
     */
    @PutMapping("")
    @Authority(mark = "role:add", name = "角色新增")
    public void add(@Valid @RequestBody RoleDto dto) {
        this.roleService.add(dto);
    }

    /**
     * 修改
     *
     * @param dto
     */
    @PatchMapping("{roleId}")
    @Authority(mark = "role:edit", name = "角色修改")
    public void edit(@Valid @RequestBody RoleDto dto,
                     @PathVariable Long roleId) {
        this.roleService.edit(roleId, dto);
    }

    /**
     * 根据主键 id 删除一条记录
     *
     * @param roleId
     */
    @DeleteMapping("{roleId}")
    @Authority(mark = "role:del", name = "角色删除")
    public void remove(@PathVariable Long roleId) {
        this.roleService.remove(roleId);
    }

    /**
     * 查询用户的角色列表 id
     *
     * @param userId 用户 id
     * @return
     */
    @GetMapping("user/{userId}")
    @Authority(mark = "role:user:list", name = "查询用户的角色列表")
    public List<String> user(@PathVariable Long userId) {
        return this.roleService.selectUserRole(userId);
    }

    /**
     * 修改用户角色
     *
     * @param userId
     * @param role
     */
    @PatchMapping("user/{userId}")
    @Authority(mark = "role:user:edit", name = "修改用户角色")
    public void user(@PathVariable Long userId,
                     @RequestBody List<Long> role) {
        this.roleService.updateUserRole(userId, role);
    }

}
