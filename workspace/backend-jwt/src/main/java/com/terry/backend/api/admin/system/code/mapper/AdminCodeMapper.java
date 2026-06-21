package com.terry.backend.api.admin.system.code.mapper;

import com.terry.backend.api.admin.system.code.dto.CodeSearchParam;
import com.terry.backend.api.code.dto.CodeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminCodeMapper {

    /**
     * 공통코드 조회
     * @return 공통 코드 목록
     */
    String getPathFromParentId(@Param(value = "parentId") String parentId);

    List<CodeDTO> select(CodeSearchParam param);

    CodeDTO selectById(@Param(value = "id") String id);

    void insert(CodeDTO entity);

    void update(CodeDTO entity);

    void delete(CodeDTO entity);

    void updateChildrenCodeType(CodeDTO entity);

    void updateCodeTreeStructure();

}
