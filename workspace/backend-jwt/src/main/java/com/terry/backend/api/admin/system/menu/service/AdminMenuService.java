package com.terry.backend.api.admin.system.menu.service;

import com.terry.backend.api.admin.system.menu.dto.MenuDTO;
import com.terry.backend.api.admin.system.menu.dto.MenuSearchParam;
import com.terry.backend.api.admin.system.menu.exception.MenuCodeAlreadyExists;
import com.terry.backend.api.admin.system.menu.exception.MenuNotFoundException;
import com.terry.backend.api.admin.system.menu.mapper.AdminMenuMapper;
import com.terry.backend.api.admin.system.menu.strategy.MenuStrategy;
import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminMenuService {

    private static final SerialConfiguration<String> ID_STRATEGY = new MenuStrategy();

    private final AdminMenuMapper adminMenuMapper;

    public AdminMenuService(AdminMenuMapper adminMenuMapper) {
        this.adminMenuMapper = adminMenuMapper;
    }

    public List<MenuDTO> select(MenuSearchParam param) throws Exception {
        // All avaliable user menus
        List<MenuDTO> menues = adminMenuMapper.select(param);
        // Get root elements
        List<MenuDTO> roots = menues
                .stream()
                .filter(x -> x.getParentId() == null)
                .sorted((a, b) -> a.getSort() - b.getSort())
                .collect(Collectors.toList());
        setMenuChildren(roots, menues);
        return roots;
    }
    public MenuDTO selectByMenuId(final String id) throws Exception {
        List<MenuDTO> contents = adminMenuMapper
                .select(MenuSearchParam
                        .builder()
                        .id(id)
                        .build());
        if (contents == null || contents.isEmpty()) {
            throw new MenuNotFoundException(id);
        }
        return contents.get(0);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void save(final String id, final MenuDTO entity, HttpServletRequest request) throws Exception {
        final boolean isEdit = StringUtils.hasText(id);

        if (isEdit) {
            update(id, entity);
        } else {
            insert(entity);
        }

        if (isEdit) {
            adminMenuMapper.update(entity);
        } else {
            adminMenuMapper.insert(entity);
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void delete(final String id, HttpServletRequest request) throws Exception {
        MenuDTO entity = selectByMenuId(id);

        adminMenuMapper.delete(entity);
    }

    private void insert(final MenuDTO entity) throws Exception {
        /**
         * Code validation
         */
        List<MenuDTO> code = adminMenuMapper
                .select(MenuSearchParam
                        .builder()
                        .code(entity.getCode())
                        .build());

        if (code != null && !code.isEmpty()) {
            throw new MenuCodeAlreadyExists();
        }

        entity.setId(SerialUtil.get(MenuStrategy.ID, ID_STRATEGY));
        entity.setCreateId(SessionUtils.getUserId());
        entity.setUpdateId(SessionUtils.getUserId());
    }

    private void update(final String id, final MenuDTO entity) throws Exception {
        // Check exists
        selectByMenuId(id);

        List<MenuDTO> code = adminMenuMapper
                .select(MenuSearchParam
                        .builder()
                        .code(entity.getCode())
                        .build())
                .stream()
                .filter(x -> !x.getId().equalsIgnoreCase(id))
                .collect(Collectors.toList());

        if (code != null && !code.isEmpty()) {
            throw new MenuCodeAlreadyExists();
        }

        entity.setUpdateId(SessionUtils.getUserId());
        entity.setId(id);
    }

    private void setMenuChildren(List<MenuDTO> roots, List<MenuDTO> menues) {
        for (MenuDTO root : roots) {
            root
                    .setChildren(menues
                            .stream()
                            .filter(x -> root.getId().equalsIgnoreCase(x.getParentId()))
                            .sorted((a, b) -> a.getSort() - b.getSort())
                            .collect(Collectors.toList()));
            setMenuChildren(root.getChildren(), menues);
        }
    }
}
