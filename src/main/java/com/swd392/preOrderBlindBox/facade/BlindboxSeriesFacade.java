package com.swd392.preOrderBlindBox.facade;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.BlindboxSeriesDetailsResponse;
import com.swd392.preOrderBlindBox.response.BlindboxSeriesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BlindboxSeriesFacade {

    BaseResponse<BlindboxSeriesDetailsResponse> getBlindboxSeriesWithDetailsById(Long id);

    Page<BlindboxSeriesResponse> searchBlindboxSeries(Specification<BlindboxSeries> spec, Pageable pageable);

}
