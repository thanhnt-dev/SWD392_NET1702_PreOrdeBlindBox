package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.entity.User;
import com.swd392.preOrderBlindBox.restcontroller.request.RegisterRequest;
import com.swd392.preOrderBlindBox.restcontroller.request.UserCriteria;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
  User findByEmail(String mail);

  void validateSignUp(RegisterRequest request);

  Optional<User> getCurrentUser();

  User createUser(User user);

  User findById(Long id);

  void updateUser(User user);

  Page<User> findByFilter(UserCriteria criteria, boolean admin);
}
