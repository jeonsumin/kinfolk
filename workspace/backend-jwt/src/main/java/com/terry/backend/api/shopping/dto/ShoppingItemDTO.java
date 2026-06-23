package com.terry.backend.api.shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ShoppingItemDTO {
    private static final long serialVersionUID = 1L;

    private String itemId;
    private String wsId;
    private String categoryId;
    private String itemNm;
    private Integer quantity;
    private Boolean isChecked;
    private String assignedUserId;
    private String assignedUserName;
}
