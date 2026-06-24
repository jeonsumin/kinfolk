package com.terry.backend.api.shopping.controller;

import com.terry.backend.api.shopping.dto.CreateShoppingItemRequest;
import com.terry.backend.api.shopping.dto.ShoppingCategoryDTO;
import com.terry.backend.api.shopping.dto.UpdateShoppingItemRequest;
import com.terry.backend.api.shopping.service.ShoppingService;
import com.terry.backend.web.controller.ApiRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Shopping", description = "장보기 API")
public class ShoppingController extends ApiRestController {

    private final ShoppingService service;

    @GetMapping("/shopping/{workspaceId}")
    @Operation(summary = "장보기 목록 조회", description = "카테고리별 아이템 그룹 목록을 반환한다.")
    public List<ShoppingCategoryDTO> getShoppingList(@PathVariable String workspaceId) throws Exception {
        return service.getShoppingList(workspaceId);
    }

    @GetMapping("/shopping/{workspaceId}/categories")
    @Operation(summary = "카테고리 목록 조회", description = "카테고리 목록을 반환한다. 첫 조회 시 기본 카테고리를 자동 생성한다.")
    public List<ShoppingCategoryDTO> getCategories(@PathVariable String workspaceId) throws Exception {
        return service.getCategories(workspaceId);
    }

    @PostMapping("/shopping/{workspaceId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "아이템 추가", description = "장보기 목록에 아이템을 추가한다.")
    public void addItem(
            @PathVariable String workspaceId,
            @Valid @RequestBody CreateShoppingItemRequest request) throws Exception {
        service.addItem(workspaceId, request);
    }

    @PostMapping("/shopping/items/{itemId}")
    @Operation(summary = "아이템 수정", description = "아이템 정보를 수정한다. null 필드는 변경하지 않는다.")
    public void updateItem(
            @PathVariable String itemId,
            @Valid @RequestBody UpdateShoppingItemRequest request) throws Exception {
        service.updateItem(itemId, request);
    }

    @DeleteMapping("/shopping/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "아이템 삭제", description = "아이템을 삭제한다.")
    public void deleteItem(@PathVariable String itemId) throws Exception {
        service.deleteItem(itemId);
    }
}
