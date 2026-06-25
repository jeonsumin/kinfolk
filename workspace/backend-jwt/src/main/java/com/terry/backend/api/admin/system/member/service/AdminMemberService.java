package com.terry.backend.api.admin.system.member.service;

import com.terry.backend.api.admin.system.member.dto.MemberAuthorityDTO;
import com.terry.backend.api.admin.system.member.dto.MemberDTO;
import com.terry.backend.api.admin.system.member.dto.MemberSearchParam;
import com.terry.backend.api.admin.system.member.exception.MemberEmailAlreadyExists;
import com.terry.backend.api.admin.system.member.exception.MemberLoginIdAlreadyExists;
import com.terry.backend.api.admin.system.member.exception.MemberNotFound;
import com.terry.backend.api.admin.system.member.exception.MemberPasswordIsEmpty;
import com.terry.backend.api.admin.system.member.mapper.AdminMemberAuthorityMapper;
import com.terry.backend.api.admin.system.member.mapper.AdminMemberMapper;
import com.terry.backend.api.admin.system.member.strategy.MemberStrategy;
import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminMemberService {
    private static final SerialConfiguration<String> ID_STRATEGY = new MemberStrategy();

    private final AdminMemberMapper adminMemberMapper;
    private final AdminMemberAuthorityMapper adminMemberAuthorityMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminMemberService(AdminMemberMapper adminMemberMapper, AdminMemberAuthorityMapper adminMemberAuthorityMapper, PasswordEncoder passwordEncoder) {
        this.adminMemberMapper = adminMemberMapper;
        this.adminMemberAuthorityMapper = adminMemberAuthorityMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<MemberDTO> select(MemberSearchParam param) throws Exception {
        return adminMemberMapper.select(param);
    }

    public MemberDTO selectById(final String id) throws Exception {
        MemberDTO entity = adminMemberMapper.selectById(id);
        if (entity == null) {
            throw new MemberNotFound(id);
        }
        entity.setPassword(null);
        entity.setAuthorities(adminMemberAuthorityMapper.selectAuthority(id));
        return entity;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void save(final String id, final MemberDTO entity) throws Exception {
        final boolean isEdit = StringUtils.hasText(id);

        if (isEdit) {
            editMember(id, entity);
            adminMemberMapper.update(entity);
        } else {
            createMember(entity);
            adminMemberMapper.insert(entity);
        }

        // 권한 목록 처리
        if (!CollectionUtils.isEmpty(entity.getAuthorities())) {
            // 먼저 삭제를 처리한다
            Set<String> authorityIdSet = entity.getAuthorities().parallelStream()
                    .filter(x -> !x.isMap() && StringUtils.hasText(x.getAuthorityId()))
                    .map(x -> "'" + x.getAuthorityId().toUpperCase() + "'").collect(Collectors.toSet());
            adminMemberAuthorityMapper.deleteByUserIdAndAuthorityIds(entity.getId(),
                    String.join(",", authorityIdSet));

            // 맵핑된 권한은 저장한다
            Set<MemberAuthorityDTO> aSet = entity.getAuthorities().parallelStream().filter(x -> x.isMap())
                    .collect(Collectors.toSet());
            for (Iterator<MemberAuthorityDTO> itr = aSet.iterator(); itr.hasNext(); ) {
                MemberAuthorityDTO authority = itr.next();
                authority.setUserId(entity.getId());
                adminMemberAuthorityMapper.save(authority);
            }
        } else {
            // 사용자 권한을 모두 삭제
            adminMemberAuthorityMapper.delete(new MemberAuthorityDTO(entity.getId()));
        }

        // Update Session Cache
        SessionUtils.updateUserCacheMap(entity.getId(), entity.getName(), entity.getLoginId());
    }


    public void delete(final String id, final String historyId) throws Exception {
        MemberDTO entity = MemberDTO.builder().id(id).build();

        /**
         * 이력 생성
         */

        /**
         * 사용자 권한 정보 삭제
         */
        adminMemberMapper.deleteAuthorityLink(entity);
        /**
         * 사용자 삭제
         */
        adminMemberMapper.delete(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void delete(final String id) throws Exception {
        delete(id, "");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void deleteByList(final String[] idList) throws Exception {
        List<String> collection = new ArrayList<>(new HashSet<>(Arrays.asList(idList)));
        if (collection == null || collection.isEmpty()) {
            List<MemberDTO> all = adminMemberMapper.select(new MemberSearchParam());
            for (int i = 0; i < all.size(); i++) {
                delete(all.get(i).getId(), "");
            }
        } else {
            for (int i = 0; i < idList.length; i++) {
                delete(idList[i], "");
            }
        }
    }

    /**
     * 사용자 정보 생성
     *
     * @param entity 생성될 사용자 Object
     * @throws Exception 생성 실패
     */
    private void createMember(final MemberDTO entity) throws Exception {
        entity.setId(SerialUtil.get(MemberStrategy.ID, ID_STRATEGY));

        /**
         * 로그인ID 중복 확인
         */
        checkLoginId(entity.getLoginId());

        /**
         * 이메일 중복 확인
         */
        if (StringUtils.hasText(entity.getEmail())) {
            checkEmail(entity.getEmail());
        }

        /**
         * 패스워드 암호화 처리
         */
        if (StringUtils.hasText(entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        } else {
            throw new MemberPasswordIsEmpty();
        }
    }


    /**
     * 사용자 정보 수정
     *
     * @param id     사용자ID
     * @param entity 수정될 사용자 Object
     * @throws Exception 수정 실패
     */
    private void editMember(final String id, final MemberDTO entity) throws Exception {
        // Try get member information first
        MemberDTO oldEntity = adminMemberMapper.selectById(id);
        if (oldEntity == null) {
            throw new MemberNotFound(id);
        }

        entity.setId(id);

        if (StringUtils.hasText(entity.getEmail())) {
            /**
             * 이메일 정보가 변경되었을 경우, 이메일 정보를 체크한다
             */
            if (!oldEntity.getEmail().equalsIgnoreCase(entity.getEmail())) {
                checkEmail(entity.getEmail());
            }
        }

        /**
         * 패스워드 변경 처리
         */
        if (StringUtils.hasText(entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
        /**
         * 패스워드 미 변경 시
         */
        else {
            entity.setPassword(oldEntity.getPassword());
        }

        System.out.println("Old password : " + oldEntity.getPassword());
        System.out.println("New password : " + entity.getPassword());
    }

    /**
     * 사용자 로그인 ID 중복 여부 확인
     *
     * @param loginId 로그인ID
     * @throws MemberLoginIdAlreadyExists 사용자 로그인ID가 이미 존재함
     */
    public void checkLoginId(final String loginId) throws MemberLoginIdAlreadyExists {
        List<MemberDTO> check =
                adminMemberMapper.select(MemberSearchParam.builder().matchLoginId(loginId).build());
        if (check != null && !check.isEmpty()) {
            throw new MemberLoginIdAlreadyExists(loginId);
        }
    }

    /**
     * 이메일 주소 중복 여부 확인
     *
     * @param email 확인할 이메일 주소
     * @throws MemberEmailAlreadyExists 이메일 주소가 중복됨
     */
    public void checkEmail(final String email) throws MemberEmailAlreadyExists {
        List<MemberDTO> check =
                adminMemberMapper.select(MemberSearchParam.builder().matchEmail(email).build());
        if (check != null && !check.isEmpty()) {
            throw new MemberEmailAlreadyExists(email);
        }
    }

}
