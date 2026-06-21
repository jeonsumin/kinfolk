package com.terry.backend.web.file.service;

import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import com.terry.backend.web.file.config.FileConfig;
import com.terry.backend.web.file.dto.FileDTO;
import com.terry.backend.web.file.mapper.FileMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

@Slf4j
@Service
public class FileService {

    private final FileConfig config;
    private final FileMapper fileMapper;

    public FileService(
            FileConfig config,
            FileMapper fileMapper
    ) {
        this.config = config;
        this.fileMapper = fileMapper;
    }

    public FileDTO insert(final String path, final String middlepath, final MultipartFile file) throws Exception {
        String fileId = SerialUtil.get("DB_FILE", new AbstractStringSerialConfiguration() {
            @Override
            public String getFormatString() {
                return config.getFormat();
            }

            @Override
            public String getDateFormat() {
                return config.getDateFormat();
            }

            @Override
            public String getCompareDateFormat() {
                return config.getCompareDateFormat();
            }
        });


        String filename = StringUtils
                .collectionToDelimitedString(Arrays.asList(fileId, FilenameUtils.getExtension(file.getOriginalFilename())), ".");

        log.debug("Prepare to insert file `" + file.getOriginalFilename() + "' => `" + fileId + "'");

        File             target = new File(path, filename);
        InputStream      is     = file.getInputStream();
        FileOutputStream fos    = new FileOutputStream(target);

        StreamUtils.copy(is, fos);
        is.close();
        fos.close();

        FileDTO data = FileDTO
                .builder()
                .fileId(fileId)
                .fileName(file.getOriginalFilename())
                .filePath(StringUtils.collectionToDelimitedString(Arrays.asList(middlepath, filename),"/"))
                .fileSize((long) file.getBytes().length)
                .fileType(file.getContentType())
                .createId(SessionUtils.getUserId())
                .build();

        fileMapper.insert(data);

        return data;
    }

    public FileDTO selectByFileId(final String fileId) throws Exception {
        FileDTO data = fileMapper.selectByFileId(fileId);
        if (data == null) {
            throw new FileNotFoundException();
        }
        return data;
    }

}
