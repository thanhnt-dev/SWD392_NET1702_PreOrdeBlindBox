package com.swd392.preOrderBlindBox.specification;

import com.swd392.preOrderBlindBox.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class AccountSpecification {

  public static Specification<User> baseSpecification(boolean isAdmin) {
    return (root, query, criteriaBuilder) -> {
      if (isAdmin) {
        query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("isActive"), true);
    };
  }

  public static Specification<User> filterByPhone(String phone) {
    return (root, query, criteriaBuilder) -> {
      if (phone == null || phone.isBlank()) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("phone"), phone);
    };
  }

  public static Specification<User> filterByEmail(String email) {
    return (root, query, criteriaBuilder) -> {
      if (email == null || email.isBlank()) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    };
  }

  public static Specification<User> filterByName(String name) {
    return (root, query, criteriaBuilder) -> {
      if (name == null || name.isBlank()) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    };
  }
}
