package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.cloudinary.utils.ObjectUtils;
import com.swd392.preOrderBlindBox.infrastructure.config.CloudinaryConfig;
import com.swd392.preOrderBlindBox.service.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

  private final CloudinaryConfig cloudinaryConfig;

  private String defaultImage =
      "https://support.heberjahiz.com/hc/article_attachments/21013076295570";

  @Override
  public String uploadImage(byte[] image) {
    var params =
        ObjectUtils.asMap(
            "folder", "eCommerce",
            "resource_type", "image");
    try {
      var uploadResult = cloudinaryConfig.cloudinary().uploader().upload(image, params);
      return uploadResult.get("asset_id").toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getImageUrl(String assetKey) {
    try {
      var imageUrl =
          cloudinaryConfig.cloudinary().api().resourceByAssetID(assetKey, ObjectUtils.emptyMap());
      return imageUrl.get("secure_url").toString();
    } catch (Exception e) {
      return defaultImage;
    }
  }
}
