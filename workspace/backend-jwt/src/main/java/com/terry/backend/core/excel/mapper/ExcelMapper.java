package com.terry.backend.core.excel.mapper;

import com.terry.backend.core.excel.dto.ExcelDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExcelMapper {
    ExcelDTO selectById(@Param(value = "id") String id);

    void insert(ExcelDTO entity);
}
