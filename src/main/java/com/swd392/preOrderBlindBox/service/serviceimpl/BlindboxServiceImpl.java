package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.entity.Blindbox;
import com.swd392.preOrderBlindBox.repository.repository.BlindboxRepository;
import com.swd392.preOrderBlindBox.service.service.BlindboxService;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlindboxServiceImpl implements BlindboxService {
  private final BlindboxRepository blindboxRepository;

  @Override
  public Blindbox getBlindboxById(Long id) {
    return blindboxRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
  }

  @Override
  public List<Blindbox> getBlindboxesByPackageId(Long packageId) {
    return blindboxRepository.findByBlindboxPackageId(packageId);
  }

  @Override
  public void updateBlindboxStatus(Long id, Boolean isSold) {
    Blindbox blindbox =
        blindboxRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    blindbox.setIsSold(isSold);
    blindboxRepository.save(blindbox);
  }

  @Override
  public void updateRevealedItem(Long blindboxId, Long itemId) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public List<Blindbox> getUnsoldBlindboxesOfPackage(Long packageId) {
    return blindboxRepository.findByBlindboxPackageId(packageId).stream().filter(blindbox -> !blindbox.getIsSold()).collect(Collectors.toList());
  }
}
