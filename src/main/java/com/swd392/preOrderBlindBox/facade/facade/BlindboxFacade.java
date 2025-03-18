package com.swd392.preOrderBlindBox.facade.facade;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.restcontroller.request.BlindboxSeriesCreateRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.BlindboxSeriesDetailsResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.BlindboxSeriesManagementDetailsResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.BlindboxSeriesResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

public interface BlindboxFacade {

  BaseResponse<BlindboxSeriesDetailsResponse> getBlindboxSeriesWithDetailsById(Long id);

  Page<BlindboxSeriesResponse> getBlindboxSeries(Specification<BlindboxSeries> spec, Pageable pageable);

  BaseResponse<BlindboxSeriesManagementDetailsResponse> getBlindboxSeriesForManagement(Long id);

  BaseResponse<BlindboxSeriesResponse> createBlindboxSeries(BlindboxSeriesCreateRequest request);

  BaseResponse<Void> uploadImageForBlindboxItem(Long id, List<MultipartFile> file);

  BaseResponse<BlindboxSeriesManagementDetailsResponse> addBlindboxPackagesToSeries(Long seriesId, int count);

  BaseResponse<BlindboxSeriesManagementDetailsResponse> addBlindboxesToSeries(Long seriesId, int count);
}
