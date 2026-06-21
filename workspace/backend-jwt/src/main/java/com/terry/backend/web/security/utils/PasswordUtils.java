package com.terry.backend.web.security.utils;

import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {

  @Setter
  private static PasswordEncoder passwordEncoder;

  public static String encode(CharSequence rawPassword) {
    return passwordEncoder.encode(rawPassword);
  };

  public static boolean matches(CharSequence rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  };

}
