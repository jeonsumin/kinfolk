package com.terry.backend.api.admin.system.authority.controller;

import com.terry.backend.api.admin.system.authority.dto.AuthorityDTO;
import com.terry.backend.api.admin.system.authority.dto.AuthorityMemberDTO;
import com.terry.backend.api.admin.system.authority.dto.AuthorityMenuDTO;
import com.terry.backend.api.admin.system.authority.dto.AuthoritySearchParam;
import com.terry.backend.api.admin.system.authority.service.AdminAuthorityService;
import com.terry.backend.api.code.dto.CodeDTO;
import com.terry.backend.web.security.AuthorityValue;
import com.terry.backend.web.security.RoleType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminAuthorityController {
    private final AdminAuthorityService adminAuthorityService;

    public AdminAuthorityController(AdminAuthorityService adminAuthorityService) {
        this.adminAuthorityService = adminAuthorityService;
    }

    @GetMapping("/admin/system/authority/getAuthorityList")
    @ResponseBody
    public List<AuthorityDTO> select(AuthoritySearchParam param) throws Exception {
        return adminAuthorityService.select(param);
    }

    @GetMapping("/admin/system/authority/getAuthority/{id}")
    @ResponseBody
    public AuthorityDTO selectById(@PathVariable String id) throws Exception {
        return adminAuthorityService.selectById(id);
    }

    @PostMapping({ "/admin/system/authority", "/admin/system/authority/{id}" })
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@PathVariable(required = false) String id, @RequestBody @Valid AuthorityDTO entity) throws Exception {
        adminAuthorityService.save(id, entity);
    }

    @GetMapping("/admin/system/authority/{id}")
    public void delete(@PathVariable String id) throws Exception {
        adminAuthorityService.delete(id);
    }

    @GetMapping("/admin/system/authority/types")
    @ResponseBody
    public List<CodeDTO> getAuthorityTypeCodeList() throws Exception {
        return RoleType.toCodeList();
    }

    @GetMapping("/admin/system/authority/values")
    @ResponseBody
    public List<CodeDTO> getAuthorityValueCodeList() throws Exception {
        return AuthorityValue.toCodeList();
    }

    @GetMapping("/admin/system/authority/members")
    @ResponseBody
    public List<AuthorityMemberDTO> selectAuthorityMembers() throws Exception {
        return adminAuthorityService.selectAuthorityMembers();
    }

    @GetMapping("/admin/system/authority/menues")
    @ResponseBody
    public List<AuthorityMenuDTO> selectAuthorityMenues() throws Exception {
        return adminAuthorityService.selectAuthorityMenues();
    }

}
