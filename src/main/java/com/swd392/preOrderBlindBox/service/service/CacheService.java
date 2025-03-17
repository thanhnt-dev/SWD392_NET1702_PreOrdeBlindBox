package com.swd392.preOrderBlindBox.service.service;

import java.util.concurrent.TimeUnit;

public interface CacheService {
  void store(String key, Object value, Integer timeout, TimeUnit timeUnit);

  void store(String key, Object value);

  Object retrieve(String key);

  void delete(String key);

  Boolean hasKey(String key);
}
