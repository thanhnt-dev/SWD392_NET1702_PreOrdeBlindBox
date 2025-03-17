package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.common.enums.ProductType;
import com.swd392.preOrderBlindBox.entity.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findByCartId(Long cartId);

  Optional<CartItem> findByCartIdAndSeriesId(Long cartId, Long seriesId);

  void deleteByCartId(Long cartId);

  Optional<CartItem> findByCartIdAndSeriesIdAndProductType(
      Long cartId, Long seriesId, ProductType productType);
}
