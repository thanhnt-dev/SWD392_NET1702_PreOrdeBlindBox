package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.entity.PreorderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreorderItemRepository extends JpaRepository<PreorderItem, Long> {
  List<PreorderItem> findByPreorderId(Long preorderId);
}
