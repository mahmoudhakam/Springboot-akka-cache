package com.se.onprem.services.cache.loader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.SortOrder;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CursorMarkParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import com.google.common.cache.CacheLoader;
import com.se.onprem.dto.business.bom.BOMRiskCacheRequest;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.services.HelperService;
import com.se.onprem.util.UaaConstants;

@Component("BOMFacetLoader")
public class BOMFacetsCacheLoader extends CacheLoader<BOMRiskCacheRequest, Map<String, Map<String, Set<String>>>>
{

	private static final String NOT_FOUND_PARTS_FACET_KEY = "N/A_Z";
	private static final String NOT_FOUND_PART_COM_ID = "0";
	private static final String[] REQUIRED_FIELDS = new String[] { "COM_ID", "MATCH_STATUS", "ROW_KEY" };
	SolrClient bomPartsClient;
	private HelperService helperService;
	private String facetMapServiceURL;

	@Autowired
	public BOMFacetsCacheLoader(SolrClient bomPartsCore, HelperService helperService, String facetMapServiceURL)
	{
		this.bomPartsClient = bomPartsCore;
		this.helperService = helperService;
		this.facetMapServiceURL = facetMapServiceURL;
	}

	@Override
	public Map<String, Map<String, Set<String>>> load(BOMRiskCacheRequest bOMRiskCacheRequest) throws Exception
	{
		SolrQuery bomPartsQuery = new SolrQuery("BOM_ID:" + bOMRiskCacheRequest.getBomId()).setRows(500).setFields(REQUIRED_FIELDS).setSort("ROW_KEY",
				SolrQuery.ORDER.asc);
		String cursor = CursorMarkParams.CURSOR_MARK_START;
		Map<String, Map<String, Set<String>>> totalFacets = new LinkedHashMap<>();
		boolean done = false;
		while(!done)
		{
			bomPartsQuery.set(CursorMarkParams.CURSOR_MARK_PARAM, cursor);
			QueryResponse response = bomPartsClient.query(bomPartsQuery);
			String nextCursorMark = response.getNextCursorMark();
			if(cursor.equals(nextCursorMark))
			{
				done = true;
				continue;
			}
			Map<String, Map<String, Set<String>>> facets = getFacetsFromAPI(response, bOMRiskCacheRequest.getToken());
			appendFactesToResponse(facets, totalFacets);
			cursor = nextCursorMark;
		}

		return totalFacets;
	}

	private void appendFactesToResponse(Map<String, Map<String, Set<String>>> facets, Map<String, Map<String, Set<String>>> totalFacets)
	{
		for(String facetKey : facets.keySet())
		{
			Map<String, Set<String>> newFacets = facets.get(facetKey);
			Map<String, Set<String>> addedFacets = totalFacets.get(facetKey);
			if(addedFacets == null)
			{
				totalFacets.put(facetKey, newFacets);
				continue;
			}
			for(String newFacetValue : newFacets.keySet())
			{
				Set<String> newComIds = newFacets.get(newFacetValue);
				Set<String> addedComIds = addedFacets.get(newFacetValue);
				if(addedComIds == null)
				{
					addedFacets.put(newFacetValue, newComIds);
					continue;
				}
				addedComIds.addAll(newComIds);
			}
		}

	}

	private Map<String, Map<String, Set<String>>> getFacetsFromAPI(QueryResponse response, String token)
	{
		LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		Map<String, List<String>> comIdsList = createComIdsMap(response.getResults());
		params.add("rerankComIDs", StringUtils.join(comIdsList.keySet(), " "));
		HttpHeaders headers = new HttpHeaders();
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set(UaaConstants.AUTHORIZATION, token);

		try
		{
			RestResponseWrapper facetWrapper = helperService.getResposneFromURL(params, headers, facetMapServiceURL);
			Map<String, Map<String, Set<String>>> facetMap = facetWrapper.getFacetMap();
			if(facetMap == null)
			{
				facetMap = new LinkedHashMap<>();
			}
			updateNotFoundFacets(facetMap, comIdsList);
			return facetMap;
		}
		catch(UnsupportedEncodingException e)
		{

			e.printStackTrace();
		}
		return null;
	}

	private Map<String, List<String>> createComIdsMap(SolrDocumentList results)
	{

		Map<String, List<String>> comIdsMap = new HashMap<>();
		for(SolrDocument solrDocument : results)
		{

			String comId = String.valueOf(solrDocument.getFieldValue("COM_ID"));
			String rowKey = String.valueOf(solrDocument.getFieldValue("ROW_KEY"));
			List<String> addedComIdsList = comIdsMap.get(comId);
			if(addedComIdsList == null)
			{
				addedComIdsList = new ArrayList<String>();
				comIdsMap.put(comId, addedComIdsList);
			}
			addedComIdsList.add(rowKey);

		}

		return comIdsMap;
	}

	private void updateNotFoundFacets(Map<String, Map<String, Set<String>>> facetMap, Map<String, List<String>> comIdsMap)
	{
		for(Map.Entry<String, Map<String, Set<String>>> entry : facetMap.entrySet())
		{
			Set<String> emptyValues = entry.getValue().remove("");
			if(emptyValues != null)
			{
				entry.getValue().put("N/A_E", emptyValues);
			}

			for(Map.Entry<String, Set<String>> keywordFacet : entry.getValue().entrySet())
			{
				Set<String> updatedFacetValues = updateFacetByRowKeys(keywordFacet.getValue(), comIdsMap);
				entry.getValue().put(keywordFacet.getKey(), updatedFacetValues);
			}
			List<String> notFoundComIds = comIdsMap.get(NOT_FOUND_PART_COM_ID);
			if(notFoundComIds != null)
			{
				entry.getValue().put(NOT_FOUND_PARTS_FACET_KEY, new LinkedHashSet<>(notFoundComIds));
			}
		}

	}

	private Set<String> updateFacetByRowKeys(Set<String> value, Map<String, List<String>> comIdsMap)
	{
		Set<String> updatedFactes = new LinkedHashSet<String>();
		for(String comId : value)
		{
			updatedFactes.addAll(comIdsMap.get(comId));
		}
		return updatedFactes;
	}

}
