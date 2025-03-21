package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.entity.BlindboxSeriesItem;
import com.swd392.preOrderBlindBox.restcontroller.request.BlindboxSeriesItemsCreateRequest;

import java.util.List;

public interface BlindboxSeriesItemService {
  List<BlindboxSeriesItem> createItemsForSeries(List<BlindboxSeriesItemsCreateRequest> itemRequests, BlindboxSeries series);

  List<BlindboxSeriesItem> getItemsByBlindboxSeriesId(Long blindboxSeriesId);

  BlindboxSeriesItem getItemById(Long id);

  BlindboxSeriesItem createItem(BlindboxSeriesItem blindboxSeriesItem);

  BlindboxSeriesItem updateItem(BlindboxSeriesItem blindboxSeriesItem, Long id);

  void deactiveItem(Long id);

  List<BlindboxSeriesItem> saveAll(List<BlindboxSeriesItem> items);

  void save(BlindboxSeriesItem item);
}
