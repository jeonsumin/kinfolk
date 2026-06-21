package com.terry.backend.api.admin.system.member.controller;

import com.terry.backend.api.admin.system.member.dto.MemberDTO;
import com.terry.backend.api.admin.system.member.dto.MemberSearchParam;
import com.terry.backend.api.admin.system.member.service.AdminMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminMemberController {
    private final AdminMemberService adminMemberService;

    public AdminMemberController(AdminMemberService adminMemberService) {
        this.adminMemberService = adminMemberService;
    }


    @GetMapping("/admin/system/member/getMemberList")
    @ResponseBody
    public List<MemberDTO> select(MemberSearchParam param) throws Exception {
        return adminMemberService.select(param);
    }

    @GetMapping("/admin/system/member/getMember/{id}")
    @ResponseBody
    public MemberDTO selectById(@PathVariable String id) throws Exception {
        return adminMemberService.selectById(id);
    }

    @PostMapping({"/admin/system/member", "/admin/system/member/{id}"})
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@PathVariable(required = false) String id, @RequestBody MemberDTO entity) throws Exception {
        adminMemberService.save(id, entity);
    }

    @GetMapping("/admin/system/member/{id}")
    public void delete(@PathVariable String id) throws Exception {
        adminMemberService.delete(id);
    }

    @GetMapping("/admin/system/member")
    public void delete(@RequestParam(value = "idList", required = true) String[] idList)
            throws Exception {
        adminMemberService.deleteByList(idList);
    }

    @GetMapping("/admin/system/member/validation/loginId")
    public void checkLoginId(@RequestParam(value = "loginId") String loginId) throws Exception {
        adminMemberService.checkLoginId(loginId);
    }

    @PutMapping("/admin/system/member/validation/email")
    public void checkEmail(@RequestBody String email) throws Exception {
        adminMemberService.checkEmail(email);
    }
}
