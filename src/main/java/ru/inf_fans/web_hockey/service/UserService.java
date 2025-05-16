package ru.inf_fans.web_hockey.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    boolean existsByEmail(String email);

    boolean existsByPhonenumber(String username);

}