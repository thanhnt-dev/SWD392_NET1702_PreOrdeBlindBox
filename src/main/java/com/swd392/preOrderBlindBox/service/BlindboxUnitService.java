package com.swd392.preOrderBlindBox.service;

import com.swd392.preOrderBlindBox.entity.BlindboxUnit;

import java.util.List;

public interface BlindboxUnitService {
    List<BlindboxUnit> getAllBlindboxUnits();

    List<BlindboxUnit> getBlindboxUnitsByBlindboxSeriesId(Long blindboxSeriesItemId);

    BlindboxUnit getBlindboxUnitById(Long id);

    BlindboxUnit createBlindboxUnit(BlindboxUnit blindboxUnit);

    BlindboxUnit updateBlindboxUnit(BlindboxUnit blindboxUnit, Long id);

    void deleteBlindboxUnit(Long id);

}
