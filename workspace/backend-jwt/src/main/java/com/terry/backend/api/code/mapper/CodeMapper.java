package com.terry.backend.api.code.mapper;

import com.terry.backend.api.code.dto.CodeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CodeMapper {
    List<CodeDTO> findByPath(@Param(value = "path") String path);

}
