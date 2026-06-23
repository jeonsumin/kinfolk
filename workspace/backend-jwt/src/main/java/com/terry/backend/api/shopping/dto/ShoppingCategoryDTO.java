package com.terry.backend.api.shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ShoppingCategoryDTO {
    private static final long serialVersionUID = 1L;

    private String categoryId;
    private String categoryNm;
    private Integer sortOrder;
    private List<ShoppingItemDTO> items;
}
