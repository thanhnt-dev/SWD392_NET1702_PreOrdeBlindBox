package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.service.service.CacheService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheIServicempl implements CacheService {

  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public void store(String key, Object value, Integer timeout, TimeUnit timeUnit) {
    this.redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
  }

  @Override
  public void store(String key, Object value) {
    this.redisTemplate.opsForValue().set(key, value);
  }

  @Override
  public Object retrieve(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  @Override
  public void delete(String key) {
    this.redisTemplate.delete(key);
  }

  @Override
  public Boolean hasKey(String key) {
    return this.redisTemplate.hasKey(key);
  }
}
