package com.swd392.preOrderBlindBox.service.service;

public interface CloudinaryService {
  String uploadImage(byte[] image);

  String getImageUrl(String assetKey);
}
