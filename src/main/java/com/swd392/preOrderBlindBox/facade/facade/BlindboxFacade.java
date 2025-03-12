package com.swd392.preOrderBlindBox.facade.facade;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.BlindboxSeriesDetailsResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.BlindboxSeriesManagementDetailsResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.BlindboxSeriesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BlindboxFacade {

    BaseResponse<BlindboxSeriesDetailsResponse> getBlindboxSeriesWithDetailsById(Long id);

    Page<BlindboxSeriesResponse> getBlindboxSeries(Specification<BlindboxSeries> spec, Pageable pageable);

    BaseResponse<BlindboxSeriesManagementDetailsResponse> getBlindboxSeriesForManagement(Long id);
}
