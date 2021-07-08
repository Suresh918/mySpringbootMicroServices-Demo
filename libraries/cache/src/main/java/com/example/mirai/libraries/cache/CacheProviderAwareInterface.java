package com.example.mirai.libraries.cache;

import java.util.List;

public interface CacheProviderAwareInterface {
	List<String> searchKeys(String pattern);
}
