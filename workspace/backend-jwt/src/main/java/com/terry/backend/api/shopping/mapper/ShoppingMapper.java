package com.terry.backend.api.shopping.mapper;

import com.terry.backend.api.shopping.dto.ShoppingCategoryDTO;
import com.terry.backend.api.shopping.dto.ShoppingItemDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShoppingMapper {

    /**
     * 1. 장보기 목록 조회 (카테고리 + 아이템)
     *
     * @param workspaceId 워크스페이스 ID
     */
    List<ShoppingCategoryDTO> selectShoppingList(@Param("workspaceId") String workspaceId);

    /**
     * 2. 카테고리 목록 조회
     */
    List<ShoppingCategoryDTO> selectCategories();

    /**
     * 3. 카테고리 수 조회 (lazy-seed 판별용)
     *
     * @param workspaceId 워크스페이스 ID
     */
    int countCategories(@Param("workspaceId") String workspaceId);

    /**
     * 4. 카테고리 단건 등록
     */
    void insertCategory(
            @Param("categoryId") String categoryId,
            @Param("wsId")       String wsId,
            @Param("categoryNm") String categoryNm,
            @Param("sortOrder")  int sortOrder,
            @Param("registId")   String registId
    );

    /**
     * 5. 아이템 단건 조회 (수정/삭제 권한 검증용)
     *
     * @param itemId 아이템 ID
     */
    ShoppingItemDTO selectItemById(@Param("itemId") String itemId);

    /**
     * 6. 아이템 등록
     */
    void insertItem(
            @Param("itemId")         String itemId,
            @Param("wsId")           String wsId,
            @Param("categoryId")     String categoryId,
            @Param("itemNm")         String itemNm,
            @Param("quantity")       Integer quantity,
            @Param("assignedUserId") String assignedUserId,
            @Param("registId")       String registId
    );

    /**
     * 7. 아이템 수정 (null 필드는 변경하지 않음)
     */
    void updateItem(
            @Param("itemId")         String itemId,
            @Param("itemNm")         String itemNm,
            @Param("quantity")       Integer quantity,
            @Param("isChecked")      Boolean isChecked,
            @Param("assignedUserId") String assignedUserId,
            @Param("categoryId")     String categoryId,
            @Param("updtId")         String updtId
    );

    /**
     * 8. 아이템 삭제
     *
     * @param itemId 아이템 ID
     */
    void deleteItem(@Param("itemId") String itemId);

    /**
     * 9. 워크스페이스 멤버십 확인
     *
     * @param workspaceId 워크스페이스 ID
     * @param userId      회원 ID
     */
    boolean existsWorkspaceMember(
            @Param("workspaceId") String workspaceId,
            @Param("userId")      String userId
    );
}
