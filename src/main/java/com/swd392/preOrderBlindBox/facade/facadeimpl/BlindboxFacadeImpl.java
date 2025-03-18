package com.swd392.preOrderBlindBox.facade.facadeimpl;

import com.swd392.preOrderBlindBox.common.enums.PackageStatus;
import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.facade.facade.BlindboxFacade;
import com.swd392.preOrderBlindBox.restcontroller.request.BlindboxSeriesCreateRequest;
import com.swd392.preOrderBlindBox.restcontroller.request.BlindboxSeriesItemsCreateRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.*;
import com.swd392.preOrderBlindBox.service.service.*;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BlindboxFacadeImpl implements BlindboxFacade {
  private final BlindboxSeriesService blindboxSeriesService;
  private final BlindboxPackageService blindboxPackageService;
  private final BlindboxSeriesItemService blindboxSeriesItemService;
  private final BlindboxAssetService blindboxAssetService;
  private final PreorderCampaignService preorderCampaignService;
  private final ModelMapper mapper;
  private final CloudinaryService cloudinaryService;
  private final BlindboxService blindboxService;

  @Override
  public BaseResponse<BlindboxSeriesDetailsResponse> getBlindboxSeriesWithDetailsById(Long id) {
    BlindboxSeries blindboxSeries =
        blindboxSeriesService
            .getBlindboxSeriesById(id)
            .orElseThrow(() -> new IllegalArgumentException("Blindbox series not found"));
    BlindboxSeriesDetailsResponse response =
        mapper.map(blindboxSeries, BlindboxSeriesDetailsResponse.class);

    response.setSeriesImageUrls(getImageUrls(blindboxSeries.getId()));
    response.setItems(getBlindboxItems(blindboxSeries));
    response.setAvailablePackageUnits(
        blindboxSeriesService.getAvailablePackageQuantityOfSeries(blindboxSeries.getId()));
    response.setAvailableBoxUnits(
        blindboxSeriesService.getAvailableBlindboxQuantityOfSeries(blindboxSeries.getId()));
    response.setActiveCampaign(getCampaignDetails(blindboxSeries));

    return BaseResponse.build(response, true);
  }

  private List<String> getImageUrls(Long entityId) {
    return blindboxAssetService.getBlindboxAssetsByEntityId(entityId).stream()
        .map(BlindboxAsset::getMediaKey)
        .toList();
  }

  private List<BlindboxSeriesItemResponse> getBlindboxItems(BlindboxSeries blindboxSeries) {
    List<BlindboxSeriesItemResponse> items =
        blindboxSeriesItemService.getItemsByBlindboxSeriesId(blindboxSeries.getId()).stream()
            .map(item -> mapper.map(item, BlindboxSeriesItemResponse.class))
            .toList();

    items.forEach(
        item -> {
          item.setImageUrls(getImageUrls(item.getId()));
          item.setSeriesId(blindboxSeries.getId());
        });
    return items;
  }

  private PreorderCampaignDetailsResponse getCampaignDetails(BlindboxSeries blindboxSeries) {
    Optional<PreorderCampaign> campaign =
        preorderCampaignService.getOngoingCampaignOfBlindboxSeries(blindboxSeries.getId());

    if (campaign.isEmpty()) {
      return null;
    }

    PreorderCampaignDetailsResponse campaignResponse =
        mapper.map(campaign.get(), PreorderCampaignDetailsResponse.class);

    List<CampaignTierResponse> campaignTierResponses =
        campaign.get().getCampaignTiers().stream()
            .map(tier -> mapper.map(tier, CampaignTierResponse.class))
            .toList();

    campaignResponse.setCampaignTiers(campaignTierResponses);
    return campaignResponse;
  }

  @Override
  public Page<BlindboxSeriesResponse> getBlindboxSeries(
      Specification<BlindboxSeries> spec, Pageable pageable) {
    Page<BlindboxSeries> blindboxSeriesPage =
        blindboxSeriesService.getBlindboxSeries(spec, pageable);

    return blindboxSeriesPage.map(
        series -> {
          BlindboxSeriesResponse response = mapper.map(series, BlindboxSeriesResponse.class);

          List<String> imageUrls =
              blindboxAssetService.getBlindboxAssetsByEntityId(series.getId()).stream()
                  .map(BlindboxAsset::getMediaKey)
                  .toList();
          response.setSeriesImageUrls(imageUrls);

          return response;
        });
  }

  @Override
  public BaseResponse<BlindboxSeriesManagementDetailsResponse> getBlindboxSeriesForManagement(Long id) {
    BlindboxSeries blindboxSeries =
        blindboxSeriesService
            .getBlindboxSeriesById(id)
            .orElseThrow(() -> new IllegalArgumentException("Blindbox series not found"));
    BlindboxSeriesManagementDetailsResponse response =
        mapper.map(blindboxSeries, BlindboxSeriesManagementDetailsResponse.class);
    List<BlindboxAsset> assets =
        blindboxAssetService.getBlindboxAssetsByEntityId(blindboxSeries.getId());
    List<BlindboxAssetResponse> assetResponses =
        assets.stream()
            .map(
                asset -> {
                  BlindboxAssetResponse assetResponse = new BlindboxAssetResponse();
                  mapper.map(asset, assetResponse);
                  return assetResponse;
                })
            .toList();
    response.setAssets(assetResponses);
    return BaseResponse.build(response, true);
  }

  @Override
  @Transactional
  public BaseResponse<BlindboxSeriesResponse> createBlindboxSeries(BlindboxSeriesCreateRequest request) {
    BlindboxSeries blindboxSeries = mapper.map(request, BlindboxSeries.class);
    BlindboxSeries savedBlindboxSeries = blindboxSeriesService.createBlindboxSeries(blindboxSeries);

    List<BlindboxPackage> packages = blindboxPackageService.createPackagesForSeries(request, savedBlindboxSeries);
    List<BlindboxSeriesItem> items = blindboxSeriesItemService.createItemsForSeries(request.getItems(), savedBlindboxSeries);

    savedBlindboxSeries.setPackages(packages);
    savedBlindboxSeries.setItems(items);
    blindboxSeriesService.updateBlindboxSeries(savedBlindboxSeries.getId(), savedBlindboxSeries);

    BlindboxSeriesResponse response = mapper.map(savedBlindboxSeries, BlindboxSeriesResponse.class);
    return BaseResponse.build(response, true);
  }

  @Override
  @SneakyThrows
  @Transactional
  public BaseResponse<Void> uploadImageForBlindboxItem(Long id, List<MultipartFile> files) {
    BlindboxSeriesItem item = blindboxSeriesItemService.getItemById(id);
    for (MultipartFile file : files) {
      String mediaKey = cloudinaryService.uploadImage(file.getBytes());
      BlindboxAsset asset =
          BlindboxAsset.builder().mediaKey(mediaKey).entityId(item.getId()).build();
      blindboxAssetService.saveBlindboxAsset(asset);
    }
    return BaseResponse.ok();
  }

  @Override
  public BaseResponse<BlindboxSeriesManagementDetailsResponse> addBlindboxPackagesToSeries(Long seriesId, int count) {
    return null;
  }

  @Override
  public BaseResponse<BlindboxSeriesManagementDetailsResponse> addBlindboxesToSeries(Long seriesId, int count) {
    return null;
  }
}
