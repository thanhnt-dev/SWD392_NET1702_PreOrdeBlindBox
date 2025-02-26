package com.swd392.preOrderBlindBox.entity;

import com.swd392.preOrderBlindBox.enums.Gender;
import com.swd392.preOrderBlindBox.enums.Role;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class User extends BaseEntity implements Serializable {

  @Column(nullable = false, unique = true, length = 50)
  private String email;

  @Column(nullable = false, length = 200)
  private String password;

  @Column(nullable = false, length = 40)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Gender gender;

  private String avatar;

  @Column(nullable = false, unique = true, length = 10)
  private String phone;

  @Column(name = "date_of_birth", nullable = false)
  private LocalDate dateOfBirth;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role roleName;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Orders> orders = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Transaction> transactions = new ArrayList<>();

  @OneToOne
  @JoinColumn(name = "cart_id")
  private Cart cart;
}
