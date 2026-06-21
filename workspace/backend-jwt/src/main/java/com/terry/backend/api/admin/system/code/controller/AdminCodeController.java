package com.terry.backend.api.admin.system.code.controller;

import com.terry.backend.api.admin.system.code.CodeType;
import com.terry.backend.api.admin.system.code.dto.CodeResponseEntity;
import com.terry.backend.api.admin.system.code.dto.CodeSearchParam;
import com.terry.backend.api.admin.system.code.service.AdminCodeService;
import com.terry.backend.api.code.dto.CodeDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "Commons Code", description = "공통코드 API")
public class AdminCodeController {
    private final AdminCodeService service;

    public AdminCodeController(AdminCodeService service) {
        this.service = service;
    }

    @GetMapping("/admin/system/code/getCodeList")
    @ResponseBody
    public CodeResponseEntity select(CodeSearchParam param) throws Exception {
        return service.select(param);
    }

    @GetMapping("/admin/system/code/getCode/{id}")
    @ResponseBody
    public CodeDTO selectById(@PathVariable String id) throws Exception {
        return service.selectById(id);
    }

    @PostMapping({ "/admin/system/code", "/admin/system/code/{id}" })
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @PathVariable(required = false) String id,
            @RequestBody @Valid CodeDTO entity) throws Exception {
        service.save(id, entity);
    }

    @GetMapping("/admin/system/code/{id}")
    public void delete(@PathVariable String id) throws Exception {
        service.delete(id);
    }

    @GetMapping("/admin/system/code")
    public void deleteByList(@RequestParam String[] idList) throws Exception {
        service.deleteByList(idList);
    }

    @GetMapping("/admin/system/code/options/code-type")
    @ResponseBody
    public List<CodeDTO> getCodeTypeList() throws Exception {
        return CodeType.toCodeList();
    }

    @PostMapping("/admin/system/code/validation/code")
    public void checkCode(@RequestBody CodeDTO entity) throws Exception {
        service.checkCode(entity.getParentId(), entity.getCode());
    }

}
