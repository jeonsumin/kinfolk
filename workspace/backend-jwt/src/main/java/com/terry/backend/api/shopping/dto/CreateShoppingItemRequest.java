package com.terry.backend.api.shopping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateShoppingItemRequest {

    private String categoryId;

    @NotBlank(message = "아이템 이름을 입력해주세요.")
    @Size(max = 255, message = "아이템 이름은 255자 이하로 입력해주세요.")
    private String itemNm;

    private Integer quantity;

    private String assignedUserId;
}
