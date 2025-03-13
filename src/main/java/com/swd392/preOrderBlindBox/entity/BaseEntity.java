package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  @Setter
  private boolean isActive = true;

  @Column(name = "created_at", nullable = false)
  @Builder.Default
  private Long createdAt = Instant.now().toEpochMilli();

  @Column(name = "updated_at", nullable = false)
  @Builder.Default
  private Long updatedAt = Instant.now().toEpochMilli();

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = Instant.now().toEpochMilli();
  }
}
