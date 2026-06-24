package com.terry.backend.web.security.controller;

import com.terry.backend.api.admin.system.member.dto.MemberDTO;
import com.terry.backend.api.admin.system.member.service.AdminMemberService;
import com.terry.backend.web.security.SignupRequest;
import com.terry.backend.web.security.service.SecurityService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WebLoginControllerTest {

    @Test
    void signupCreatesAnEnabledMember() throws Exception {
        AdminMemberService memberService = mock(AdminMemberService.class);
        WebLoginController controller = new WebLoginController(mock(SecurityService.class), memberService);
        SignupRequest request = new SignupRequest();
        request.setUsername("family.user");
        request.setPassword("secret");
        request.setName("홍길동");

        var response = controller.signup(request);
        var member = forClass(MemberDTO.class);
        verify(memberService).save(isNull(), member.capture());

        assertEquals(201, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals("family.user", member.getValue().getLoginId());
        assertEquals("홍길동", member.getValue().getName());
        assertEquals("Y", member.getValue().getUse());
        assertEquals("N", member.getValue().getLock());
    }
}
