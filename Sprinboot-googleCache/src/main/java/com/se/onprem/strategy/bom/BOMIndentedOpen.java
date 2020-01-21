package com.se.onprem.strategy.bom;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.onprem.dto.business.bom.BOMMesssage;
import com.se.onprem.dto.business.bom.BOMRow;
import com.se.onprem.dto.ws.PartSearchDTO;
import com.se.onprem.dto.ws.PartSearchResult;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.services.HelperService;
import com.se.onprem.util.ParametricConstants;
import com.se.onprem.util.SolrReader;
import com.se.onprem.util.UaaConstants;

@Service
public class BOMIndentedOpen implements IBOMActions
{

	@Autowired
	private SolrReader<BOMRow> solrReader;
	@Autowired
	private SolrClient bomPartsCore;
	@Autowired
	HelperService helper;

	@Override
	public BOMMesssage doAction(BOMMesssage message)
	{
		SolrQuery query = new SolrQuery(buildQuery(message.getBomId(), message.getLevels(), message.getPaths(), message.getIgnoredPaths()))
				.setRows(message.getPageSize()).setStart((message.getPageNumber() - 1) * message.getPageSize()).addSort("ROW_ID", ORDER.asc);

		QueryResponse response;
		try
		{
			response = bomPartsCore.query(query);
			if(response == null)
			{
				return message;
			}
			List<BOMRow> partsList = solrReader.objectsListFromSolr(response.getResults(), BOMRow.class);
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
			resultMessage.setResultCount(response.getResults().getNumFound());
			return resultMessage;
		}
		catch(SolrServerException | IOException e)
		{

			e.printStackTrace();
		}
		return message;
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
		Map<String, List<BOMRow>> comIdsToParts = new HashMap<>();
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

	private String buildQuery(String bomId, String levels, String paths, String ignoredPaths)
	{

		StringBuilder query = new StringBuilder("BOM_ID:" + bomId + " ");

		if(StringUtils.isNotEmpty(levels) || StringUtils.isNotEmpty(paths))
		{
			query.append("AND ( ");
			String levelQuery = getOrQueryFromList("LEVEL", StringUtils.split(levels, ","));
			query.append(levelQuery);
			String pathQuery = getOrQueryFromList("PATH", StringUtils.split(paths, ","));
			query.append(StringUtils.isNotEmpty(pathQuery) && StringUtils.isNotEmpty(levelQuery) ? " OR " : "");
			query.append(pathQuery);

			query.append(")");

		}

		if(StringUtils.isNotEmpty(ignoredPaths))
		{
			query.append(" AND -PATH : (");
			query.append(ignoredPaths.replaceAll(",", "* OR "));
			query.append("*)");
		}

		return query.toString();
	}

	private String getOrQueryFromList(String fieldName, String[] fieldValues)
	{
		StringBuilder query = new StringBuilder();
		if(fieldValues != null && fieldValues.length > 0)
		{

			query.append(fieldName).append(":(").append(StringUtils.join(fieldValues, " ")).append(")");
		}
		return query.toString();
	}
}
