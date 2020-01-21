package com.se.onprem.services.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.se.onprem.dto.business.ACLQueryResult;
import com.se.onprem.dto.business.bom.BOMRiskCacheRequest;

@Service
public class CacheService
{
	CacheLoader<String, Integer> customerPartCountCacheLoader;
	LoadingCache<String, Integer> customerCountCache;
	CacheLoader<String, Integer> sePartCountCacheLoader;
	LoadingCache<String, Integer> seCountCache;
	CacheLoader<String, ACLQueryResult> customerComidCacheLoader;
	LoadingCache<String, ACLQueryResult> customerComidCache;
	CacheLoader<BOMRiskCacheRequest, Map<String, Map<String, Set<String>>>> BOMFacetLoader;
	LoadingCache<BOMRiskCacheRequest, Map<String, Map<String, Set<String>>>> BOMFacetCache;
	CacheLoader<BOMRiskCacheRequest, Map<String, Map<String, Set<String>>>> BOMRiskLoader;
	LoadingCache<BOMRiskCacheRequest, Map<String, Map<String, Set<String>>>> BOMRiskCache;

	@Autowired
	public CacheService(CacheLoader<String, Integer> customerPartCountCacheLoader, CacheLoader<String, Integer> sePartCountCacheLoader,
			CacheLoader<String, ACLQueryResult> customerComidCacheLoader,
			CacheLoader<BOMRiskCacheRequest, Map<String, Map<String, Set<String>>>> BOMFacetLoader,
			CacheLoader<BOMRiskCacheRequest, Map<String, Map<String, Set<String>>>> BOMRiskLoader)
	{
		super();
		this.customerPartCountCacheLoader = customerPartCountCacheLoader;
		this.sePartCountCacheLoader = sePartCountCacheLoader;
		this.customerComidCacheLoader = customerComidCacheLoader;
		this.BOMFacetLoader = BOMFacetLoader;
		this.BOMRiskLoader = BOMRiskLoader;
		customerComidCache = CacheBuilder.newBuilder().build(customerComidCacheLoader);
		customerCountCache = CacheBuilder.newBuilder().build(customerPartCountCacheLoader);
		seCountCache = CacheBuilder.newBuilder().build(sePartCountCacheLoader);
		BOMFacetCache = CacheBuilder.newBuilder().build(BOMFacetLoader);
		BOMRiskCache = CacheBuilder.newBuilder().build(BOMRiskLoader);

	}

	public int getCustomerPartsCountForQuery(String query)
	{
		try
		{
			return customerCountCache.get(query);
		}
		catch(ExecutionException e)
		{

			e.printStackTrace();
			return 0;
		}
	}

	public ACLQueryResult getCustomerPartsComidListForQuery(String query)
	{
		try
		{
			return customerComidCache.get(query);
		}
		catch(ExecutionException e)
		{

			e.printStackTrace();
			return null;
		}
	}

	public Map<String, Map<String, Set<String>>> getBomFacets(String bomId, String token)
	{
		try
		{
			return BOMFacetCache.get(new BOMRiskCacheRequest(bomId, null, token));
		}
		catch(ExecutionException e)
		{

			e.printStackTrace();
			return null;
		}
	}

	public Map<String, Map<String, Set<String>>> getBomRisks(String bomId, Map<String, Map<String, Set<String>>> bomFacets, String token)
	{
		try
		{
			return BOMRiskCache.get(new BOMRiskCacheRequest(bomId, bomFacets, token));
		}
		catch(ExecutionException e)
		{

			e.printStackTrace();
			return null;
		}
	}
}
