package com.swd392.preOrderBlindBox.facade.facadeimpl;

import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.facade.facade.BlindboxFacade;
import com.swd392.preOrderBlindBox.restcontroller.response.*;
import com.swd392.preOrderBlindBox.service.service.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlindboxFacadeImpl implements BlindboxFacade {
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
        List<String> imageUrls = blindboxAssetService.getBlindboxAssetsByEntityId(blindboxSeries.getId()).stream()
                .map(BlindboxAsset::getMediaKey)
                .toList();
        response.setSeriesImageUrls(imageUrls);
        return BaseResponse.build(response, true);
    }

    @Override
    public Page<BlindboxSeriesResponse> getBlindboxSeries(Specification<BlindboxSeries> spec, Pageable pageable) {
        Page<BlindboxSeries> blindboxSeriesPage = blindboxSeriesService.getBlindboxSeries(spec, pageable);

        return blindboxSeriesPage.map(series -> {
            BlindboxSeriesResponse response = mapper.map(series, BlindboxSeriesResponse.class);

            List<String> imageUrls = blindboxAssetService.getBlindboxAssetsByEntityId(series.getId()).stream()
                    .map(BlindboxAsset::getMediaKey)
                    .toList();
            response.setSeriesImageUrls(imageUrls);

            return response;
        });
    }

    @Override
    public BaseResponse<BlindboxSeriesManagementDetailsResponse> getBlindboxSeriesForManagement(Long id) {
        BlindboxSeries blindboxSeries = blindboxSeriesService.getBlindboxSeriesById(id);
        BlindboxSeriesManagementDetailsResponse response = mapper.map(blindboxSeries, BlindboxSeriesManagementDetailsResponse.class);
        List<BlindboxAsset> assets = blindboxAssetService.getBlindboxAssetsByEntityId(blindboxSeries.getId());
        List<BlindboxAssetResponse> assetResponses = assets.stream()
                .map(asset -> {
                    BlindboxAssetResponse assetResponse = new BlindboxAssetResponse();
                    mapper.map(asset, assetResponse);
                    return assetResponse;
                })
                .toList();
        response.setAssets(assetResponses);
        return BaseResponse.build(response, true);
    }
}