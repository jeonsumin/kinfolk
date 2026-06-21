package com.terry.backend.web.file.mapper;

import com.terry.backend.web.file.dto.FileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileMapper {

    FileDTO selectByFileId(@Param(value = "fileId") String fileId);

    void insert(FileDTO entity);
}
