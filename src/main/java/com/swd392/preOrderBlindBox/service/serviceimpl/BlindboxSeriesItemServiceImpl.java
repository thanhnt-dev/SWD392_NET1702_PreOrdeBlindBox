package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.entity.BlindboxSeriesItem;
import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.repository.repository.BlindboxSeriesItemRepository;
import com.swd392.preOrderBlindBox.service.service.BlindboxSeriesItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BlindboxSeriesItemServiceImpl implements BlindboxSeriesItemService {
    private final BlindboxSeriesItemRepository blindboxSeriesItemRepository;

    @Override
    public List<BlindboxSeriesItem> getItemsByBlindboxSeriesId(Long blindboxSeriesId) {
        return blindboxSeriesItemRepository.findByBlindboxSeriesId(blindboxSeriesId);
    }

    @Override
    public BlindboxSeriesItem getItemById(Long id) {
        return blindboxSeriesItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    }

    @Override
    public BlindboxSeriesItem createItem(BlindboxSeriesItem blindboxSeriesItem) {
        return blindboxSeriesItemRepository.save(blindboxSeriesItem);
    }

    @Override
    public BlindboxSeriesItem updateItem(BlindboxSeriesItem blindboxSeriesItem, Long id) {
        if (!Objects.equals(blindboxSeriesItem.getId(), id)) {
            throw new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND);
        }

        BlindboxSeriesItem existingBlindboxSeriesItem = blindboxSeriesItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
        existingBlindboxSeriesItem.setItemName(blindboxSeriesItem.getItemName());
        existingBlindboxSeriesItem.setItemChance(blindboxSeriesItem.getItemChance());


        return blindboxSeriesItemRepository.save(existingBlindboxSeriesItem);
    }

    @Override
    public void deactiveItem(Long id) {
        BlindboxSeriesItem item = getItemById(id);
        item.setActive(false);
        blindboxSeriesItemRepository.save(item);
    }
}