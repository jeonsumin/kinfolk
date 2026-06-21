package com.terry.backend.api.admin.system.code.service;

import com.terry.backend.api.admin.system.code.dto.CodeResponseEntity;
import com.terry.backend.api.admin.system.code.dto.CodeSearchParam;
import com.terry.backend.api.admin.system.code.exception.CodeAreadyExists;
import com.terry.backend.api.admin.system.code.exception.CodeNotFound;
import com.terry.backend.api.admin.system.code.mapper.AdminCodeMapper;
import com.terry.backend.api.admin.system.code.strategy.CodeStrategy;
import com.terry.backend.api.code.dto.CodeDTO;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AdminCodeService {

    private final AdminCodeMapper mapper;
    private static final SerialConfiguration<String> ID_STRATEGY = new CodeStrategy();

    public AdminCodeService(
            AdminCodeMapper mapper
    ) {
        this.mapper = mapper;
    }

    public CodeResponseEntity select(CodeSearchParam param) throws Exception {
        List<CodeDTO> contents = mapper.select(param);
        return CodeResponseEntity
                .builder()
                .path(mapper.getPathFromParentId(param.getParentId()))
                .contents(contents)
                .build();
    }

    public CodeDTO selectById(@Param(value = "id") String id) throws Exception {
        CodeDTO entity = mapper.selectById(id);
        if (entity == null) {
            throw new CodeNotFound(id);
        }
        return entity;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void save(final String id, final CodeDTO entity) throws Exception {
        final boolean isEdit = StringUtils.hasText(id);

        if (isEdit) {
            /**
             * 코드가 존재하는지 체크한다
             */
            CodeDTO oldEntity = selectById(id);
            /**
             * 코드 유형이 변경됨
             */
            if (oldEntity.getType() != entity.getType()) {
                mapper.updateChildrenCodeType(entity);
            }

            mapper.update(entity);
        } else {
            entity.setId(SerialUtil.get(CodeStrategy.ID, ID_STRATEGY));

            /**
             * 코드 중복을 체크해본다
             */
            checkCode(entity.getParentId(), entity.getCode());

            mapper.insert(entity);
        }

        mapper.updateCodeTreeStructure();

        // Get result
        CodeDTO result = selectById(entity.getId());
//         Remove modified cache
//        CodeUtil.removeCache(result.getPath());
    }

    public void delete(final String id, final String historyId) throws Exception {
        CodeDTO entity = CodeDTO
                .builder()
                .id(id)
                .build();

        mapper.delete(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void delete(final String id) throws Exception {
        delete(id, "");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void deleteByList(final String[] idList) throws Exception {
        if (idList == null || idList.length == 0) {
            List<CodeDTO> all = mapper.select(new CodeSearchParam());
            for (int i = 0; i < all.size(); i++) {
                delete(all.get(0).getId(), "");
            }
        } else {
            for (int i = 0; i < idList.length; i++) {
                delete(idList[i], "");
            }
        }
    }

    public void checkCode(final String parentId, final String code) throws CodeAreadyExists {
        List<CodeDTO> check = mapper
                .select(CodeSearchParam.builder().parentId(parentId).matchCode(code).build());
        if (check != null && !check.isEmpty()) {
            throw new CodeAreadyExists(code);
        }
    }

}
