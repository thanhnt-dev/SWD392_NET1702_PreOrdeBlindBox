package com.swd392.preOrderBlindBox.service;

import com.swd392.preOrderBlindBox.entity.BlindboxSeriesItem;

import java.util.List;

public interface BlindboxSeriesItemService {
    List<BlindboxSeriesItem> getBlindboxSeriesItemsByBlindboxSeriesId(Long blindboxSeriesId);

    BlindboxSeriesItem getBlindboxSeriesItemById(Long id);

    BlindboxSeriesItem createBlindboxSeriesItem(BlindboxSeriesItem blindboxSeriesItem);

    BlindboxSeriesItem updateBlindboxSeriesItem(BlindboxSeriesItem blindboxSeriesItem, Long id);

    void deleteBlindboxSeriesItem(Long id);
}
