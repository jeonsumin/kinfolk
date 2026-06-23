package com.terry.backend.api.shopping.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShoppingItemRequest {

    @Size(max = 255, message = "아이템 이름은 255자 이하로 입력해주세요.")
    private String itemNm;

    private Integer quantity;

    private Boolean isChecked;

    private String assignedUserId;

    private String categoryId;
}
