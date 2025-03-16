package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.entity.PreorderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreorderItemRepository extends JpaRepository<PreorderItem, Long> {
    List<PreorderItem> findByPreorderId(Long preorderId);
}
