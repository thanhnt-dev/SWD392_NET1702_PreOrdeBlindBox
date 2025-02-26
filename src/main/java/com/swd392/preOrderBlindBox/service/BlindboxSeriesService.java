package com.swd392.preOrderBlindBox.service;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;

import java.util.List;

public interface BlindboxSeriesService {
    List<BlindboxSeries> getAllBlindboxSeries();

    BlindboxSeries getBlindboxSeriesById(Long id);

    BlindboxSeries createBlindboxSeries(BlindboxSeries blindboxSeries);

    BlindboxSeries updateBlindboxSeries(Long id, BlindboxSeries blindboxSeries);

    void deleteBlindboxSeries(Long id);
}
