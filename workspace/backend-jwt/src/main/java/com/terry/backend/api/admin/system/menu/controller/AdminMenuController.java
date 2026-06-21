package com.terry.backend.api.admin.system.menu.controller;

import com.terry.backend.api.admin.system.menu.MenuType;
import com.terry.backend.api.admin.system.menu.dto.MenuDTO;
import com.terry.backend.api.admin.system.menu.dto.MenuSearchParam;
import com.terry.backend.api.admin.system.menu.service.AdminMenuService;
import com.terry.backend.api.code.dto.CodeDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminMenuController {
    private final AdminMenuService adminMenuService;

    public AdminMenuController(AdminMenuService adminMenuService) {
        this.adminMenuService = adminMenuService;
    }

    @GetMapping("/admin/system/menu/getMenuList")
    @ResponseBody
    public List<MenuDTO> getMenues(MenuSearchParam param) throws Exception {
        return adminMenuService.select(param);
    }

    @GetMapping("/admin/system/menu/getMenu/{id}")
    @ResponseBody
    public MenuDTO selectByMenuId(@PathVariable String id) throws Exception {
        return adminMenuService.selectByMenuId(id);
    }

    @PostMapping({ "/admin/system/menu", "/admin/system/menu/{id}" })
    @ResponseStatus(code = HttpStatus.CREATED)
    public void save(
            @PathVariable(required = false) String id,
            @RequestBody @Valid MenuDTO entity,
            HttpServletRequest request) throws Exception {
        adminMenuService.save(id, entity, request);
    }

    @GetMapping("/admin/system/menu/{id}")
    public void delete(@PathVariable String id, HttpServletRequest request) throws Exception {
        adminMenuService.delete(id, request);
    }

    @GetMapping("/admin/system/menu/types")
    @ResponseBody
    public List<CodeDTO> getMenuTypes() {
        return MenuType.toCodeList();
    }


}
