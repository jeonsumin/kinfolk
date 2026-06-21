package com.terry.backend.core.excel.controller;

import com.terry.backend.core.excel.config.ExcelConfig;
import com.terry.backend.core.excel.dto.ExcelDTO;
import com.terry.backend.core.excel.mapper.ExcelMapper;
import com.terry.backend.web.file.exception.FileNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
public class ExcelController {
    private final ExcelMapper excelMapper;
    private final ExcelConfig config;

    public ExcelController(
            ExcelMapper excelMapper,
            ExcelConfig config
    ) {
        this.excelMapper = excelMapper;
        this.config = config;
    }

    @GetMapping("${backend.excel.context-path:/excel}/download/{id}")
    public void download(HttpServletResponse response, @PathVariable String id) throws Exception{
        ExcelDTO entity = excelMapper.selectById(id);

        if (entity == null) {
            throw new FileNotFoundException();
        }

        String base = config.getTemporaryPath();
        base.replaceAll("\\\\", "/");
        if (!base.endsWith("/")) {
            base = base + "/";
        }

        File file = new File(String.format("%s%s.xlsx", base, entity.getFileId()));

        if (!file.exists())
            throw new FileNotFoundException();

        // Open input stream;
        InputStream  is = new FileInputStream(file);
        OutputStream os = response.getOutputStream();

        String headerKey   = "Content-Disposition";
        String headerValue = "attachment;filename=\"" + URLEncoder.encode(entity.getFileName(), StandardCharsets.UTF_8.name()) + "\"";

        response.setHeader(headerKey, headerValue);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(entity.getFileType());
        response.setContentLength((int) file.length());

        os.write(IOUtils.toByteArray(is));

        is.close();
        os.close();

    }
}
