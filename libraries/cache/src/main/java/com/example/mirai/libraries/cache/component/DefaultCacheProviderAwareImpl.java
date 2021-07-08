package com.example.mirai.libraries.cache.component;

import java.util.ArrayList;
import java.util.List;

import com.example.mirai.libraries.cache.CacheProviderAwareInterface;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "none", matchIfMissing = true)
public class DefaultCacheProviderAwareImpl implements CacheProviderAwareInterface {

	@Override
	public List<String> searchKeys(String pattern) {
		return new ArrayList<>();
	}
}
