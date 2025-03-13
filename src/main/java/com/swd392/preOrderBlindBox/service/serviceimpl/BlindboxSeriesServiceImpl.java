package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.PackageStatus;
import com.swd392.preOrderBlindBox.entity.Blindbox;
import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.repository.repository.BlindboxPackageRepository;
import com.swd392.preOrderBlindBox.repository.repository.BlindboxSeriesRepository;
import com.swd392.preOrderBlindBox.service.service.BlindboxPackageService;
import com.swd392.preOrderBlindBox.service.service.BlindboxSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlindboxSeriesServiceImpl implements BlindboxSeriesService {
    private final BlindboxSeriesRepository blindboxSeriesRepository;
    private final BlindboxPackageRepository blindboxPackageRepository;
    private final BlindboxPackageService blindboxPackageService;

    @Override
    public List<BlindboxSeries> getAllBlindboxSeries() {
        return blindboxSeriesRepository.findAll();
    }

    @Override
    public BlindboxSeries getBlindboxSeriesById(Long id) {
        return blindboxSeriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    }

    @Override
    public List<BlindboxSeries> getActiveBlindboxSeries() {
        return List.of();
    }

    @Override
    public BlindboxSeries createBlindboxSeries(BlindboxSeries blindboxSeries) {
        return blindboxSeriesRepository.save(blindboxSeries);
    }

   @Override
   public BlindboxSeries updateBlindboxSeries(Long id, BlindboxSeries blindboxSeries) {
       BlindboxSeries existingBlindboxSeries = blindboxSeriesRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

       // Update basic fields
       existingBlindboxSeries.setSeriesName(blindboxSeries.getSeriesName());
       existingBlindboxSeries.setDescription(blindboxSeries.getDescription());
       existingBlindboxSeries.setPackagePrice(blindboxSeries.getPackagePrice());
       existingBlindboxSeries.setBoxPrice(blindboxSeries.getBoxPrice());

       return blindboxSeriesRepository.save(existingBlindboxSeries);
   }

    @Override
    public void deactiveBlindboxSeries(Long id) {
        blindboxSeriesRepository.findById(id)
                .ifPresent(blindboxSeries -> {
                    blindboxSeries.setActive(false);
                    blindboxSeriesRepository.save(blindboxSeries);
                });
    }

    @Override
    public Page<BlindboxSeries> getBlindboxSeries(Specification<BlindboxSeries> spec, Pageable pageable) {
        return blindboxSeriesRepository.findAll(spec, pageable);
    }

    @Override
    public int getAvailablePackageQuantityOfSeries(Long seriesId) {
        return (int) blindboxPackageRepository.findBySeriesId(seriesId).stream()
                .filter(pkg -> pkg.getStatus() == PackageStatus.SEALED)
                .count();
    }

    @Override
    public int getAvailableBlindboxQuantityOfSeries(Long seriesId) {
        return blindboxPackageService.getBlindboxPackagesBySeriesId(seriesId).stream()
                .mapToInt(pkg -> blindboxPackageService.getAvailableBlindboxQuantityOfPackageByPackageId(pkg.getId()))
                .sum();
    }
}
