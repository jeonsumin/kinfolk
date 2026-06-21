package com.terry.backend.core.serial.mapper;

import com.terry.backend.core.serial.dto.SerialDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface CoreSerialMapper {

    Optional<SerialDTO> findOne(@Param(value = "id") String id);

    void save(SerialDTO entity);
}
