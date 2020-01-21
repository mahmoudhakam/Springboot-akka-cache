package com.se.onprem.strategy.bom;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.onprem.dto.business.bom.BOMDto;
import com.se.onprem.dto.business.bom.BOMMesssage;
import com.se.onprem.dto.business.bom.BOMRow;
import com.se.onprem.dto.ws.MatchFiltersRequestDTO;
import com.se.onprem.util.SolrReader;

@Service
public class BOMExport implements IBOMActions
{

	@Autowired
	private SolrReader<BOMRow> partsSolrReader;

	@Autowired
	private SolrReader<BOMDto> bomSolrReader;

	@Autowired
	private SolrClient bomPartsCore;

	@Autowired
	private SolrClient bomCore;

	private Map<String, Set<String>> matchTypeMapping;

	private final String facetNameDefault = "No Match";

	@PostConstruct
	public void init()
	{
		matchTypeMapping = new HashMap<>();

		matchTypeMapping.put("Exact", new HashSet<>());
		matchTypeMapping.put("No match similar", new HashSet<>());

		matchTypeMapping.get("Exact").add("1");

		matchTypeMapping.get("No match similar").add("5");
		matchTypeMapping.get("No match similar").add("7");
		matchTypeMapping.get("No match similar").add("8");
		matchTypeMapping.get("No match similar").add("9");
	}

	private Map<String, MatchFiltersRequestDTO> getFilteredBomMatchFacets(List<String> matchFiltersRequest,
			Map<String, MatchFiltersRequestDTO> matchFilters)
	{
		matchFilters = new HashMap<>();
		matchFilters.put(facetNameDefault, new MatchFiltersRequestDTO(new HashSet<>(), false));
		matchFilters.put("NOT " + facetNameDefault, new MatchFiltersRequestDTO(new HashSet<>(), false));

		for(String matchFilter : matchFiltersRequest)
		{
			if(StringUtils.equals(matchFilter, facetNameDefault))
			{

				Set<String> noMatch = new HashSet<>();

				for(Map.Entry<String, Set<String>> matchTypeMappingEntry : matchTypeMapping.entrySet())
				{
					noMatch.addAll(matchTypeMappingEntry.getValue());
				}

				matchFilters.get(facetNameDefault).setNot(true);
				matchFilters.get(facetNameDefault).getMatchFilters().addAll(noMatch);
			}
			else if(matchTypeMapping.get(matchFilter) != null)
			{
				matchFilters.get("NOT " + facetNameDefault).getMatchFilters().addAll(matchTypeMapping.get(matchFilter));
			}
		}
		return matchFilters;
	}

	@Override
	public BOMMesssage doAction(BOMMesssage message)
	{
		SolrQuery partsQuery = new SolrQuery("BOM_ID:" + message.getBomId()).setRows(0);
		SolrQuery bomQuery = new SolrQuery("BOM_ID:" + message.getBomId());

		QueryResponse partsResponse, bomResponse;
		BOMMesssage resultMessage = new BOMMesssage();

		Map<String, MatchFiltersRequestDTO> matchFilters = new HashMap<>();
		try
		{
			bomResponse = bomCore.query(bomQuery);

			if(bomResponse == null)
			{
				return message;
			}

			List<BOMDto> bomList = bomSolrReader.objectsListFromSolr(bomResponse.getResults(), BOMDto.class);
			BOMDto bomDto = new BOMDto();
			if(bomList.size() > 0)
			{
				bomDto = bomList.get(0);
			}

			resultMessage.setBomName(bomDto.getBomName().substring(0, bomDto.getBomName().lastIndexOf('.')));

			List<String> matchFiltersRequest = message.getMatchStatusExportRequest();

			partsResponse = bomPartsCore.query(partsQuery);

			StringBuilder partsQuerySb = new StringBuilder("BOM_ID:" + message.getBomId());

			if(matchFiltersRequest != null && matchFiltersRequest.size() > 0)
			{
				matchFilters = getFilteredBomMatchFacets(matchFiltersRequest, matchFilters);
				partsQuerySb.append(" AND (");

				partsQuerySb.append(buildQuery(" ( " + (matchFilters.get(facetNameDefault).isNot() ? "*:* -" : "") + "MATCH_STATUS_CODE ",
						matchFilters.get(facetNameDefault).getMatchFilters()));

				if(matchFilters.get(facetNameDefault).getMatchFilters() != null && matchFilters.get(facetNameDefault).getMatchFilters().size() > 0)
				{
					partsQuerySb.append(" ) ");

					if(matchFilters.get("NOT " + facetNameDefault).getMatchFilters() != null
							&& matchFilters.get("NOT " + facetNameDefault).getMatchFilters().size() > 0)
					{
						partsQuerySb.append(" OR ");
					}

				}

				partsQuerySb.append(buildQuery((matchFilters.get("NOT " + facetNameDefault).isNot() ? "*:* -" : "") + "MATCH_STATUS_CODE ",
						matchFilters.get("NOT " + facetNameDefault).getMatchFilters()));

				partsQuerySb.append(" )");

			}

			partsQuery = new SolrQuery(partsQuerySb.toString()).setRows((int) partsResponse.getResults().getNumFound())
					.setParam("fl", "COM_ID,ROW_KEY,ROW_ID," + message.getBomExportQuery()).addSort("ROW_ID", ORDER.asc);

			partsResponse = bomPartsCore.query(partsQuery);

			List<BOMRow> partsList = partsSolrReader.objectsListFromSolr(partsResponse.getResults(), BOMRow.class);

			resultMessage.setBomParts(partsList);
			resultMessage.setResultCount(partsResponse.getResults().getNumFound());
			return resultMessage;
		}
		catch(SolrServerException | IOException e)
		{

			e.printStackTrace();
		}
		return message;
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
