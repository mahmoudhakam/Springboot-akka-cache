package com.se.onprem.strategy.bom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.onprem.dto.business.bom.BOMMesssage;
import com.se.onprem.dto.business.bom.BOMRow;
import com.se.onprem.dto.ws.BomMatchFacets;
import com.se.onprem.dto.ws.KeywordFacet;
import com.se.onprem.dto.ws.MatchFiltersRequestDTO;
import com.se.onprem.dto.ws.PartSearchDTO;
import com.se.onprem.dto.ws.PartSearchResult;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.services.HelperService;
import com.se.onprem.services.cache.CacheService;
import com.se.onprem.util.ParametricConstants;
import com.se.onprem.util.SolrReader;
import com.se.onprem.util.UaaConstants;

@Service
public class BOMOpen implements IBOMActions
{

	private static final String N_A_Z = "N/A_Z";
	private static final String N_A_E = "N/A_E";
	private static final String N_A = "N/A";
	@Autowired
	private SolrReader<BOMRow> solrReader;
	@Autowired
	private SolrClient bomPartsCore;
	@Autowired
	HelperService helper;
	@Autowired
	CacheService cacheService;

	private Map<String, Map<String, String>> facetFieldsMapping;

	private final String partMatchStatus = "Part Match Status";
	private final String partMatchType = "Part Match Type";
	private final String facetNameDefault = "No Match";
	private final String facetNameMatchExact = "Exact";
	private final String facetNameNoMatchSimilar = "No match similar";

	@PostConstruct
	public void init()
	{
		Map<String, String> matchStatusMapping = new LinkedHashMap<>();
		matchStatusMapping.put("1", facetNameMatchExact);

		Map<String, String> matchTypeMapping = new LinkedHashMap<>();
		matchTypeMapping.put("1", facetNameMatchExact);
		matchTypeMapping.put("5", facetNameNoMatchSimilar);
		matchTypeMapping.put("7", facetNameNoMatchSimilar);
		matchTypeMapping.put("8", facetNameNoMatchSimilar);
		matchTypeMapping.put("9", facetNameNoMatchSimilar);

		facetFieldsMapping = new LinkedHashMap<>();

		facetFieldsMapping.put(partMatchStatus, matchStatusMapping);
		facetFieldsMapping.put(partMatchType, matchTypeMapping);

	}

	@Override
	public BOMMesssage doAction(BOMMesssage message)
	{
		int pageSize = ParametricConstants.ServiceDefaults.MAX_PARTS_PER_PAGE;
		Map<String, Map<String, Set<String>>> bomFacetsOriginal = cacheService.getBomFacets(message.getBomId(), message.getToken());
		Map<String, Map<String, Set<String>>> bomRiskFacets = cacheService.getBomRisks(message.getBomId(), bomFacetsOriginal, message.getToken());
		Map<String, Map<String, Set<String>>> filteredBomFacets = new LinkedHashMap<>();
		Map<String, Map<String, Set<String>>> filteredBomFacetsWithoutLastFilter = new LinkedHashMap<>();
		Map<String, List<String>> filtersRequest = message.getFiltersRequest();
		Map<String, List<String>> matchFiltersRequest = message.getMatchFiltersRequest();
		Map<String, Set<String>> orderedFacet;
		Map<String, Map<String, Set<String>>> bomFacets = new LinkedHashMap<>();
		Set<String> comIds = new LinkedHashSet<>();
		Set<String> comIdsWithoutLastFilter = new HashSet<>();
		Map<String, MatchFiltersRequestDTO> matchFilters = new LinkedHashMap<>();

		boolean sortFromFacets = message.getSortField() != null && bomFacetsOriginal.get(message.getSortField()) != null;

		boolean isExact = false, isNoMatch = false;
		
		if(!StringUtils.isEmpty(message.getSortType()) && ORDER.valueOf(message.getSortType()).equals(ORDER.desc))
		{
			orderedFacet = new TreeMap<>(Collections.reverseOrder());
		}
		else
		{
			orderedFacet = new TreeMap<>();
		}

		if(sortFromFacets && matchFiltersRequest != null && matchFiltersRequest.size() > 0)
		{
			for(Map.Entry<String, List<String>> matchFiltersRequestEntry : matchFiltersRequest.entrySet())
			{
				for(String matchEntry : matchFiltersRequestEntry.getValue())
				{
					if(StringUtils.equals(facetNameMatchExact, matchEntry))
					{
						isExact = true;
					}
					if(StringUtils.equals(facetNameNoMatchSimilar, matchEntry) || StringUtils.equals(facetNameDefault, matchEntry))
					{
						isNoMatch = true;
					}
				}
			}
		}

		if(matchFiltersRequest != null)
		{
			getFilteredBomMatchFacets(matchFiltersRequest, matchFilters);
		}

		for(Map.Entry<String, Map<String, Set<String>>> bomFacetsOriginalEntry : bomFacetsOriginal.entrySet())
		{

			Map<String, Set<String>> facetMap = new LinkedHashMap<>();

			for(Map.Entry<String, Set<String>> facetEntry : bomFacetsOriginalEntry.getValue().entrySet())
			{
				facetMap.put(facetEntry.getKey(), new HashSet(facetEntry.getValue()));
				if(filtersRequest == null && StringUtils.equals(message.getSortField(), bomFacetsOriginalEntry.getKey()))
				{
					if((isExact == isNoMatch) || (isExact && !isNoMatch && !(StringUtils.equals(facetEntry.getKey(), N_A_Z))) || (isNoMatch
							&& !isExact && (StringUtils.equals(facetEntry.getKey(), N_A_E) || (StringUtils.equals(facetEntry.getKey(), N_A_Z)))))
					{
						orderedFacet.put(facetEntry.getKey(), new HashSet(facetEntry.getValue()));
					}
				}
			}

			bomFacets.put(bomFacetsOriginalEntry.getKey(), facetMap);
		}

		bomFacets.putAll(bomRiskFacets);

		String lastFilter = message.getLastFilter();
		String matchLastFilter = null;

		if(facetFieldsMapping.get(lastFilter) != null)
		{
			matchLastFilter = lastFilter;
			lastFilter = null;
		}

		if(filtersRequest != null)
		{
			Map<String, List<String>> filtersRequestWithoutLastFilter = new LinkedHashMap<>(message.getFiltersRequest());
			filtersRequestWithoutLastFilter.remove(lastFilter);

			orderedFacet.clear();
			getFilteredBomFacets(bomFacets, filteredBomFacets, filtersRequest, comIds, message, orderedFacet, isExact, isNoMatch);

			if(filtersRequestWithoutLastFilter.isEmpty())
			{
				filteredBomFacetsWithoutLastFilter.putAll(bomFacets);
			}
			else
			{

				getFilteredBomFacets(bomFacets, filteredBomFacetsWithoutLastFilter, filtersRequestWithoutLastFilter, comIdsWithoutLastFilter, message,
						null, false, false);
			}

			if(filteredBomFacets.get(lastFilter) != null)
			{
				filteredBomFacets.put(lastFilter, filteredBomFacetsWithoutLastFilter.get(lastFilter));
			}

		}

		StringBuilder solrQuery = new StringBuilder("BOM_ID:" + message.getBomId());
		StringBuilder solrQueryWithoutMatchLastFilter = new StringBuilder();

		Set<String> sortedComIds = new LinkedHashSet<>();

		if(orderedFacet != null && orderedFacet.size() > 0)
		{
			int index = 0;
			int startRow = (message.getPageNumber() - 1) * pageSize;

			for(Map.Entry<String, Set<String>> orderedFacetEntry : orderedFacet.entrySet())
			{
				for(String comId : orderedFacetEntry.getValue())
				{
					if(index >= startRow + pageSize)
					{
						break;
					}
					if(index >= startRow)
					{
						sortedComIds.add(comId);
					}
					index++;
				}

				if(index >= startRow + pageSize)
				{
					break;
				}
			}

			System.out.println(orderedFacet);
			System.out.println(sortedComIds.toString());
			System.out.println(sortedComIds.size());

		}

		if(message.getFiltersRequest() != null)
		{
			if(comIds.size() == 0)
			{
				return message;
			}
			solrQuery.append(buildQuery(" AND ROW_KEY", comIds));
		}

		solrQueryWithoutMatchLastFilter.append(solrQuery.toString());

		if(message.getMatchFiltersRequest() != null)
		{

			solrQuery = appendMatchFiltersToQuery(matchFilters, solrQuery);

			if(matchLastFilter != null)
			{
				matchFilters.remove(matchLastFilter);
				solrQueryWithoutMatchLastFilter = appendMatchFiltersToQuery(matchFilters, solrQueryWithoutMatchLastFilter);
			}
		}

		SolrQuery query = createSolrQuery(message, pageSize, solrQuery, sortFromFacets, sortedComIds);
		QueryResponse response, responseWithoutMatchLastFilter;
		try
		{
			response = bomPartsCore.query(query, METHOD.POST);
			if(response == null)
			{
				return message;
			}

			BomMatchFacets BomMatchFacetsResponse = getBomMatchFacets(response.getFacetField("MATCH_STATUS_CODE").getValues());

			BomMatchFacets BomMatchFacetsResponseWithoutMatchLastFilter = null;

			if(matchLastFilter != null)
			{
				query = createSolrQueryForMatchLastFilter(message, solrQueryWithoutMatchLastFilter);
				responseWithoutMatchLastFilter = bomPartsCore.query(query, METHOD.POST);
				BomMatchFacetsResponseWithoutMatchLastFilter = getBomMatchFacets(
						responseWithoutMatchLastFilter.getFacetField("MATCH_STATUS_CODE").getValues());
				BomMatchFacetsResponse.getBomMatchFacets().put(matchLastFilter,
						BomMatchFacetsResponseWithoutMatchLastFilter.getBomMatchFacets().get(matchLastFilter));
			}

			List<BOMRow> partsList = solrReader.objectsListFromSolr(response.getResults(), BOMRow.class);

			if(sortFromFacets)
			{
				Map<String, BOMRow> partsMap = new HashMap<>();
				for(BOMRow bomRow : partsList)
				{
					partsMap.put(bomRow.getRowKey(), bomRow);
				}

				partsList = new ArrayList<>();

				for(String comId : sortedComIds)
				{
					if(partsMap.get(comId) != null)
					{
						partsList.add(partsMap.get(comId));
					}
				}
			}

			Map<String, List<BOMRow>> comIdsToParts = createComIdsTopartsMap(partsList);

			Map<String, String> customHeaders = new HashMap<>();
			customHeaders.put(UaaConstants.AUTHORIZATION, message.getToken());

			RestResponseWrapper seData = helper.getComidsRequestResult(comIdsToParts.keySet(), customHeaders);
			List<PartSearchResult> seResult = seData.getKeywordResults();
			if(seResult != null)
			{

				completeBOMRowsWithSEData(comIdsToParts, seResult);
			}
			BOMMesssage resultMessage = new BOMMesssage();
			resultMessage.setBomParts(partsList);

			if(message.getMatchFiltersRequest() == null || BomMatchFacetsResponse.isExact())
			{
				if(filtersRequest != null)
				{
					resultMessage.setBomFacets(filteredBomFacets);
				}
				else
				{
					resultMessage.setBomFacets(bomFacets);
				}
			}
			else
			{
				resultMessage.setBomFacets(createEmptyBomFacet(bomFacets));
			}
			resultMessage.setExact(BomMatchFacetsResponse.isExactOnly());
			resultMessage.setBomMatchFacets(BomMatchFacetsResponse.getBomMatchFacets());
			resultMessage.setResultCount(response.getResults().getNumFound());
			resultMessage.setLastFilter(lastFilter);
			// resultMessage.setBomRiskFacets(bomRiskFacets);
			return resultMessage;
		}
		catch(SolrServerException | IOException e)
		{

			e.printStackTrace();
		}
		return message;
	}

	private SolrQuery createSolrQuery(BOMMesssage message, int pageSize, StringBuilder solrQuery, boolean sortFromFacets, Set<String> sortedComIds)
	{

		int pageNumber = message.getPageNumber();

		if(sortFromFacets)
		{
			pageNumber = 1;
		}

		SolrQuery query = new SolrQuery(solrQuery.toString()).setRows(pageSize).setStart((pageNumber - 1) * pageSize).setFacet(true)
				.addFacetField("MATCH_STATUS_CODE").setFacetMinCount(1);

		if(!StringUtils.isEmpty(message.getSortField()) && BOMRow.getSolrFromJson(message.getSortField()) != null)
		{
			query = query.setSort(BOMRow.getSolrFromJson(message.getSortField()), ORDER.valueOf(message.getSortType()));
		}
		else if(sortFromFacets)
		{
			query.addSort("query($rx,0)", ORDER.desc);
			query.set("rx", "ROW_KEY:(" + StringUtils.join(sortedComIds, " ") + ")");
		}
		else
		{
			query = query.setSort("ROW_ID", ORDER.asc);
		}

		System.out.println(query.toString());
		return query;
	}

	private SolrQuery createSolrQueryForMatchLastFilter(BOMMesssage message, StringBuilder solrQuery)
	{
		SolrQuery query = new SolrQuery(solrQuery.toString()).setRows(0).setFacet(true).addFacetField("MATCH_STATUS_CODE").setFacetMinCount(1);
		return query;
	}

	private StringBuilder appendMatchFiltersToQuery(Map<String, MatchFiltersRequestDTO> matchFilters, StringBuilder solrQuery)
	{
		for(Map.Entry<String, MatchFiltersRequestDTO> MatchFiltersQuery : matchFilters.entrySet())
		{

			if(MatchFiltersQuery.getValue().getMatchFilters().size() > 0 || MatchFiltersQuery.getValue().getNotMatchFilters().size() > 0)
			{
				solrQuery.append(" AND (");
			}

			if(MatchFiltersQuery.getValue().getMatchFilters().size() > 0)
			{
				solrQuery.append(buildQuery("(MATCH_STATUS_CODE ", MatchFiltersQuery.getValue().getMatchFilters()));
				solrQuery.append(" ) ");

			}

			if(MatchFiltersQuery.getValue().getMatchFilters().size() > 0 && MatchFiltersQuery.getValue().getNotMatchFilters().size() > 0)
			{
				solrQuery.append(" OR ");
			}

			if(MatchFiltersQuery.getValue().getNotMatchFilters().size() > 0)
			{
				solrQuery.append(buildQuery("(*:* -MATCH_STATUS_CODE ", MatchFiltersQuery.getValue().getNotMatchFilters()));
				solrQuery.append(" ) ");

			}

			if(MatchFiltersQuery.getValue().getMatchFilters().size() > 0 || MatchFiltersQuery.getValue().getNotMatchFilters().size() > 0)
			{
				solrQuery.append(" ) ");
			}
		}

		return solrQuery;
	}

	private Map<String, Map<String, Set<String>>> createEmptyBomFacet(Map<String, Map<String, Set<String>>> bomFacets)
	{

		Map<String, Map<String, Set<String>>> tempBomFacet = new LinkedHashMap<>();

		for(Map.Entry<String, Map<String, Set<String>>> tempEntry : bomFacets.entrySet())
		{
			tempBomFacet.put(tempEntry.getKey(), new LinkedHashMap<>());
		}
		return tempBomFacet;
	}

	private void getFilteredBomMatchFacets(Map<String, List<String>> matchFiltersRequest, Map<String, MatchFiltersRequestDTO> matchFilters)
	{
		for(Map.Entry<String, Map<String, String>> facetFieldsMappingEntry : facetFieldsMapping.entrySet())
		{

			String facetFieldsMappingKey = facetFieldsMappingEntry.getKey();
			matchFilters.put(facetFieldsMappingKey, new MatchFiltersRequestDTO());

			if(matchFiltersRequest.get(facetFieldsMappingKey) != null)
			{
				for(Map.Entry<String, String> matchFacet : facetFieldsMappingEntry.getValue().entrySet())
				{

					for(String matchFilter : matchFiltersRequest.get(facetFieldsMappingKey))
					{
						if(matchFilter.equals(matchFacet.getValue()))
						{
							matchFilters.get(facetFieldsMappingKey).getMatchFilters().add(matchFacet.getKey());

						}
						else if(matchFilter.equals(facetNameDefault))
						{
							matchFilters.get(facetFieldsMappingKey).getNotMatchFilters().add(matchFacet.getKey());
						}
					}
				}
			}
		}
	}

	private void getFilteredBomFacets(Map<String, Map<String, Set<String>>> bomFacets, Map<String, Map<String, Set<String>>> filteredBomFacets,
			Map<String, List<String>> filtersRequest, Set<String> comIds, BOMMesssage message, Map<String, Set<String>> orderedFacet, boolean isExact,
			boolean isNoMatch)
	{
		boolean firstFilter = true;
		for(Map.Entry<String, List<String>> requestEntry : filtersRequest.entrySet())
		{
			Set<String> filterComIds = new HashSet<>();
			for(int i = 0; i < requestEntry.getValue().size(); i++)
			{
				if(requestEntry.getValue().get(i).equals(N_A))
				{
					requestEntry.getValue().set(i, N_A_E);
					requestEntry.getValue().add(N_A_Z);
				}
				if(bomFacets.get(requestEntry.getKey()) != null && bomFacets.get(requestEntry.getKey()).get(requestEntry.getValue().get(i)) != null)
				{
					filterComIds.addAll(bomFacets.get(requestEntry.getKey()).get(requestEntry.getValue().get(i)));
				}
			}
			if(firstFilter)
			{
				comIds.addAll(filterComIds);
				firstFilter = !firstFilter;
			}
			else
			{
				comIds.retainAll(filterComIds);
			}
		}

		for(Map.Entry<String, Map<String, Set<String>>> bomFacetEntry : bomFacets.entrySet())
		{
			filteredBomFacets.put(bomFacetEntry.getKey(), new LinkedHashMap<>());
			for(Map.Entry<String, Set<String>> facet : bomFacetEntry.getValue().entrySet())
			{
				Set<String> intersection = new HashSet<>(comIds);
				intersection.retainAll(facet.getValue());
				if(intersection.size() > 0)
				{
					filteredBomFacets.get(bomFacetEntry.getKey()).put(facet.getKey(), intersection);
					if(orderedFacet != null && StringUtils.equals(message.getSortField(), bomFacetEntry.getKey()))
					{

						if((isExact == isNoMatch) || (isExact && !isNoMatch && !(StringUtils.equals(facet.getKey(), N_A_Z))) || (isNoMatch && !isExact
								&& (StringUtils.equals(facet.getKey(), N_A_E) || (StringUtils.equals(facet.getKey(), N_A_Z)))))
						{
							orderedFacet.put(facet.getKey(), intersection);
						}
					}
				}
			}
		}
	}

	private void completeBOMRowsWithSEData(Map<String, List<BOMRow>> comIdsToParts, List<PartSearchResult> seResult)
	{
		for(PartSearchResult partSearchResult : seResult)
		{
			List<PartSearchDTO> result = partSearchResult.getPartResult();
			if(result != null && !result.isEmpty())
			{
				PartSearchDTO sePart = result.get(0);
				String comId = sePart.getComID();
				fillBOMRowswithSEData(sePart, comIdsToParts.get(comId));

			}
		}

	}

	private void fillBOMRowswithSEData(PartSearchDTO sePart, List<BOMRow> list)
	{
		for(BOMRow bomRow : list)
		{
			bomRow.setLifecycle(sePart.getLifecycle());
			bomRow.setRohs(sePart.getRohs());
			bomRow.setRohsVersion(sePart.getRohsVersion());
			bomRow.setDescription(sePart.getDescription());
		}

	}

	private Map<String, List<BOMRow>> createComIdsTopartsMap(List<BOMRow> partsList)
	{
		Map<String, List<BOMRow>> comIdsToParts = new LinkedHashMap<>();
		for(BOMRow bomRow : partsList)
		{
			String comId = String.valueOf(bomRow.getComID());
			List<BOMRow> addedParts = comIdsToParts.get(comId);
			if(addedParts == null)
			{
				addedParts = new LinkedList<>();
				comIdsToParts.put(comId, addedParts);
			}
			addedParts.add(bomRow);
		}
		return comIdsToParts;
	}

	private BomMatchFacets getBomMatchFacets(List<Count> facetData)
	{

		BomMatchFacets bomMatchFacetsResponse = new BomMatchFacets();
		Map<String, List<KeywordFacet>> bomMatchFacets = new LinkedHashMap<>();
		bomMatchFacets.put(partMatchStatus, new ArrayList<>());
		bomMatchFacets.put(partMatchType, new ArrayList<>());
		bomMatchFacetsResponse.setExactOnly(true);

		Map<String, Long> partMatchStatusData = new LinkedHashMap<>();
		Map<String, Long> partMatchTypeData = new LinkedHashMap<>();

		for(Count facet : facetData)
		{
			String partStatusKey = facetFieldsMapping.get(partMatchStatus).getOrDefault(facet.getName(), facetNameDefault);

			Long matchStatusCount = partMatchStatusData.get(partStatusKey);
			if(matchStatusCount == null)
			{
				matchStatusCount = 0L;
			}

			partMatchStatusData.put(partStatusKey, matchStatusCount + facet.getCount());
			String matchTypeKey = facetFieldsMapping.get(partMatchType).getOrDefault(facet.getName(), facetNameDefault);

			Long matchTypeCount = partMatchTypeData.get(matchTypeKey);
			if(matchTypeCount == null)
			{
				matchTypeCount = 0L;
			}
			partMatchTypeData.put(matchTypeKey, matchTypeCount + facet.getCount());
		}

		for(Map.Entry<String, Long> entry : partMatchStatusData.entrySet())
		{
			if(entry.getKey().equals(facetFieldsMapping.get(partMatchStatus).get("1")) && entry.getValue() > 0)
			{
				bomMatchFacetsResponse.setExact(true);
			}
			else
			{
				bomMatchFacetsResponse.setExactOnly(false);
			}
			bomMatchFacets.get(partMatchStatus).add(new KeywordFacet(entry.getKey(), entry.getValue()));
		}

		for(Map.Entry<String, Long> entry : partMatchTypeData.entrySet())
		{
			if(entry.getKey().equals(facetFieldsMapping.get(partMatchType).get("1")) && entry.getValue() > 0)
			{
				bomMatchFacetsResponse.setExact(true);
			}
			else
			{
				bomMatchFacetsResponse.setExactOnly(false);
			}
			bomMatchFacets.get(partMatchType).add(new KeywordFacet(entry.getKey(), entry.getValue()));
		}

		bomMatchFacetsResponse.setBomMatchFacets(bomMatchFacets);
		return bomMatchFacetsResponse;
	}

	private String buildQuery(String fieldName, Set<String> fieldValues)
	{
		StringBuilder query = new StringBuilder();
		if(fieldValues != null && fieldValues.size() > 0)
		{

			query.append(fieldName).append(":(").append(StringUtils.join(fieldValues, " ")).append(")");
		}
		return query.toString();
	}

}
