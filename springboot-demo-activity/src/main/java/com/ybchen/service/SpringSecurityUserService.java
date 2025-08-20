package com.ybchen.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

/**
 * 模拟SpringSecurity登录查询用户
 * @description:
 * @author: Alex
 * @create: 2023-08-26 15:44
 */
@Service
public class SpringSecurityUserService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //基于内存
        InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
        UserDetails user = User
                .withUsername(username)
                .password(username)
                .authorities(username)
                .build();
        userDetailsService.createUser(user);
        return user;
    }
}