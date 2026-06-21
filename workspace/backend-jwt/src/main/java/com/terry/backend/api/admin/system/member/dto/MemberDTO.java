package com.terry.backend.api.admin.system.member.dto;

import com.terry.backend.core.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MemberDTO extends BaseDTO {

  private static final long serialVersionUID = 94562537211990651L;

  private String id;
  private String loginId;
  private String name;
  private String password;
  private String imageUrl;
  private String sex;
  private String email;
  private String remarks;
  private String use;
  private String lock;
  private Date lastLoginDate;

  private String mobile; // 휴대전화번호
  private List<MemberAuthorityDTO> authorities; // 권한목록

}
