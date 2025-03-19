package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.common.enums.PackageStatus;
import com.swd392.preOrderBlindBox.entity.Blindbox;
import com.swd392.preOrderBlindBox.entity.BlindboxPackage;
import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.restcontroller.request.BlindboxSeriesCreateRequest;

import java.util.List;

public interface BlindboxPackageService {
  List<BlindboxPackage> createPackagesForSeries(BlindboxSeriesCreateRequest request, BlindboxSeries series);

  List<BlindboxPackage> createWholesalePackages(int numberOfPackages, BlindboxSeries series);

  List<BlindboxPackage> createSeparatedSalePackages(int numberOfBlindboxesPerPackage, int numberOfPackages, BlindboxSeries series);

  BlindboxPackage getBlindboxPackageById(Long id);

  List<BlindboxPackage> getBlindboxPackagesBySeriesId(Long seriesId);

  BlindboxPackage createBlindboxPackage(BlindboxPackage blindboxPackage);

  BlindboxPackage updateBlindboxPackage(Long id, BlindboxPackage blindboxPackage);

  void deactivateBlindboxPackage(Long id);

  void updatePackageStatus(Long id, PackageStatus status);

  List<Blindbox> generateBlindboxesForPackage(Long packageId);

  int getAvailableBlindboxQuantityOfPackageByPackageId(Long packageId);

  List<BlindboxPackage> getPackagesForWholeSaleOfSeries(Long seriesId);

  List<BlindboxPackage> getPackagesForSeparatedSaleOfSeries(Long seriesId);
}
