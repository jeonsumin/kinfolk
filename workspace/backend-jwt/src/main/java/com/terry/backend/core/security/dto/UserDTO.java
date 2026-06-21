package com.terry.backend.core.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terry.backend.core.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDTO extends BaseDTO implements UserDetails, Serializable {

  private static final long serialVersionUID = -6125449464702039104L;

  private String id;

  private String loginId;

  @JsonIgnore
  private String password;

  private String name;

  private String profileImageUri;

  private String sex;

  private String email;

  private Boolean use;

  private Boolean lock;

  private List<AuthorityDTO> authorities;

  private String mobile; // 휴대전화번호

  @JsonIgnore
  @Override
  public String getUsername() {
    return id;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonLocked() {
    return !lock;
  }

  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isEnabled() {
    return use;
  }

  @Override
  public String toString() {
    return "User@" + "id=" + id + ", loginId=" + loginId + ", name=" + name + ", email=" + email
        + ", mobile=" + mobile + ", use=" + use ;
  }

}
