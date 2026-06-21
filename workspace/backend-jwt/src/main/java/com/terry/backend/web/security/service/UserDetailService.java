package com.terry.backend.web.security.service;

import com.terry.backend.core.security.dto.UserDTO;
import com.terry.backend.web.security.mapper.SecurityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailService implements UserDetailsService {

    @Autowired
    private SecurityMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = mapper.selectByUsername(username);

        if (user != null) {
            user.setAuthorities(mapper.selectUserAuthorities(user.getUsername()));
            return user;
        }

        throw new UsernameNotFoundException(username);
    }
}
