package com.swd392.preOrderBlindBox.service.impl;

import com.swd392.preOrderBlindBox.entity.BlindboxUnit;
import com.swd392.preOrderBlindBox.enums.ErrorCode;
import com.swd392.preOrderBlindBox.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.repository.BlindboxUnitRepository;
import com.swd392.preOrderBlindBox.service.BlindboxUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlindboxUnitServiceImpl implements BlindboxUnitService {
    private final BlindboxUnitRepository blindboxUnitRepository;

    @Override
    public List<BlindboxUnit> getAllBlindboxUnits() {
        return blindboxUnitRepository.findAll();
    }

    @Override
    public List<BlindboxUnit> getBlindboxUnitsByBlindboxSeriesId(Long blindboxSeriesId) {
        return blindboxUnitRepository.findByBlindboxSeriesId(blindboxSeriesId);
    }

    @Override
    public BlindboxUnit getBlindboxUnitById(Long id) {
        return blindboxUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    }

    @Override
    public BlindboxUnit createBlindboxUnit(BlindboxUnit blindboxUnit) {
        return blindboxUnitRepository.save(blindboxUnit);
    }

    @Override
    public BlindboxUnit updateBlindboxUnit(BlindboxUnit blindboxUnit, Long id) {
        if (!blindboxUnit.getId().equals(id)) {
            throw new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND);
        }

        BlindboxUnit existingBlindboxUnit = blindboxUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
        existingBlindboxUnit.setTitle(blindboxUnit.getTitle());
        existingBlindboxUnit.setPrice(blindboxUnit.getPrice());
        existingBlindboxUnit.setDiscountPercent(blindboxUnit.getDiscountPercent());
        existingBlindboxUnit.setStockQuantity(blindboxUnit.getStockQuantity());
        existingBlindboxUnit.setQuantityPerPackage(blindboxUnit.getQuantityPerPackage());
        return blindboxUnitRepository.save(existingBlindboxUnit);
    }

    @Override
    public void deleteBlindboxUnit(Long id) {
        blindboxUnitRepository.deleteById(id);
    }
}
