package com.se.onprem.services.cache.loader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.common.cache.CacheLoader;
import com.se.onprem.dto.business.bom.BOMRiskCacheRequest;

@Component("BOMRiskLoader")
public class BOMRiskCacheLoader extends CacheLoader<BOMRiskCacheRequest, Map<String, Map<String, Set<String>>>>
{
	private final String LIFE_CYCLE_RISK = "LC_STATE_RISK";
	private final String LIFE_CYCLE = "LC_STATE";
	private final String ROHS_RISK = "ROHS_RISK";
	private final String ROHS = "ROHS";

	private final String RISK_HIGH = "High";
	private final String RISK_MEDIUM = "Medium";
	private final String RISK_LOW = "Low";
	private final String RISK_UNKNOWN = "Unknown";

	@Override
	public Map<String, Map<String, Set<String>>> load(BOMRiskCacheRequest bOMRiskCacheRequest) throws Exception
	{
		Map<String, Map<String, Set<String>>> riskFacets = new HashMap<>();
		// riskFacets.put(LIFE_CYCLE_RISK, putLC(bOMRiskCacheRequest.getCachedBOMFacets()));
		// riskFacets.put(ROHS_RISK, putROHS(bOMRiskCacheRequest.getCachedBOMFacets()));

		riskFacets.put(LIFE_CYCLE_RISK, new HashMap<>());
		riskFacets.put(ROHS_RISK, new HashMap<>());

		return riskFacets;
	}

	private Map<String, Set<String>> putLC(Map<String, Map<String, Set<String>>> bomFacets)
	{
		Map<String, Set<String>> lcMap = getEachRiskMap();

		for(Map.Entry<String, Set<String>> facet : bomFacets.get(LIFE_CYCLE).entrySet())
		{
			for(String comId : facet.getValue())
			{
				if(facet.getKey().equals("Active") && getYOELForLC(comId))
				{
					lcMap.get(RISK_HIGH).add(comId);
				}
				else if(facet.getKey().equals("Active") && !getYOELForLC(comId))
				{
					lcMap.get(RISK_MEDIUM).add(comId);
				}
				else if(!facet.getKey().equals("Active") && getYOELForLC(comId))
				{
					lcMap.get(RISK_LOW).add(comId);
				}
				else
				{
					lcMap.get(RISK_UNKNOWN).add(comId);
				}
			}
		}
		return lcMap;

	}

	private Map<String, Set<String>> putROHS(Map<String, Map<String, Set<String>>> bomFacets)
	{
		Map<String, Set<String>> rohsMap = getEachRiskMap();

		for(Map.Entry<String, Set<String>> facet : bomFacets.get(ROHS).entrySet())
		{
			for(String comId : facet.getValue())
			{
				if(facet.getKey().equals("Unknown") && getYOELForLC(comId))
				{
					rohsMap.get(RISK_HIGH).add(comId);
				}
				else if(facet.getKey().equals("No") && !getYOELForLC(comId))
				{
					rohsMap.get(RISK_MEDIUM).add(comId);
				}
				else if(!facet.getKey().equals("Yes") && getYOELForLC(comId))
				{
					rohsMap.get(RISK_LOW).add(comId);
				}
				else
				{
					rohsMap.get(RISK_UNKNOWN).add(comId);
				}
			}
		}
		return rohsMap;

	}

	private Map<String, Set<String>> getEachRiskMap()
	{
		Map<String, Set<String>> riskMap = new HashMap<>();
		riskMap.put(RISK_HIGH, new HashSet<>());
		riskMap.put(RISK_MEDIUM, new HashSet<>());
		riskMap.put(RISK_LOW, new HashSet<>());
		riskMap.put(RISK_UNKNOWN, new HashSet<>());
		return riskMap;
	}

	private boolean getYOELForLC(String comId)
	{
		if(Long.valueOf(comId.substring(0, comId.indexOf("||"))) % 2 == 0)
		{
			return true;
		}
		return false;
	}

}
