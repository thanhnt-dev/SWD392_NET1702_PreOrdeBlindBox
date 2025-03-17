package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.entity.Preorder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreorderRepository extends JpaRepository<Preorder, Long> {
    Optional<Preorder> findByOrderCode(String orderCode);
}
