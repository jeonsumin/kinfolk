package com.terry.backend.web.file.controller;

import com.terry.backend.web.file.config.FileConfig;
import com.terry.backend.web.file.dto.FileDTO;
import com.terry.backend.web.file.exception.FileNotFoundException;
import com.terry.backend.web.file.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
public class FileController {

    protected static final DateFormat df = new SimpleDateFormat("yyyyMMdd");

    private final FileService service;
    private final FileConfig config;

    public FileController(
            FileService fileService,
            FileConfig config
    ) {
        this.service = fileService;
        this.config = config;
    }

    @PostMapping("/file")
    public @ResponseBody List<FileDTO> upload(MultipartHttpServletRequest request) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Start upload files...");

        String basepath   = config.getBasename();
        String middlepath = df.format(new Date());
        String path       = StringUtils.collectionToDelimitedString(Arrays.asList(basepath, middlepath), config.getDelim());
        File dir        = new File(path);

        if (log.isDebugEnabled())
            log.debug("Check folder at `" + path + "' ... " + (dir.isDirectory() ? "exist" : "not exist"));

        if (!dir.isDirectory()) {
            dir.mkdirs();
            if (log.isDebugEnabled())
                log.debug("Create new folders at `" + path + "'");
        }

        Iterator<String> filenames = request.getFileNames();
        List<FileDTO>    files     = new ArrayList<FileDTO>();

        if (log.isDebugEnabled())
            log.debug("Start insert files");

        while (filenames.hasNext()) {
            MultipartFile file = request.getFile(filenames.next());
            if (log.isDebugEnabled())
                log.debug("Adding file `" + file.getOriginalFilename() + "'");
            files.add(service.insert(path, middlepath, file));
        }

        return files;
    }


    @GetMapping("/file/{fileId}")
    public void download(@PathVariable String fileId, @RequestParam(name = "attachType", required = false) String attachType,
                         HttpServletResponse response, Principal principal) throws Exception {
        FileDTO f = service.selectByFileId(fileId);

        if (f == null) {
            throw new FileNotFoundException();
        }

        String basePath = config.getBasename();
        String filepath = StringUtils.collectionToDelimitedString(Arrays.asList(basePath, f.getFilePath()), config.getDelim());

//        if (log.isDebugEnabled())

        log.info("Try to find file from {}", filepath);

        File file = new File(filepath);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        attachType = StringUtils.isEmpty(attachType) ? "attachment" : attachType;

        response.setHeader("Content-Disposition", attachType + ";filename=\"" + URLEncoder.encode(f.getFileName(), "UTF-8") + "\"");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength((int) file.length());
        response.setContentType(f.getFileType());

        FileInputStream fis = new FileInputStream(file);
        OutputStream os  = response.getOutputStream();

        StreamUtils.copy(fis, os);

        fis.close();
        os.close();
    }
}
