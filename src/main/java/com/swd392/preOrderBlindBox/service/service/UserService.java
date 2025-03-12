package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
  User findByEmail(String mail);
  Optional<User> getCurrentUser();
}
