package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.enums.PackageStatus;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.entity.Blindbox;
import com.swd392.preOrderBlindBox.entity.BlindboxPackage;
import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.repository.repository.BlindboxPackageRepository;
import com.swd392.preOrderBlindBox.repository.repository.BlindboxRepository;
import com.swd392.preOrderBlindBox.restcontroller.request.BlindboxSeriesCreateRequest;
import com.swd392.preOrderBlindBox.service.service.BlindboxPackageService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlindboxPackageServiceImpl implements BlindboxPackageService {
  private final BlindboxPackageRepository blindboxPackageRepository;
  private final BlindboxRepository blindboxRepository;

  @Override
  public List<BlindboxPackage> createPackagesForSeries(BlindboxSeriesCreateRequest request, BlindboxSeries series) {
    List<BlindboxPackage> packages = new ArrayList<>();
    packages.addAll(createWholesalePackages(request.getNumberOfWholeSalePackage(), series));
    packages.addAll(createSeparatedSalePackages(request, series));
    return packages;
  }

  @Override
  public List<BlindboxPackage> createWholesalePackages(int numberOfPackages, BlindboxSeries series) {
    return IntStream.range(0, numberOfPackages)
            .mapToObj(i -> BlindboxPackage.builder()
                    .series(series)
                    .blindboxes(null)
                    .currentSoldUnits(6)
                    .totalUnits(6)
                    .status(PackageStatus.SEALED)
                    .build())
            .map(this::createBlindboxPackage)
            .collect(Collectors.toList());
  }

  @Override
  public List<BlindboxPackage> createSeparatedSalePackages(int numberOfBlindboxesPerPackage, int numberOfPackages, BlindboxSeries series) {
    return getBlindboxPackagesForSeparatedSale(numberOfPackages, series, numberOfBlindboxesPerPackage);
  }

  private List<BlindboxPackage> createSeparatedSalePackages(BlindboxSeriesCreateRequest request, BlindboxSeries series) {
    int numberOfPackages = request.getNumberOfSeparatedSalePackage();
    int blindboxesPerPackage = request.getNumberOfBlindboxesPerPackage();

    return getBlindboxPackagesForSeparatedSale(numberOfPackages, series, blindboxesPerPackage);
  }

  private List<BlindboxPackage> getBlindboxPackagesForSeparatedSale(int numberOfPackages, BlindboxSeries series, int blindboxesPerPackage) {
    return IntStream.range(0, numberOfPackages)
            .mapToObj(i -> {
              BlindboxPackage pkg = BlindboxPackage.builder()
                      .series(series)
                      .currentSoldUnits(0)
                      .totalUnits(blindboxesPerPackage)
                      .status(PackageStatus.UNPACKED)
                      .build();
              BlindboxPackage savedPkg = createBlindboxPackage(pkg);
              List<Blindbox> blindboxes = createBlindboxesForPackage(blindboxesPerPackage, savedPkg);
              savedPkg.setBlindboxes(blindboxes);
              return savedPkg;
            })
            .collect(Collectors.toList());
  }

  private List<Blindbox> createBlindboxesForPackage(int count, BlindboxPackage blindboxPackage) {
    List<Blindbox> blindboxes = IntStream.range(0, count)
            .mapToObj(i -> Blindbox.builder()
                    .blindboxPackage(blindboxPackage)
                    .isSold(false)
                    .revealedItem(null)
                    .build())
            .collect(Collectors.toList());

    return blindboxRepository.saveAll(blindboxes);
  }


  @Override
  public BlindboxPackage getBlindboxPackageById(Long id) {
    return blindboxPackageRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
  }

  @Override
  public List<BlindboxPackage> getBlindboxPackagesBySeriesId(Long seriesId) {
    return blindboxPackageRepository.findBySeriesId(seriesId);
  }

  @Override
  public BlindboxPackage createBlindboxPackage(BlindboxPackage blindboxPackage) {
    return blindboxPackageRepository.save(blindboxPackage);
  }

  @Override
  public BlindboxPackage updateBlindboxPackage(Long id, BlindboxPackage blindboxPackage) {
    BlindboxPackage existingBlindboxPackage =
        blindboxPackageRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    existingBlindboxPackage.setTotalUnits(blindboxPackage.getTotalUnits());
    existingBlindboxPackage.setCurrentSoldUnits(blindboxPackage.getCurrentSoldUnits());
    existingBlindboxPackage.setStatus(blindboxPackage.getStatus());

    return blindboxPackageRepository.save(existingBlindboxPackage);
  }

  @Override
  public void deactivateBlindboxPackage(Long id) {
    BlindboxPackage blindboxPackage =
        blindboxPackageRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    blindboxPackage.setActive(false);
    blindboxPackageRepository.save(blindboxPackage);
  }

  @Override
  public void updatePackageStatus(Long id, PackageStatus status) {
    BlindboxPackage blindboxPackage =
        blindboxPackageRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    blindboxPackage.setStatus(status);
    blindboxPackageRepository.save(blindboxPackage);
  }

  @Override
  public List<Blindbox> generateBlindboxesForPackage(Long packageId) {
    // Implementation logic should be added here based on business requirements
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public int getAvailableBlindboxQuantityOfPackageByPackageId(Long packageId) {
    return (int)
        blindboxRepository.findByBlindboxPackageId(packageId).stream()
            .filter(blindbox -> !blindbox.getIsSold())
            .count();
  }

  @Override
  public List<BlindboxPackage> getPackagesForWholeSaleOfSeries(Long seriesId) {
    return blindboxPackageRepository.findBySeriesId(seriesId).stream()
        .filter(pkg -> pkg.getStatus() == PackageStatus.SEALED)
        .toList();
  }

  @Override
  public List<BlindboxPackage> getPackagesForSeparatedSaleOfSeries(Long seriesId) {
    return blindboxPackageRepository.findBySeriesId(seriesId).stream()
        .filter(pkg -> pkg.getStatus() == PackageStatus.UNPACKED)
        .toList();
  }
}
