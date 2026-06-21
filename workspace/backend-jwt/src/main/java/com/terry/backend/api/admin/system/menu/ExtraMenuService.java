package com.terry.backend.api.admin.system.menu;

import com.terry.backend.api.admin.system.menu.dto.MenuDTO;

import java.util.List;

public interface ExtraMenuService {

    List<MenuDTO> selectMenues(String username);

}
