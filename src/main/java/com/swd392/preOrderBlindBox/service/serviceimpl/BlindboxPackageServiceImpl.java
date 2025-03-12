package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.PackageStatus;
import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.entity.Blindbox;
import com.swd392.preOrderBlindBox.entity.BlindboxPackage;
import com.swd392.preOrderBlindBox.repository.repository.BlindboxPackageRepository;
import com.swd392.preOrderBlindBox.repository.repository.BlindboxRepository;
import com.swd392.preOrderBlindBox.service.service.BlindboxPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlindboxPackageServiceImpl implements BlindboxPackageService {
    private final BlindboxPackageRepository blindboxPackageRepository;
    private final BlindboxRepository blindboxRepository;

    @Override
    public BlindboxPackage getBlindboxPackageById(Long id) {
        return blindboxPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    }

    @Override
    public List<BlindboxPackage> getBlindboxPackagesBySeriesId(Long seriesId) {
        return blindboxPackageRepository.findBySeriesId(seriesId);
    }

    @Override
    public BlindboxPackage createBlindboxPackage(BlindboxPackage blindboxPackage) {
        return blindboxPackageRepository.save(blindboxPackage);
    }

    @Override
    public BlindboxPackage updateBlindboxPackage(Long id, BlindboxPackage blindboxPackage) {
        BlindboxPackage existingBlindboxPackage = blindboxPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        existingBlindboxPackage.setTotalUnits(blindboxPackage.getTotalUnits());
        existingBlindboxPackage.setCurrentSoldUnits(blindboxPackage.getCurrentSoldUnits());
        existingBlindboxPackage.setStatus(blindboxPackage.getStatus());

        return blindboxPackageRepository.save(existingBlindboxPackage);
    }

    @Override
    public void deactivateBlindboxPackage(Long id) {
        BlindboxPackage blindboxPackage = blindboxPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        blindboxPackage.setActive(false);
        blindboxPackageRepository.save(blindboxPackage);
    }

    @Override
    public void updatePackageStatus(Long id, PackageStatus status) {
        BlindboxPackage blindboxPackage = blindboxPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        blindboxPackage.setStatus(status);
        blindboxPackageRepository.save(blindboxPackage);
    }

    @Override
    public List<Blindbox> generateBlindboxesForPackage(Long packageId) {
        // Implementation logic should be added here based on business requirements
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int getAvailableBlindboxQuantityOfPackageByPackageId(Long packageId) {
        return (int) blindboxRepository.findByBlindboxPackageId(packageId).stream()
                .filter(Blindbox::getIsSold).count();
    }
}
