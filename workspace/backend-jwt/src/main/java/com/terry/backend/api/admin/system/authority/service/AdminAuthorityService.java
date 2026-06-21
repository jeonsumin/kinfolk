package com.terry.backend.api.admin.system.authority.service;

import com.terry.backend.api.admin.system.authority.dto.AuthorityDTO;
import com.terry.backend.api.admin.system.authority.dto.AuthorityMemberDTO;
import com.terry.backend.api.admin.system.authority.dto.AuthorityMenuDTO;
import com.terry.backend.api.admin.system.authority.dto.AuthoritySearchParam;
import com.terry.backend.api.admin.system.authority.exception.AuthorityNotFoundException;
import com.terry.backend.api.admin.system.authority.mapper.AdminAuthorityMapper;
import com.terry.backend.api.admin.system.authority.strategy.AuthorityStrategy;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminAuthorityService {

    private final AdminAuthorityMapper adminAuthorityMapper;
    private static final SerialConfiguration<String> ID_STRATEGY = new AuthorityStrategy();

    public AdminAuthorityService(AdminAuthorityMapper adminAuthorityMapper) {
        this.adminAuthorityMapper = adminAuthorityMapper;
    }

    public List<AuthorityDTO> select(AuthoritySearchParam param) throws Exception{
        return adminAuthorityMapper.select(param);
    }

    public AuthorityDTO selectById(final String id) throws Exception {
        List<AuthorityDTO> entities = adminAuthorityMapper.select(AuthoritySearchParam.builder().id(id).build());
        if (entities == null || entities.isEmpty()) {
            throw new AuthorityNotFoundException(id);
        }
        AuthorityDTO entity = entities.get(0);
        entity.setMembers(selectAuthorityMembers(id));
        entity.setMenues(selectAuthorityMenues(id));
        return entities.get(0);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void save(final String id, final AuthorityDTO entity) throws Exception {
        final boolean isEdit = StringUtils.hasText(id);

        if (isEdit) {
            // Check exists
            selectById(id);
        } else {
            entity.setId(SerialUtil.<String>get("SWC_AUTHOR", ID_STRATEGY));
        }

        adminAuthorityMapper.save(entity);

        /**
         * 권한 사용자 처리
         */
        if (entity.getMembers() == null || entity.getMembers().isEmpty()) {
            adminAuthorityMapper.deleteMembersById(entity.getId(), null);
        } else {
            // Delete members
            entity.getMembers().stream().filter(x -> !x.isMap()).forEach(x -> {
                adminAuthorityMapper.deleteMembersById(entity.getId(), x.getId());
            });

            // Save members
            entity.getMembers().stream().filter(x -> x.isMap()).forEach(x -> {
                x.setAuthorityId(entity.getId());
                adminAuthorityMapper.saveMember(x);
            });
        }

        /**
         * 권한 메뉴 처리
         */
        if (entity.getMenues() == null || entity.getMenues().isEmpty()) {
            adminAuthorityMapper.deleteMenuesById(entity.getId(), null);
        } else {
            // Delete menues first
            entity.getMenues().stream().filter(x -> !x.isMap()).forEach(x -> {
                adminAuthorityMapper.deleteMenuesById(entity.getId(), x.getId());
            });

            // Save menues
            entity.getMenues().stream().filter(x -> x.isMap()).forEach(x -> {
                x.setAuthorityId(entity.getId());
                adminAuthorityMapper.saveMenu(x);
            });
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void delete(final String id) throws Exception {
        AuthorityDTO entity = selectById(id);
        adminAuthorityMapper.delete(entity);
        adminAuthorityMapper.deleteMembersById(entity.getId(), null);
        adminAuthorityMapper.deleteMenuesById(entity.getId(), null);
    }


    public List<AuthorityMemberDTO> selectAuthorityMembers(final String authorityId) throws Exception {
        return adminAuthorityMapper.selectAuthorityMembers(authorityId);
    }

    public List<AuthorityMemberDTO> selectAuthorityMembers() throws Exception {
        return selectAuthorityMembers(null);
    }

    public List<AuthorityMenuDTO> selectAuthorityMenues(final String authorityId) throws Exception {
        // All avaliable user menus
        List<AuthorityMenuDTO> menues = adminAuthorityMapper.selectAuthorityMenues(authorityId);
        // Get root elements
        List<AuthorityMenuDTO> roots = menues
                .stream()
                .filter(x -> x.getParentId() == null)
                .sorted((a, b) -> a.getSort() - b.getSort())
                .collect(Collectors.toList());
        setMenuChildren(roots, menues);
        return roots;
    }
    /**
     * 메뉴 리스트를 트리 목록으로 만든다
     * <dl>
     * <dt>작성일</dt>
     * <dd>2019년 09월 23일</dd>
     * <dt>작성자</dt>
     * <dd>윤지영 <a href="mailto:jyyoon@skyware.co.kr">jyyoon@skyware.co.kr</a></dd>
     * </dl>
     *
     * @param roots  트리 루트 노드
     * @param menues 전체 노드
     */
    private void setMenuChildren(List<AuthorityMenuDTO> roots, List<AuthorityMenuDTO> menues) {
        for (AuthorityMenuDTO root : roots) {
            root
                    .setChildren(menues
                            .stream()
                            .filter(x -> root.getId().equalsIgnoreCase(x.getParentId()))
                            .sorted((a, b) -> a.getSort() - b.getSort())
                            .collect(Collectors.toList()));
            setMenuChildren(root.getChildren(), menues);
        }
    }

    public List<AuthorityMenuDTO> selectAuthorityMenues() throws Exception {
        return selectAuthorityMenues(null);
    }

}
