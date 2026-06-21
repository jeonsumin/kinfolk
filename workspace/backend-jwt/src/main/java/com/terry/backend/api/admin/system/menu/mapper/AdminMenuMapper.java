package com.terry.backend.api.admin.system.menu.mapper;

import com.terry.backend.api.admin.system.menu.dto.MenuDTO;
import com.terry.backend.api.admin.system.menu.dto.MenuSearchParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMenuMapper {
    List<MenuDTO> select(MenuSearchParam param);

    void insert(MenuDTO entity);

    void update(MenuDTO entity);

    void deleteAuthorityLink(MenuDTO entity);

    void delete(MenuDTO entity);

}
