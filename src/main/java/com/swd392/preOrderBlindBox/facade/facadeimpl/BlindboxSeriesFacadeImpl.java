package com.swd392.preOrderBlindBox.facade.facadeimpl;

import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.facade.facade.BlindboxSeriesFacade;
import com.swd392.preOrderBlindBox.restcontroller.response.*;
import com.swd392.preOrderBlindBox.service.service.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlindboxSeriesFacadeImpl implements BlindboxSeriesFacade {
    private final BlindboxSeriesService blindboxSeriesService;
    private final BlindboxPackageService blindboxPackageService;
    private final BlindboxSeriesItemService blindboxSeriesItemService;
    private final BlindboxAssetService blindboxAssetService;
    private final BlindboxService blindboxService;
    private final ModelMapper mapper;

    @Override
    public BaseResponse<BlindboxSeriesDetailsResponse> getBlindboxSeriesWithDetailsById(Long id) {
        BlindboxSeries blindboxSeries = blindboxSeriesService.getBlindboxSeriesById(id);
        BlindboxSeriesDetailsResponse response = mapper.map(blindboxSeries, BlindboxSeriesDetailsResponse.class);
        return BaseResponse.build(response, true);
    }

    @Override
    public Page<BlindboxSeriesResponse> getBlindboxSeries(Specification<BlindboxSeries> spec, Pageable pageable) {
        Page<BlindboxSeries> blindboxSeriesPage = blindboxSeriesService.getBlindboxSeries(spec, pageable);
        return blindboxSeriesPage.map(series -> mapper.map(series, BlindboxSeriesResponse.class));
    }

    @Override
    public BaseResponse<BlindboxSeriesManagementDetailsResponse> getBlindboxSeriesForManagement(Long id) {
        BlindboxSeries blindboxSeries = blindboxSeriesService.getBlindboxSeriesById(id);
        BlindboxSeriesManagementDetailsResponse response = mapper.map(blindboxSeries, BlindboxSeriesManagementDetailsResponse.class);
        return BaseResponse.build(response, true);
    }
}