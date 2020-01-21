package com.se.onprem.services.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.se.onprem.dto.business.bom.BomExportFeature;

@Service
public class ExportPartsCache
{
	@Value("#{environment['export.cache.size']}")
	int maxCacheSize;
	@Value("#{environment['export.cache.duration']}")
	int maxCacheDuration;

	private Cache<String, Map<String, List<BomExportFeature>>> exportPartsCache;

	public ExportPartsCache()
	{
	}

	@PostConstruct
	void init()
	{
		exportPartsCache = CacheBuilder.newBuilder().maximumSize(maxCacheSize).expireAfterWrite(maxCacheDuration, TimeUnit.HOURS).build();
	}

	public void addPartToCache(String key, Map<String, List<BomExportFeature>> result)
	{
		exportPartsCache.put(key, result);
	}

	public Map<String, List<BomExportFeature>> getPartOrNull(Object key)
	{
		return exportPartsCache.getIfPresent(key);
	}

	public ConcurrentMap<String, Map<String, List<BomExportFeature>>> getAllCachedMap()
	{
		return exportPartsCache.asMap();
	}
}
