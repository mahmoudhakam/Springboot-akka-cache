package com.se.onprem.services;

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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.se.onprem.dto.ws.CustomerPart;
import com.se.onprem.dto.ws.DebugStep;
import com.se.onprem.dto.ws.FeatureDTO;
import com.se.onprem.dto.ws.PartResult;
import com.se.onprem.dto.ws.PartSearchDTO;
import com.se.onprem.dto.ws.PartSearchRequest;
import com.se.onprem.dto.ws.PartSearchResult;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.dto.ws.Status;
import com.se.onprem.messages.OperationMessages;
import com.se.onprem.services.cache.CacheService;
import com.se.onprem.strategy.LoggerStrategy;
import com.se.onprem.util.ConstantSolrFields;
import com.se.onprem.util.JsonHandler;
import com.se.onprem.util.ParametricConstants;

@Service
public class HelperService
{
	JsonHandler<FeatureDTO> featureJsonHandler;
	private static final int MAX_OR_QUERY_ELEMENTS = 1000;
	@Value("${" + ParametricConstants.SE_PART_SEARCH_API_URL + "}")
	private String sEPartSearchAPIUrl;

	@Value("${" + ParametricConstants.SE_KEYWORD_SEARCH_API_URL + "}")
	private String sEKeywordSearchAPIUrl;
	@Value("${" + ParametricConstants.SE_KEYWORD_QUERY_API_URL + "}")
	private String sEKeywordQueryAPIUrl;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	JsonHandler<RestResponseWrapper> resultWrapperJsonHandler;
	private RestTemplate restTemplate;
	JsonHandler<PartResult> partsJsonHandler;

	@Autowired
	public HelperService(RestTemplate restTemplate)
	{
		// will be injected into constructor
		featureJsonHandler = new JsonHandler<>();
		resultWrapperJsonHandler = new JsonHandler<>();
		partsJsonHandler = new JsonHandler<>();
		this.restTemplate = restTemplate;
	}

	public String getFullUrl(HttpServletRequest request)
	{
		int REQ_LEN = 4000;
		return stringTruncate(request.getRequestURL().toString() + "?" + request.getQueryString(), REQ_LEN);
	}

	private String stringTruncate(String str, int len)
	{
		int limit = str.length();
		if(len < limit)
		{
			limit = len;
			return str.substring(0, limit) + "...";
		}
		return str;
	}

	public String getSolrIsCustomPartQuery()
	{
		return " -" + ConstantSolrFields.IS_CUSTOM_PART + ":1";
	}

	public QueryResponse executeSorlQuery(SolrQuery query, SolrClient solrCore) throws SolrServerException, IOException
	{
		return solrCore.query(query, METHOD.POST);
	}

	public int getSolrDocumentsCount(QueryResponse response)
	{
		return (int) response.getResults().getNumFound();
	}

	public int calculateSolrStartingPage(Integer pageNumber, Integer pageSize)
	{
		return ((pageNumber - 1) * pageSize);
	}

	public String getRemoteAddress(HttpServletRequest request)
	{
		return request.getRemoteAddr();
	}

	public RestResponseWrapper handleInternalErrors(String operationName, Exception e, String timeTaken)
	{
		RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));

		return restResponseWrapper;
	}

	public String stringOrEmpty(Object obj)
	{
		return (obj == null) ? "" : obj.toString();
	}

	public ResponseEntity<String> callRestEndpoint(HttpMethod httpMethod, URI endPoint, HttpEntity<?> entity, RestTemplate restTemplate)
			throws HttpClientErrorException, HttpStatusCodeException
	{
		ResponseEntity<String> responseEntity;
		long start = System.currentTimeMillis();
		try
		{
			responseEntity = restTemplate.exchange(endPoint, httpMethod, entity, String.class);
			long elapsedTime = System.currentTimeMillis() - start;
			logger.info("Finish calling:{} takes about:{} ms", endPoint, elapsedTime);
			// if(responseEntity.getStatusCodeValue() == 200 && responseEntity.getBody() != null)
			// {
			//
			// return responseEntity;
			// }
		}
		catch(HttpClientErrorException httpClientErrorException)
		{
			responseEntity = new ResponseEntity<>(httpClientErrorException.getStatusCode());
		}

		return responseEntity;
	}

	public URI getPartSearchEndpoint(Map<String, String> queryParams) throws UnsupportedEncodingException
	{
		String url = sEPartSearchAPIUrl;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		if(queryParams != null && !queryParams.isEmpty())
		{
			queryParams.entrySet().forEach(e -> {
				builder.queryParam(e.getKey(), e.getValue());
			});

		}
		URI uri = builder.build().encode().toUri();
		return uri;
	}

	public URI getEndpointURL(Map<String, String> queryParams, String url) throws UnsupportedEncodingException
	{

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		if(queryParams != null && !queryParams.isEmpty())
		{
			queryParams.entrySet().forEach(e -> {
				builder.queryParam(e.getKey(), e.getValue());
			});

		}
		// URI uri = builder.build().encode().toUri();
		URI uri = builder.build().toUri();
		return uri;
	}

	public HttpEntity<LinkedMultiValueMap<String, Object>> getEntityWithCookies(Map<String, String> customHeaders)

	{
		LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		return getEntityWithCookies(customHeaders, params);
	}

	public HttpEntity<LinkedMultiValueMap<String, Object>> getEntityWithCookies(Map<String, String> customHeaders,
			LinkedMultiValueMap<String, Object> body)

	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("User-Agent", "mozilla");

		if(customHeaders != null)
		{
			for(Map.Entry<String, String> customHeadersEntry : customHeaders.entrySet())
			{
				headers.set(customHeadersEntry.getKey(), customHeadersEntry.getValue());
			}
		}

		if(body == null)
		{
			body = new LinkedMultiValueMap<>();
		}

		HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
		return entity;
	}

	public HttpEntity<Object> getEntityWithCookies(Map<String, String> customHeaders, Object body)

	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("User-Agent", "mozilla");

		if(customHeaders != null)
		{
			for(Map.Entry<String, String> customHeadersEntry : customHeaders.entrySet())
			{
				headers.set(customHeadersEntry.getKey(), customHeadersEntry.getValue());
			}
		}

		if(body == null)
		{
			body = new Object();
		}

		HttpEntity<Object> entity = new HttpEntity<>(body, headers);
		return entity;
	}

	public void fillCustomerData(SolrDocumentList documents, Map<String, PartSearchDTO> finalParts)
	{
		for(SolrDocument d : documents)
		{
			Long comId = (Long) d.getFieldValue("COM_ID");
			if(comId != null)
			{
				PartSearchDTO part = finalParts.get(comId.toString());
				if(part != null)
				{
					CustomerPart customerDataDTO = new CustomerPart();
					customerDataDTO.setCpn((String) d.getFieldValue("CPN"));
					customerDataDTO.setMan((String) d.getFieldValue("MAN_NAME"));
					customerDataDTO.setMpn((String) d.getFieldValue("MPN"));
					if(d.getFieldValue("PART_ID") != null)
						customerDataDTO.setPartID((long) d.getFieldValue("PART_ID"));
					String features = (String) d.getFieldValue("CUSTOM_FEATURES");
					customerDataDTO.setCustomFeatures(featureJsonHandler.convertJSONToList(features, FeatureDTO.class));

					part.addCustomerPart(customerDataDTO);
				}
			}
		}
	}

	public SolrQuery formateCPNQuery(Set<String> keySet)
	{
		SolrQuery query = new SolrQuery();
		StringBuilder q = new StringBuilder();
		q.append("COM_ID:(");
		String delimeter = "";
		for(String comID : keySet)
		{
			q.append(delimeter);
			q.append(comID);
			delimeter = " OR ";
		}
		q.append(")");
		query.set("q", q.toString());
		query.setRows(200);
		return query;
	}

	public RestResponseWrapper getPartSearchWithACLPartsFirst(PartSearchRequest request, LoggerStrategy logger2, List<DebugStep> steps, String query,
			CacheService cache, SolrClient aclSolrServer, Map<String, String> customHeaders) throws SolrServerException, IOException
	{

		int numberOfACLParts = cache.getCustomerPartsCountForQuery(query);
		SolrQuery solrQuery = new SolrQuery(query);
		RestResponseWrapper wrapper = new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));
		final List<PartSearchDTO> partResult = new LinkedList<>();
		if(numberOfACLParts > 0)
		{
			int page = request.getPageNumber();
			int pageSize = request.getPageSize();
			int customStart = (page - 1) * pageSize;
			int seStartOffset = customStart > numberOfACLParts ? 0 : numberOfACLParts % pageSize;
			int seStart = customStart - (numberOfACLParts) + seStartOffset;
			int seEnd = (((page - 1) * pageSize) + pageSize) - numberOfACLParts;
			// case 1, custom parts found
			if(customStart < numberOfACLParts)
			{
				solrQuery.setStart(customStart);
				solrQuery.setRows(pageSize);
				QueryResponse result = executeSorlQuery(solrQuery, aclSolrServer);
				Map<String, List<CustomerPart>> customPartsMap = transformCustomerSolDocs(result.getResults());
				wrapper = getComidsRequestResult(customPartsMap.keySet(), customHeaders);

				wrapper.getKeywordResults().stream().forEach(part -> {
					part.getPartResult().forEach(e -> {
						List<CustomerPart> customParts = customPartsMap.get(e.getComID());
						e.setCustomerDataDTOList(customParts);
						partResult.add(e);

					});
				});

			}
			if(seStart >= 0)
			{
				request.setStart("" + seStart);
				request.setPageSize(seEnd - seStart);
				request.setExcludedComIds(StringUtils.join(cache.getCustomerPartsComidListForQuery(query), " "));
				RestResponseWrapper seWrapper = getKeywordPartsList(request, customHeaders);
				List<PartSearchResult> keywordResults = seWrapper.getKeywordResults();
				if(seWrapper != null && keywordResults != null && !keywordResults.isEmpty())
				{
					partResult.addAll(keywordResults.get(0).getPartResult());
				}

			}
			RestResponseWrapper resultWrapper = new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));
			if(!partResult.isEmpty())
			{
				resultWrapper.setStatus(new Status(OperationMessages.SUCCESSFULL_OPERATION, true));
				List<PartSearchResult> keywordResults = new LinkedList<>();
				PartSearchResult part = new PartSearchResult();
				part.setPartResult(partResult);
				keywordResults.add(part);
				resultWrapper.setKeywordResults(keywordResults);
			}
			return resultWrapper;
		}
		else
		{
			return null;
		}

	}

	private String handleComIdsParam(Set<String> comIds)
	{
		List<PartResult> searchParts = new ArrayList<PartResult>();
		for(String comId : comIds)
		{
			PartResult part = new PartResult();
			part.setComID(comId);
			searchParts.add(part);
		}
		return partsJsonHandler.convertListToJon(searchParts);
	}

	public URI preparePartSearchRequestWithComId(Set<String> comIds) throws UnsupportedEncodingException
	{
		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("partNumber", handleComIdsParam(comIds));
		URI endPoint = getPartSearchEndpoint(queryParams);
		return endPoint;
	}

	public RestResponseWrapper getComidsRequestResult(Set<String> comIds, Map<String, String> customHeaders) throws UnsupportedEncodingException
	{
		URI endPoint = preparePartSearchRequestWithComId(comIds);
		ResponseEntity<String> partSearchResponse = callRestEndpoint(HttpMethod.GET, endPoint, getEntityWithCookies(customHeaders), restTemplate);
		String responseBody = partSearchResponse.getBody();
		RestResponseWrapper restResponseWrapper = resultWrapperJsonHandler.convertJSONToObjectV2(responseBody, RestResponseWrapper.class);
		return restResponseWrapper;
	}

	public URI prepareKeywordSearchRequest(PartSearchRequest request) throws UnsupportedEncodingException
	{
		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("keyword", request.getPartNumber());

		queryParams.put("boostResults", String.valueOf(request.isBoostResults()));
		queryParams.put("rerankComIDs", request.getBoostedComIDs());
		queryParams.put("filters", request.getFilters());
		queryParams.put("order", request.getOrder());
		queryParams.put("exact", String.valueOf(request.isExact()));
		queryParams.put("autocompleteSection", request.getAutocompleteSection());
		queryParams.put("pageSize", "" + request.getPageSize());
		queryParams.put("pageNumber", "" + request.getPageNumber());

		URI endPoint = getEndpointURL(queryParams, sEKeywordSearchAPIUrl);
		return endPoint;
	}

	public RestResponseWrapper getKeywordPartsList(PartSearchRequest keywordSearchRequest, Map<String, String> customHeaders)
			throws UnsupportedEncodingException
	{
		URI endPoint = prepareKeywordSearchRequest(keywordSearchRequest);
		ResponseEntity<String> partSearchResponse = callRestEndpoint(HttpMethod.GET, endPoint, getEntityWithCookies(customHeaders), restTemplate);
		String responseBody = partSearchResponse.getBody();
		RestResponseWrapper restResponseWrapper = resultWrapperJsonHandler.convertJSONToObjectV2(responseBody, RestResponseWrapper.class);
		return restResponseWrapper;
	}

	public RestResponseWrapper getResposneFromURL(Map<String, String> queryParams, String url, Map<String, String> customHeaders)
			throws UnsupportedEncodingException
	{
		URI endPoint = getEndpointURL(queryParams, url);
		ResponseEntity<String> partSearchResponse = callRestEndpoint(HttpMethod.POST, endPoint, getEntityWithCookies(customHeaders), restTemplate);
		String responseBody = partSearchResponse.getBody();
		RestResponseWrapper restResponseWrapper = resultWrapperJsonHandler.convertJSONToObjectV2(responseBody, RestResponseWrapper.class);
		return restResponseWrapper;
	}

	public RestResponseWrapper getResposneFromURL(Map<String, String> queryParams, LinkedMultiValueMap<String, Object> body, String url,
			Map<String, String> headers, String method) throws UnsupportedEncodingException
	{

		URI endPoint = getEndpointURL(queryParams, url);
		ResponseEntity<String> partSearchResponse = callRestEndpoint(HttpMethod.valueOf(method), endPoint, getEntityWithCookies(headers, body),
				restTemplate);
		String responseBody = partSearchResponse.getBody();
		if(responseBody != null)
		{
			try
			{
				RestResponseWrapper restResponseWrapper = resultWrapperJsonHandler.convertJSONToObjectV2(responseBody, RestResponseWrapper.class);
				return restResponseWrapper;

			}
			catch(Exception e)
			{
				logger.error("Error during Gateway", e);
			}
		}

		return new RestResponseWrapper(partSearchResponse.getStatusCode());
	}

	public RestResponseWrapper getResposneFromURL(Map<String, String> queryParams, Object body, String url, Map<String, String> headers,
			String method) throws UnsupportedEncodingException
	{

		URI endPoint = getEndpointURL(queryParams, url);
		ResponseEntity<String> partSearchResponse = callRestEndpoint(HttpMethod.valueOf(method), endPoint, getEntityWithCookies(headers, body),
				restTemplate);
		String responseBody = partSearchResponse.getBody();
		if(responseBody != null)
		{
			try
			{
				RestResponseWrapper restResponseWrapper = resultWrapperJsonHandler.convertJSONToObjectV2(responseBody, RestResponseWrapper.class);
				return restResponseWrapper;

			}
			catch(Exception e)
			{
				logger.error("Error during Gateway", e);
			}
		}

		return new RestResponseWrapper(partSearchResponse.getStatusCode());
	}

	public RestResponseWrapper getResposneFromURL(LinkedMultiValueMap<String, Object> params, HttpHeaders headers, String url)
			throws UnsupportedEncodingException
	{
		URI endPoint = getEndpointURL(null, url);
		// ResponseEntity<String> partSearchResponse = callRestEndpoint(HttpMethod.POST, endPoint, getEntityWithCookies(params), restTemplate);
		HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);
		ResponseEntity<String> partSearchResponse = restTemplate.postForEntity(endPoint, entity, String.class);
		String responseBody = partSearchResponse.getBody();
		RestResponseWrapper restResponseWrapper = resultWrapperJsonHandler.convertJSONToObjectV2(responseBody, RestResponseWrapper.class);
		return restResponseWrapper;
	}

	public String getKeywordQueryString(PartSearchRequest keywordSearchRequest, Map<String, String> customHeaders) throws UnsupportedEncodingException
	{
		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("keyword", keywordSearchRequest.getPartNumber());
		URI endPoint = getEndpointURL(queryParams, sEKeywordQueryAPIUrl);
		ResponseEntity<String> partSearchResponse = callRestEndpoint(HttpMethod.GET, endPoint, getEntityWithCookies(customHeaders), restTemplate);
		String responseBody = partSearchResponse.getBody();
		return responseBody;
	}

	private Map<String, List<CustomerPart>> transformCustomerSolDocs(SolrDocumentList results)
	{
		Map<String, List<CustomerPart>> partsMap = new LinkedHashMap<>();
		if(results != null)
		{
			results.stream().forEach(d -> {
				Object comId = d.getFieldValue("COM_ID");
				if(comId != null)
				{
					List<CustomerPart> parts = partsMap.get(comId.toString());
					if(parts == null)
					{
						parts = new LinkedList<>();
					}

					CustomerPart customerDataDTO = new CustomerPart();
					customerDataDTO.setCpn((String) d.getFieldValue("CPN"));
					customerDataDTO.setMan((String) d.getFieldValue("MAN_NAME"));
					customerDataDTO.setMpn((String) d.getFieldValue("MPN"));
					if(d.getFieldValue("PART_ID") != null)
						customerDataDTO.setPartID((long) d.getFieldValue("PART_ID"));
					String features = (String) d.getFieldValue("CUSTOM_FEATURES");
					// customerDataDTO.setCustomFeatures(featureJsonHandler.convertJSONToList(features, FeatureDTO.class));

					parts.add(customerDataDTO);
					partsMap.put(comId.toString(), parts);

				}
			});
		}
		return partsMap;
	}
}
