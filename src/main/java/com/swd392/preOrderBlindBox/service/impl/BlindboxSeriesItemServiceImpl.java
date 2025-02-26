package com.swd392.preOrderBlindBox.service.impl;

import com.swd392.preOrderBlindBox.entity.BlindboxSeriesItem;
import com.swd392.preOrderBlindBox.enums.ErrorCode;
import com.swd392.preOrderBlindBox.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.repository.BlindboxSeriesItemRepository;
import com.swd392.preOrderBlindBox.service.BlindboxSeriesItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BlindboxSeriesItemServiceImpl implements BlindboxSeriesItemService {
    private final BlindboxSeriesItemRepository blindboxSeriesItemRepository;

    @Override
    public List<BlindboxSeriesItem> getAllBlindboxSeriesItemsByBlindboxSeriesId(Long blindboxSeriesId) {
        return blindboxSeriesItemRepository.findByBlindboxSeriesId(blindboxSeriesId);
    }

    @Override
    public BlindboxSeriesItem getBlindboxSeriesItemById(Long id) {
        return blindboxSeriesItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    }

    @Override
    public BlindboxSeriesItem createBlindboxSeriesItem(BlindboxSeriesItem blindboxSeriesItem) {
        return blindboxSeriesItemRepository.save(blindboxSeriesItem);
    }

    @Override
    public BlindboxSeriesItem updateBlindboxSeriesItem(BlindboxSeriesItem blindboxSeriesItem, Long id) {
        if (!Objects.equals(blindboxSeriesItem.getId(), id)) {
            throw new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND);
        }

        BlindboxSeriesItem existingBlindboxSeriesItem = blindboxSeriesItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
        existingBlindboxSeriesItem.setItemName(blindboxSeriesItem.getItemName());
        existingBlindboxSeriesItem.setDescription(blindboxSeriesItem.getDescription());
        existingBlindboxSeriesItem.setImageUrl(blindboxSeriesItem.getImageUrl());
        existingBlindboxSeriesItem.setRarityPercentage(blindboxSeriesItem.getRarityPercentage());

        return blindboxSeriesItemRepository.save(existingBlindboxSeriesItem);

    }

    @Override
    public void deleteBlindboxSeriesItem(Long id) {
        blindboxSeriesItemRepository.deleteById(id);
    }
}
