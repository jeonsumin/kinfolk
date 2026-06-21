package com.terry.backend.web.security.controller;

import com.terry.backend.web.security.MenuAuthority;
import com.terry.backend.web.security.RoleType;
import com.terry.backend.web.security.service.SecurityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSecurityController {

    private final SecurityService service;

    public WebSecurityController(SecurityService securityService) {
        this.service = securityService;
    }

    @GetMapping("/security/{menuCode}")
    @ResponseBody
    public MenuAuthority selectUserMenuAuthority(@PathVariable String menuCode) throws Exception {
        return service.selectUserMenuAuthority(menuCode);
    }

    @GetMapping("/security")
    public RoleType selectUserAuthority(@PathVariable String menuCode) throws Exception {
        return null;
    }
}
