package com.example.mirai.libraries.cache.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.mirai.libraries.cache.CacheProviderAwareInterface;
import lombok.AllArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "redis")
@AllArgsConstructor
public class RedisCacheProviderAwareImpl implements CacheProviderAwareInterface {
	private final RedisTemplate redisTemplate;

	public List<String> searchKeys(String pattern) {
		Set<String> keys = redisTemplate.keys(pattern);
		List list = new ArrayList<String>();
		list.addAll(keys);
		return list;
	}
}
