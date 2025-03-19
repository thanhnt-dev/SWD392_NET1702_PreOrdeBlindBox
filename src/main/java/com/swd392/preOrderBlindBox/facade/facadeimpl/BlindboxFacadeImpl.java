package com.swd392.preOrderBlindBox.facade.facadeimpl;

import com.swd392.preOrderBlindBox.common.enums.AssetEntityType;
import com.swd392.preOrderBlindBox.common.enums.PackageStatus;
import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.facade.facade.BlindboxFacade;
import com.swd392.preOrderBlindBox.restcontroller.request.BlindboxSeriesCreateRequest;
import com.swd392.preOrderBlindBox.restcontroller.request.BlindboxSeriesItemsCreateRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.*;
import com.swd392.preOrderBlindBox.service.service.*;
import jakarta.transaction.Transactional;

import java.io.IOException;
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

  @Override
  public BaseResponse<BlindboxSeriesDetailsResponse> getBlindboxSeriesWithDetailsById(Long id) {
    BlindboxSeries blindboxSeries =
        blindboxSeriesService
            .getBlindboxSeriesById(id)
            .orElseThrow(() -> new IllegalArgumentException("Blindbox series not found"));
    BlindboxSeriesDetailsResponse response =
        mapper.map(blindboxSeries, BlindboxSeriesDetailsResponse.class);

    response.setSeriesImageUrls(getImageUrls(blindboxSeries.getId(), AssetEntityType.BLINDBOX_SERIES));
    response.setItems(getBlindboxItems(blindboxSeries));
    response.setAvailablePackageUnits(blindboxSeriesService.getAvailablePackageQuantityOfSeries(blindboxSeries.getId()));
    response.setAvailableBoxUnits(blindboxSeriesService.getAvailableBlindboxQuantityOfSeries(blindboxSeries.getId()));
    response.setActiveCampaign(getCampaignDetails(blindboxSeries));

    return BaseResponse.build(response, true);
  }


  private List<BlindboxSeriesItemResponse> getBlindboxItems(BlindboxSeries blindboxSeries) {
    List<BlindboxSeriesItemResponse> items =
        blindboxSeriesItemService.getItemsByBlindboxSeriesId(blindboxSeries.getId()).stream()
            .map(item -> mapper.map(item, BlindboxSeriesItemResponse.class))
            .toList();

    items.forEach(
        item -> {
          item.setImageUrl(getThumbnailImageUrl(item.getId(), AssetEntityType.BLINDBOX_SERIES_ITEM));
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
          response.setSeriesImageUrl(getThumbnailImageUrl(series.getId(), AssetEntityType.BLINDBOX_SERIES));
          return response;
        });
  }

  @Override
  public BaseResponse<BlindboxSeriesManagementDetailsResponse> getBlindboxSeriesForManagement(Long id) {
    BlindboxSeries blindboxSeries =
        blindboxSeriesService
            .getBlindboxSeriesById(id)
            .orElseThrow(() -> new IllegalArgumentException("Blindbox series not found"));
    BlindboxSeriesManagementDetailsResponse response = mapper.map(blindboxSeries, BlindboxSeriesManagementDetailsResponse.class);
    List<BlindboxAsset> assets = blindboxAssetService.getBlindboxAssetsByEntityIdAndType(blindboxSeries.getId(), AssetEntityType.BLINDBOX_SERIES);
    List<BlindboxAssetResponse> assetResponses = new ArrayList<>();

      for (BlindboxAsset asset : assets) {
          BlindboxAssetResponse assetResponse = mapper.map(asset, BlindboxAssetResponse.class);
          assetResponse.setMediaUrl(asset.getMediaKey());
          assetResponses.add(assetResponse);
      }

    response.setAssets(assetResponses);
    return BaseResponse.build(response, true);
  }


  @Override
  @Transactional
  public BaseResponse<BlindboxSeriesResponse> createBlindboxSeries(BlindboxSeriesCreateRequest request, List<MultipartFile> seriesImages) throws IOException {
    BlindboxSeries blindboxSeries = mapper.map(request, BlindboxSeries.class);
    BlindboxSeries savedBlindboxSeries = blindboxSeriesService.createBlindboxSeries(blindboxSeries);

    List<BlindboxPackage> packages = blindboxPackageService.createPackagesForSeries(request, savedBlindboxSeries);
    List<BlindboxSeriesItem> items = blindboxSeriesItemService.createItemsForSeries(request.getItems(), savedBlindboxSeries);

    savedBlindboxSeries.setPackages(packages);
    savedBlindboxSeries.setItems(items);

    for (MultipartFile file : seriesImages) {
      String mediaKey = cloudinaryService.uploadImage(file.getBytes());
      BlindboxAsset asset =
              BlindboxAsset.builder()
                      .mediaKey(mediaKey)
                      .entityId(savedBlindboxSeries.getId())
                      .assetEntityType(AssetEntityType.BLINDBOX_SERIES)
                      .build();
      blindboxAssetService.saveBlindboxAsset(asset);
    }

    blindboxSeriesService.updateBlindboxSeries(savedBlindboxSeries.getId(), savedBlindboxSeries);

    BlindboxSeriesResponse response = mapper.map(savedBlindboxSeries, BlindboxSeriesResponse.class);
    response.setSeriesImageUrl(getThumbnailImageUrl(savedBlindboxSeries.getId(), AssetEntityType.BLINDBOX_SERIES));
    return BaseResponse.build(response, true);
  }

  @Override
  @Transactional
  public BaseResponse<Void> uploadImageForBlindboxItem(Long id, MultipartFile file) throws IOException {
    // Validate input
    if (id == null) {
      throw new IllegalArgumentException("Blindbox item ID cannot be null");
    }
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("Image file is required and cannot be empty");
    }

    // Check file type (optional, adjust as needed)
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new IllegalArgumentException("Only image files are supported");
    }

    // Fetch item
    BlindboxSeriesItem item = blindboxSeriesItemService.getItemById(id);
    if (item == null) {
      throw new IllegalArgumentException("Blindbox item not found with ID: " + id);
    }

    // Handle existing assets
    List<BlindboxAsset> assets = blindboxAssetService.getBlindboxAssetsByEntityIdAndType(id, AssetEntityType.BLINDBOX_SERIES_ITEM);
    if (assets.size() > 1) {
      throw new IllegalStateException("Multiple assets found for item ID: " + id + "; expected at most one");
    }
    if (!assets.isEmpty()) {
      blindboxAssetService.deleteBlindboxAsset(assets.getFirst().getId());
    }

    // Upload new image
    String mediaKey = cloudinaryService.uploadImage(file.getBytes());
    if (mediaKey == null || mediaKey.trim().isEmpty()) {
      throw new IllegalStateException("Failed to upload image to Cloudinary");
    }

    // Create and save new asset
    BlindboxAsset asset = BlindboxAsset.builder()
            .mediaKey(mediaKey)
            .entityId(item.getId())
            .assetEntityType(AssetEntityType.BLINDBOX_SERIES_ITEM)
            .build();
    blindboxAssetService.saveBlindboxAsset(asset);

    return BaseResponse.ok();
  }

  @Override
  @Transactional
  public BaseResponse<Void> uploadImageForBlindboxSeries(Long id, List<MultipartFile> files) throws IOException {
    BlindboxSeries series = blindboxSeriesService.getBlindboxSeriesById(id)
            .orElseThrow(() -> new IllegalArgumentException("Blindbox series not found"));
    return getAssets(files, series.getId(), AssetEntityType.BLINDBOX_SERIES);
  }

  private BaseResponse<Void> getAssets(List<MultipartFile> files, Long id, AssetEntityType entityType) throws IOException {
    for (MultipartFile file : files) {
      String mediaKey = cloudinaryService.uploadImage(file.getBytes());
      BlindboxAsset asset =
              BlindboxAsset.builder()
                      .mediaKey(mediaKey)
                      .entityId(id)
                      .assetEntityType(entityType)
                      .build();
      blindboxAssetService.saveBlindboxAsset(asset);
    }
    return BaseResponse.ok();
  }

  @Override
  public BaseResponse<BlindboxSeriesManagementDetailsResponse> addBlindboxPackagesToSeries(Long seriesId, int count) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public BaseResponse<BlindboxSeriesManagementDetailsResponse> addBlindboxesToSeries(Long seriesId, int count) {
    throw new UnsupportedOperationException("Not implemented yet");
  }


  private String getThumbnailImageUrl(Long entityId, AssetEntityType entityType) {
    return blindboxAssetService.getBlindboxAssetsByEntityIdAndType(entityId, entityType).stream()
            .map(asset -> cloudinaryService.getImageUrl(asset.getMediaKey()))
            .findFirst()
            .orElse(null);
  }

    private List<String> getImageUrls(Long entityId, AssetEntityType entityType) {
        return blindboxAssetService.getBlindboxAssetsByEntityIdAndType(entityId, entityType).stream()
                .map(asset -> cloudinaryService.getImageUrl(asset.getMediaKey()))
                .collect(Collectors.toList());
    }
}
