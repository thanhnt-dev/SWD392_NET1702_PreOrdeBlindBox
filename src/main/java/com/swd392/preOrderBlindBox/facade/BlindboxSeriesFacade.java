package com.swd392.preOrderBlindBox.facade;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.BlindboxSeriesResponse;

import java.util.List;

public interface BlindboxSeriesFacade {
    BaseResponse<List<BlindboxSeriesResponse>> getAllBlindboxSeries();
}
