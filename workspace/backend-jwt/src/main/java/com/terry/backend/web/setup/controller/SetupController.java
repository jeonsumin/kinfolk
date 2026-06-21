package com.terry.backend.web.setup.controller;

import com.terry.backend.web.setup.dto.AdminSetupRequest;
import com.terry.backend.web.setup.service.SetupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Setup", description = "시스템 초기 설정 API")
@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupController {

    private final SetupService setupService;

    @Operation(summary = "관리자 계정 존재 여부 확인", description = "시스템에 관리자 계정이 존재하는지 확인합니다.")
    @GetMapping("/admin-exists")
    public ResponseEntity<Map<String, Boolean>> checkAdminExists() {
        boolean adminExists = setupService.checkAdminExists();
        return ResponseEntity.ok(Map.of("adminExists", adminExists));
    }

    @Operation(summary = "첫 번째 관리자 계정 생성", description = "시스템에 첫 번째 관리자 계정을 생성합니다.")
    @PostMapping("/create-admin")
    public ResponseEntity<Map<String, String>> createFirstAdmin(@RequestBody AdminSetupRequest request) {
        try {
            setupService.createFirstAdmin(request);
            return ResponseEntity.ok(Map.of("message", "관리자 계정이 성공적으로 생성되었습니다."));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "관리자 계정 생성 중 오류가 발생했습니다."));
        }
    }
}