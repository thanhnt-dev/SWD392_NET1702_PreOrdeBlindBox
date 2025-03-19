package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.entity.BlindboxSeriesItem;
import com.swd392.preOrderBlindBox.repository.repository.BlindboxSeriesItemRepository;
import com.swd392.preOrderBlindBox.restcontroller.request.BlindboxSeriesItemsCreateRequest;
import com.swd392.preOrderBlindBox.service.service.BlindboxSeriesItemService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlindboxSeriesItemServiceImpl implements BlindboxSeriesItemService {
  private final BlindboxSeriesItemRepository blindboxSeriesItemRepository;
  private final ModelMapper mapper;

  @Override
  public List<BlindboxSeriesItem> createItemsForSeries(List<BlindboxSeriesItemsCreateRequest> itemRequests, BlindboxSeries series) {
    return itemRequests.stream()
            .map(request -> {
              BlindboxSeriesItem item = mapper.map(request, BlindboxSeriesItem.class);
              item.setBlindboxSeries(series);
              return createItem(item);
            })
            .collect(Collectors.toList());
  }

  @Override
  public List<BlindboxSeriesItem> getItemsByBlindboxSeriesId(Long blindboxSeriesId) {
    return blindboxSeriesItemRepository.findByBlindboxSeriesId(blindboxSeriesId);
  }

  @Override
  public BlindboxSeriesItem getItemById(Long id) {
    return blindboxSeriesItemRepository
        .findById(id)
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

    BlindboxSeriesItem existingBlindboxSeriesItem =
        blindboxSeriesItemRepository
            .findById(id)
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

  @Override
  @Transactional
  public List<BlindboxSeriesItem> saveAll(List<BlindboxSeriesItem> items) {
    return blindboxSeriesItemRepository.saveAll(items);
  }

  @Override
  @Transactional
  public void save(BlindboxSeriesItem item) {
    blindboxSeriesItemRepository.save(item);
  }
}
