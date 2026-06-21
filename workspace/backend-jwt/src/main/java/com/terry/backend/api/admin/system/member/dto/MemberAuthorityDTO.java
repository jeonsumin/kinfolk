package com.terry.backend.api.admin.system.member.dto;

import com.terry.backend.core.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = false, of = {"userId", "authorityId"})
public class MemberAuthorityDTO extends BaseDTO {

  private static final long serialVersionUID = -3851545976620853946L;

  private String userId;
  private String authorityId;
  private String authorityCode;
  private String authorityName;
  private boolean map;

  public MemberAuthorityDTO(String userId) {
    super();
    this.userId = userId;
  }

}
