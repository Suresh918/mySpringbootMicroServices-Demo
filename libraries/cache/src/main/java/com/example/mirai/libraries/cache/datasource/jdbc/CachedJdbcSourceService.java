package com.example.mirai.libraries.cache.datasource.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import com.example.mirai.libraries.cache.CacheProviderAwareInterface;
import com.example.mirai.libraries.cache.CacheableEntityInterface;
import com.example.mirai.libraries.cache.config.CacheConfiguration;
import com.example.mirai.libraries.jdbcdatasource.service.JdbcClient;
import com.example.mirai.libraries.jdbcdatasource.service.JdbcSourceService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

@CacheConfig(cacheResolver = "customCacheResolver")
@Getter
@Slf4j
public abstract class CachedJdbcSourceService<E extends CacheableEntityInterface> extends JdbcSourceService<E> {

	protected final CacheProviderAwareInterface cacheProviderAware;

	protected final CacheConfiguration cacheConfiguration;

	public CachedJdbcSourceService(JdbcClient jdbcClient, CacheProviderAwareInterface cacheProviderAware,
			CacheConfiguration cacheConfiguration) {
		super(jdbcClient);
		this.cacheProviderAware = cacheProviderAware;
		this.cacheConfiguration = cacheConfiguration;
	}

	@Override
	public abstract CachedJdbcSourceService getSelf();

	public abstract String getSqlForBulk();

	public abstract boolean isCacheEnabled();

	public void refreshEntities() {
        ResultSet resultSet = jdbcClient.executeQuery(getSqlForBulk());
        try {
			while (resultSet.next())
				getSelf().convertResultSetToEntityAndCache(resultSet);
		} catch (Exception exception) {
        	exception.printStackTrace();
		}
	}

	@CachePut(key = "#result.getClass().getCanonicalName() + \":\" + #result.getId().toUpperCase()",
			condition = "#root.target.isCacheEnabled() && @cacheConfigSpringCacheConfiguration !=null &&  !(@cacheConfigSpringCacheConfiguration.getType().equalsIgnoreCase(\"NONE\"))",
			unless = "#result==null")
	public E convertResultSetToEntityAndCache(ResultSet resultSet) {
		E e = (E) getSelf().convertResultSet(resultSet);
		log.info("Caching " + e.getClass() + " " + e.getId());
		return e;
	}

	@Cacheable(key = "#root.target.getEntityClass().getCanonicalName() + \":\" + #root.args[0].toUpperCase()",
			condition = "#root.target.isCacheEnabled() && @cacheConfigSpringCacheConfiguration !=null &&  !(@cacheConfigSpringCacheConfiguration.getType().equalsIgnoreCase(\"NONE\"))",
			unless = "#result==null")
	@Override
	public E getEntity(String id) {
		return super.getEntity(id);
	}

	@Override
	public List<E> getEntities(List<String> ids) {
		return super.getEntities(ids);
	}

	public List<E> searchEntities(String pattern) {
		String cacheableEntityClassName = getEntityClass().getCanonicalName();
		pattern = pattern.replaceAll("^\\*|\\*$","");
		pattern = "*" + cacheableEntityClassName + ":*" + pattern.toUpperCase() + "*";
		List<String> keys = cacheProviderAware.searchKeys(pattern);
		List<String> ids = keys.stream().map(key -> key.substring(key.indexOf(cacheableEntityClassName + ":") + cacheableEntityClassName.length()+1)).collect(Collectors.toList());
		return getEntities(ids);
	}

}
