package com.swd392.preOrderBlindBox.service.impl;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.enums.ErrorCode;
import com.swd392.preOrderBlindBox.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.repository.BlindboxSeriesRepository;
import com.swd392.preOrderBlindBox.service.BlindboxSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlindboxSeriesServiceImpl implements BlindboxSeriesService {
    private final BlindboxSeriesRepository blindboxSeriesRepository;

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
       existingBlindboxSeries.setOpenedAt(blindboxSeries.getOpenedAt());
       existingBlindboxSeries.setCategory(blindboxSeries.getCategory());
       existingBlindboxSeries.setCampaigns(blindboxSeries.getCampaigns());

       // Clear and update collections
       existingBlindboxSeries.getBlindboxUnits().clear();
       existingBlindboxSeries.getBlindboxUnits().addAll(blindboxSeries.getBlindboxUnits());

       existingBlindboxSeries.getBlindboxAssets().clear();
       existingBlindboxSeries.getBlindboxAssets().addAll(blindboxSeries.getBlindboxAssets());

       return blindboxSeriesRepository.save(existingBlindboxSeries);
   }

    @Override
    public void deleteBlindboxSeries(Long id) {
        blindboxSeriesRepository.deleteById(id);
    }
}
