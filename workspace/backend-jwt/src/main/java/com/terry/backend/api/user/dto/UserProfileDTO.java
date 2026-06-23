package com.terry.backend.api.user.dto;

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
public class UserProfileDTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private String loginId;
    private String name;
    private String email;
    private String profileImageUri;
    private String mobile;
}
