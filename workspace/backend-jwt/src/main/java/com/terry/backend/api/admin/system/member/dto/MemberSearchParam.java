package com.terry.backend.api.admin.system.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terry.backend.core.dto.BaseSearchParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MemberSearchParam extends BaseSearchParam {

  private String id;
  private String loginId;
  private String name;
  private String email;
  private String sex;
  private String use;
  private String lock;
  @JsonIgnore
  private String matchEmail;
  @JsonIgnore
  private String matchLoginId;
  private String mobile;

}
