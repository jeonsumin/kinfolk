package com.terry.backend.api.user.controller;

import com.terry.backend.api.user.dto.UpdateDisplayNameRequest;
import com.terry.backend.api.user.dto.UserProfileDTO;
import com.terry.backend.api.user.service.UserService;
import com.terry.backend.web.controller.ApiRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserController extends ApiRestController {

    private final UserService service;

    @GetMapping("/user/me")
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 반환한다.")
    public UserProfileDTO getMyProfile() {
        return service.getMyProfile();
    }

    @PatchMapping("/user/me")
    @Operation(summary = "이름 수정", description = "현재 로그인한 사용자의 이름을 수정하고 수정된 프로필을 반환한다.")
    public UserProfileDTO updateDisplayName(@Valid @RequestBody UpdateDisplayNameRequest request) {
        return service.updateDisplayName(request.getDisplayName());
    }
}
