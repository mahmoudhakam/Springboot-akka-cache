package com.se.onprem.services.cache.loader;

import org.springframework.stereotype.Component;

import com.google.common.cache.CacheLoader;

@Component("sePartCountCacheLoader")
public class SEPartsCountCacheLoader extends CacheLoader<String, Integer>
{

	@Override
	public Integer load(String arg0) throws Exception
	{
		
		return null;
	}

}
