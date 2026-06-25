package com.terry.backend.api.shopping.service;

import com.terry.backend.api.shopping.dto.CreateShoppingItemRequest;
import com.terry.backend.api.shopping.dto.ShoppingCategoryDTO;
import com.terry.backend.api.shopping.dto.ShoppingItemDTO;
import com.terry.backend.api.shopping.dto.UpdateShoppingItemRequest;
import com.terry.backend.api.shopping.mapper.ShoppingMapper;
import com.terry.backend.api.shopping.strategy.ShoppingCategoryStrategy;
import com.terry.backend.api.shopping.strategy.ShoppingItemStrategy;
import com.terry.backend.core.excption.SystemException;
import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingService {

    private static final SerialConfiguration<String> CATEGORY_STRATEGY = new ShoppingCategoryStrategy();
    private static final SerialConfiguration<String> ITEM_STRATEGY      = new ShoppingItemStrategy();

    private static final String[] DEFAULT_CATEGORIES = {
            "채소/과일", "고기/해산물", "유제품/계란", "가공식품", "음료/주류", "생활용품", "기타"
    };

    private final ShoppingMapper mapper;

    /**
     * 멤버십 검증: 비멤버면 403 SystemException 발생
     */
    private void checkMembership(String workspaceId, String userId) throws SystemException {
        if (!mapper.existsWorkspaceMember(workspaceId, userId)) {
            throw new SystemException(HttpStatus.FORBIDDEN, "접근권한이 없습니다.");
        }
    }

    /**
     * 카테고리별 아이템 그룹 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ShoppingCategoryDTO> getShoppingList(String workspaceId) throws SystemException {
        String userId = SessionUtils.getUserId();
        checkMembership(workspaceId, userId);
        return mapper.selectShoppingList(workspaceId);
    }

    /**
     * 카테고리 목록 조회 (lazy-seed: 카테고리가 없으면 기본값 자동 생성)
     */
    @Transactional
    public List<ShoppingCategoryDTO> getCategories(String workspaceId) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(workspaceId, userId);
        return mapper.selectCategories();
    }

    /**
     * 아이템 추가
     */
    @Transactional
    public void addItem(String workspaceId, CreateShoppingItemRequest request) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(workspaceId, userId);
        String itemId = SerialUtil.get(ShoppingItemStrategy.ID, ITEM_STRATEGY);
        mapper.insertItem(
                itemId,
                workspaceId,
                request.getCategoryId(),
                request.getItemNm(),
                request.getQuantity() != null ? request.getQuantity() : 1,
                request.getAssignedUserId(),
                userId
        );
    }

    /**
     * 아이템 수정 (null 필드는 변경하지 않음)
     */
    @Transactional
    public void updateItem(String itemId, UpdateShoppingItemRequest request) throws SystemException {
        String userId = SessionUtils.getUserId();
        ShoppingItemDTO item = mapper.selectItemById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("존재하지 않는 아이템입니다.");
        }
        checkMembership(item.getWsId(), userId);
        mapper.updateItem(
                itemId,
                request.getItemNm(),
                request.getQuantity(),
                request.getIsChecked(),
                request.getAssignedUserId(),
                request.getCategoryId(),
                userId
        );
    }

    /**
     * 아이템 삭제
     */
    @Transactional
    public void deleteItem(String itemId) throws SystemException {
        String userId = SessionUtils.getUserId();
        ShoppingItemDTO item = mapper.selectItemById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("존재하지 않는 아이템입니다.");
        }
        checkMembership(item.getWsId(), userId);
        mapper.deleteItem(itemId);
    }
}
