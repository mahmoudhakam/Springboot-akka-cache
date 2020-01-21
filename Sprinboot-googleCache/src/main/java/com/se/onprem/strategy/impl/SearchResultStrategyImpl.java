package com.se.onprem.strategy.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.se.onprem.dto.ws.CustomerPart;
import com.se.onprem.dto.ws.DebugStep;
import com.se.onprem.dto.ws.FeatureDTO;
import com.se.onprem.dto.ws.PartInput;
import com.se.onprem.dto.ws.PartResult;
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
import com.se.onprem.strategy.CpnSearchStrategy;
import com.se.onprem.strategy.LoggerStrategy;
import com.se.onprem.strategy.PartSearchStrategy;
import com.se.onprem.util.ConstantSolrFields;
import com.se.onprem.util.JsonHandler;
import com.se.onprem.util.ParametricConstants;
import com.se.onprem.util.PartSearchServiceConstants;
import com.se.onprem.util.UaaConstants;

@Service
public class SearchResultStrategyImpl implements PartSearchStrategy
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	SolrClient aclSolrServer;
	private HelperService helperService;
	private RestTemplate restTemplate;

	@Value("${" + ParametricConstants.SE_PART_SEARCH_API_URL + "}")
	private String SEPartSearchAPIUrl;
	JsonHandler<RestResponseWrapper> jsonHandler;
	JsonHandler<PartResult> partsJsonHandler;
	JsonHandler<PartInput> partsInputJsonHandler;
	CpnSearchStrategyImpl cpnSearcher;
	CacheService cache;

	@Autowired
	public SearchResultStrategyImpl(HelperService helperService, RestTemplate restTemplate, SolrClient aclCore, CacheService cache,
			CpnSearchStrategyImpl cpnSearcher)
	{
		this.helperService = helperService;
		this.restTemplate = restTemplate;
		this.aclSolrServer = aclCore;
		jsonHandler = new JsonHandler<>();
		partsJsonHandler = new JsonHandler<>();
		partsInputJsonHandler = new JsonHandler<>();
		this.cache = cache;
		this.cpnSearcher = cpnSearcher;
	}

	@Override
	public RestResponseWrapper getPartSearch(PartSearchRequest request, LoggerStrategy logger, List<DebugStep> steps)
	{
		Map<String, String> customHeaders = new HashMap<>();
		customHeaders.put(UaaConstants.AUTHORIZATION, request.getToken());

		List<PartInput> parts = partsInputJsonHandler.convertJSONToList(request.getPartNumber(), PartInput.class);
		if(request.isBoostResults() && parts != null && parts.size() == 1)
		{
			try
			{
				RestResponseWrapper wrapper = helperService.getPartSearchWithACLPartsFirst(request, logger, steps,
						getQueryFromPartInput(parts.get(0)), cache, aclSolrServer, customHeaders);
				if(wrapper == null)
				{
					return getPartSearchResultsNonAclBoosted(request, steps, customHeaders);
				}
				return wrapper;
			}
			catch(SolrServerException | IOException e)
			{
				return new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));
			}
		}
		return getPartSearchResultsNonAclBoosted(request, steps, customHeaders);
	}

	private RestResponseWrapper getPartSearchResultsNonAclBoosted(PartSearchRequest request, List<DebugStep> steps, Map<String, String> customHeaders)
	{
		RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));

		long start = System.currentTimeMillis();
		try
		{
			URI endPoint = prepareRequest(request);
			ResponseEntity<String> partSearchResponse = helperService.callRestEndpoint(HttpMethod.GET, endPoint,
					helperService.getEntityWithCookies(customHeaders), restTemplate);

			DebugStep setp = new DebugStep("SE Part Search", endPoint.toURL().toString(), (System.currentTimeMillis() - start));
			steps.add(setp);

			String responseBody = partSearchResponse.getBody();
			restResponseWrapper = jsonHandler.convertJSONToObjectV2(responseBody, RestResponseWrapper.class);
			Map<String, PartSearchDTO> finalParts = new HashMap<>();
			if(restResponseWrapper != null && restResponseWrapper.getStatus().getMessage().equals("Successfull Operation"))
			{

				restResponseWrapper.getKeywordResults().forEach(actionResult -> {
					actionResult.getPartResult().forEach(part -> {
						finalParts.put(part.getComID(), part);
					});
				});

				start = System.currentTimeMillis();
				SolrQuery query = helperService.formateCPNQuery(finalParts.keySet());
				QueryResponse response = helperService.executeSorlQuery(query, aclSolrServer);
				DebugStep setpACL = new DebugStep("ACL Solr", query.toQueryString(), (System.currentTimeMillis() - start));
				steps.add(setpACL);

				if(!response.getResults().isEmpty())
				{
					SolrDocumentList documents = response.getResults();
					helperService.fillCustomerData(documents, finalParts);
				}
			}
			else
			{
				System.out.println("not success");

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return restResponseWrapper;
	}

	private String getQueryFromPartInput(PartInput partInput)
	{
		StringBuilder queryBuilder = new StringBuilder(512);
		queryBuilder.append(ConstantSolrFields.NAN_PARTNUM).append(":").append(partInput.getPartNumber()).append("*");
		if(StringUtils.isNotEmpty(partInput.getMan()))
		{
			queryBuilder.append(" AND ").append(ConstantSolrFields.MAN_NAME_EXACT).append(":")
					.append(ClientUtils.escapeQueryChars(partInput.getMan()));
		}
		return queryBuilder.toString();
	}

	public URI prepareRequest(PartSearchRequest request) throws UnsupportedEncodingException
	{
		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("partNumber", request.getPartNumber());
		queryParams.put("pageNumber", "" + request.getPageNumber());
		queryParams.put("pageSize", "" + request.getPageSize());
		queryParams.put("partNumber", request.getPartNumber());
		queryParams.put("start", request.getStart());
		queryParams.put("excluded_comids", request.getExcludedComIds());
		if(StringUtils.isNotEmpty(request.getWildCardMulti()))
		{
			queryParams.put(PartSearchServiceConstants.Parameters.WILDCARD_MULTI, request.getWildCardMulti());

		}
		if(StringUtils.isNotEmpty(request.getWildcardSingle()))
		{
			queryParams.put(PartSearchServiceConstants.Parameters.WILDCARD_SINGLE, request.getWildcardSingle());

		}
		if(request != null && request.getMode() != null)
		{
			queryParams.put("mode", request.getMode());
		}

		URI endPoint = helperService.getPartSearchEndpoint(queryParams);
		return endPoint;
	}

	public UriComponentsBuilder getURIWithQueryParameters(PartSearchRequest request)
	{
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(SEPartSearchAPIUrl).queryParam("man", request.getManName())
				.queryParam("partNumber", request.getPartNumber()).queryParam("pageNumber", request.getPageNumber())
				.queryParam("pageSize", request.getPageSize());
		return builder;
	}

	@Override
	public List<CustomerPart> completePartsWithMissingComIds(List<CustomerPart> inputParts, String token) throws UnsupportedEncodingException
	{
		Map<String, String> customHeaders = new HashMap<>();
		customHeaders.put(UaaConstants.AUTHORIZATION, token);

		Map<String, CustomerPart> customerPartsmap = new LinkedHashMap<>();
		List<PartResult> searchParts = inputParts.stream().map(p -> {
			PartResult part = new PartResult();
			part.setPartNumber(p.getMpn());
			part.setManufacturer(p.getMan());
			customerPartsmap.put(p.getMpn() + p.getMan(), p);
			return part;
		}).collect(Collectors.toList());

		PartSearchRequest request = new PartSearchRequest();
		request.setPartNumber(partsJsonHandler.convertListToJon(searchParts));
		request.setPageSize(1);
		URI endPoint = prepareRequest(request);
		ResponseEntity<String> partSearchResponse = helperService.callRestEndpoint(HttpMethod.GET, endPoint,
				helperService.getEntityWithCookies(customHeaders), restTemplate);

		String responseBody = partSearchResponse.getBody();
		RestResponseWrapper restResponseWrapper = jsonHandler.convertJSONToObjectV2(responseBody, RestResponseWrapper.class);
		if(restResponseWrapper != null && restResponseWrapper.isSuccessufull())
		{

			restResponseWrapper.getKeywordResults().forEach(actionResult -> {

				CustomerPart customerPart = customerPartsmap.get(actionResult.getRequestedMPN() + actionResult.getRequestedMan());
				List<PartSearchDTO> partResult = actionResult.getPartResult();
				if(customerPart != null && partResult != null && partResult.size() > 0)
				{
					PartSearchDTO partSearchDTO = partResult.get(0);
					customerPart.setComID(partSearchDTO.getComID());
					customerPart.setManId(partSearchDTO.getManufacturerId());
					customerPart.setPlId(partSearchDTO.getPlId());
					customerPart.setNanPart(partSearchDTO.getNanPartNumber());

				}
			});

		}
		return inputParts;
	}
}
