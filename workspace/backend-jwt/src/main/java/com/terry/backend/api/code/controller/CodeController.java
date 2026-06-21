package com.terry.backend.api.code.controller;

import com.terry.backend.api.code.dto.CodeDTO;
import com.terry.backend.api.code.service.CodeService;
import com.terry.backend.web.controller.ApiRestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "Commons Code", description = "공통코드 API")
public class CodeController extends ApiRestController {
    private final CodeService service;

    public CodeController(CodeService service) {
        this.service = service;
    }


    @GetMapping("/code")
    @ResponseBody
    public List<CodeDTO> findByPath(@RequestParam(required = true) String path) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Requested code list by %s", path));
        }
        return service.findByPath(path);
    }

}
