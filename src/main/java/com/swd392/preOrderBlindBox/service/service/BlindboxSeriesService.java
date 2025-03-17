package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BlindboxSeriesService {
  List<BlindboxSeries> getAllBlindboxSeries();

  Optional<BlindboxSeries> getBlindboxSeriesById(Long id);

  List<BlindboxSeries> getActiveBlindboxSeries();

  BlindboxSeries createBlindboxSeries(BlindboxSeries blindboxSeries);

  BlindboxSeries updateBlindboxSeries(Long id, BlindboxSeries blindboxSeries);

  void deactiveBlindboxSeries(Long id);

  Page<BlindboxSeries> getBlindboxSeries(Specification<BlindboxSeries> spec, Pageable pageable);

  int getAvailablePackageQuantityOfSeries(Long packageId);

  int getAvailableBlindboxQuantityOfSeries(Long packageId);
}
