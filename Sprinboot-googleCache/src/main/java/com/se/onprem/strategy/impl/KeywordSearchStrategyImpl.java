package com.se.onprem.strategy.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.se.onprem.dto.business.ACLQueryResult;
import com.se.onprem.dto.ws.CustomerPart;
import com.se.onprem.dto.ws.DebugStep;
import com.se.onprem.dto.ws.FeatureDTO;
import com.se.onprem.dto.ws.PartSearchDTO;
import com.se.onprem.dto.ws.PartSearchRequest;
import com.se.onprem.dto.ws.PartSearchResponse;
import com.se.onprem.dto.ws.PartSearchResult;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.dto.ws.Status;
import com.se.onprem.messages.OperationMessages;
import com.se.onprem.messages.PartSearchOperationMessages;
import com.se.onprem.messages.PartSearchStatus;
import com.se.onprem.services.HelperService;
import com.se.onprem.services.cache.CacheService;
import com.se.onprem.strategy.KeywordSearchStrategy;
import com.se.onprem.strategy.LoggerStrategy;
import com.se.onprem.util.ConstantSolrFields;
import com.se.onprem.util.JsonHandler;
import com.se.onprem.util.ParametricConstants;
import com.se.onprem.util.UaaConstants;

@Service
public class KeywordSearchStrategyImpl implements KeywordSearchStrategy
{
	@Autowired
	CpnSearchStrategyImpl cpnSearchStrategyImpl;

	@Autowired
	HelperService helperService;

	@Value("${" + ParametricConstants.SE_KEYWORD_AUTOCOMPLETE_API_URL + "}")
	private String sEAutoCompleteUrl;
	JsonHandler<PartSearchResponse> resultWrapperJsonHandler;
	private RestTemplate restTemplate;
	SolrClient aclSolrServer;
	CacheService cache;

	@Autowired
	public KeywordSearchStrategyImpl(RestTemplate restTemplate, SolrClient aclCore, CacheService cache)
	{
		this.restTemplate = restTemplate;
		this.aclSolrServer = aclCore;
		resultWrapperJsonHandler = new JsonHandler<PartSearchResponse>();
		this.cache = cache;
	}

	@Override
	public RestResponseWrapper getkewordSearch(PartSearchRequest keywordSearchRequest, LoggerStrategy databaseLoggerStrategy,
			HelperService helperService)
	{
		RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.SUCCESSFULL_OPERATION, true));
		try
		{

			// get Keyword from backend
			String query = getKeywordQuery(keywordSearchRequest.getPartNumber());
			ACLQueryResult queryResult = cache.getCustomerPartsComidListForQuery(query);
			List<PartSearchDTO> partList = new LinkedList<>();

			Map<String, String> customHeaders = new HashMap<>();
			customHeaders.put(UaaConstants.AUTHORIZATION, keywordSearchRequest.getToken());

			int numberOfACLParts = 0;
			if(queryResult.isEmpty())
			{
				query = helperService.getKeywordQueryString(keywordSearchRequest, customHeaders);
				queryResult = cache.getCustomerPartsComidListForQuery(query);
			}
			List<String> foundParts = queryResult.getFoundComIds();
			if(queryResult.hasPartsWithComids() && StringUtils.isEmpty(keywordSearchRequest.getFilters()))
			{
				List<PartSearchDTO> notFoundParts = queryResult.getPartsWithNoComid();
				numberOfACLParts = notFoundParts.size();
				int page = keywordSearchRequest.getPageNumber();
				int pageSize = keywordSearchRequest.getPageSize();
				int customStart = (page - 1) * pageSize;
				int seStartOffset = customStart > numberOfACLParts ? 0 : numberOfACLParts % pageSize;
				int seStart = customStart - (numberOfACLParts) + seStartOffset;
				int seEnd = (((page - 1) * pageSize) + pageSize) - numberOfACLParts;
				if(seStart < 0)
				{
					seStart = 0;
					seEnd = 0;
				}
				keywordSearchRequest.setStart("" + seStart);
				keywordSearchRequest.setPageSize(seEnd - seStart);
				partList.addAll(notFoundParts.subList(Math.min(customStart, numberOfACLParts), Math.min(pageSize, numberOfACLParts)));
			}

			keywordSearchRequest.setBoostedComIDs(StringUtils.join(foundParts, " "));
			restResponseWrapper = helperService.getKeywordPartsList(keywordSearchRequest, customHeaders);
			Map<String, PartSearchDTO> finalParts = new HashMap<>();
			if(restResponseWrapper.getKeywordResults() != null)
			{
				restResponseWrapper.getKeywordResults().forEach(actionResult -> {
					actionResult.getPartResult().forEach(part -> {
						finalParts.put(part.getComID(), part);
					});
				});
			}

			if(finalParts.isEmpty())
			{
				restResponseWrapper.setStatus(new Status(OperationMessages.NO_RESULT_FOUND, false));
				return restResponseWrapper;
			}
			SolrQuery query1 = helperService.formateCPNQuery(finalParts.keySet());
			QueryResponse response = helperService.executeSorlQuery(query1, aclSolrServer);

			if(!response.getResults().isEmpty())
			{
				SolrDocumentList documents = response.getResults();
				helperService.fillCustomerData(documents, finalParts);
			}
			if(restResponseWrapper.getKeywordResults() != null && !restResponseWrapper.getKeywordResults().isEmpty())
			{

				partList.addAll(restResponseWrapper.getKeywordResults().get(0).getPartResult());

			}
			restResponseWrapper.getKeywordResults().get(0).setPartResult(partList);
			restResponseWrapper.setTotalItems(restResponseWrapper.getTotalItems() + numberOfACLParts);
			return restResponseWrapper;

		}
		catch(SolrServerException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return restResponseWrapper;
	}

	private String getKeywordQuery(String partNumber)
	{
		StringBuilder sb = new StringBuilder(512);
		sb.append(ConstantSolrFields.NAN_PARTNUM_EXACT).append(":").append(StringUtils.removeAll(partNumber, "[^a-zA-Z0-9\\=\\.#+]")).append("* OR ")
				.append(ConstantSolrFields.CPN).append(":").append(partNumber).append("*");
		return sb.toString();
	}

	@Override
	public RestResponseWrapper getAutoCompleteResults(PartSearchRequest keywordRequest, LoggerStrategy databaseLoggerStrategy,
			HelperService helperService)
	{
		RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.SUCCESSFULL_OPERATION, true));

		Map<String, String> queryParams = new HashMap<>();

		Map<String, String> customHeaders = new HashMap<>();
		customHeaders.put(UaaConstants.AUTHORIZATION, keywordRequest.getToken());
		try
		{
			queryParams.put("keyword", keywordRequest.getPartNumber());
			queryParams.put("debug", StringUtils.defaultString(keywordRequest.getDebugMode(), "false"));
			restResponseWrapper = helperService.getResposneFromURL(queryParams, sEAutoCompleteUrl, customHeaders);
		}
		catch(UnsupportedEncodingException e)
		{
			restResponseWrapper.setStatus(new Status(OperationMessages.FAILED_OPERATION, false));
		}

		return restResponseWrapper;
	}

}
