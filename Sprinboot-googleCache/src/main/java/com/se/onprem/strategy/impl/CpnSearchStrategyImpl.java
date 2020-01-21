package com.se.onprem.strategy.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.se.onprem.dto.ws.CustomerPart;
import com.se.onprem.dto.ws.FeatureDTO;
import com.se.onprem.dto.ws.PartResult;
import com.se.onprem.dto.ws.PartSearchDTO;
import com.se.onprem.dto.ws.PartSearchRequest;
import com.se.onprem.dto.ws.PartSearchResponse;
import com.se.onprem.dto.ws.PartSearchResult;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.messages.PartSearchOperationMessages;
import com.se.onprem.messages.PartSearchStatus;
import com.se.onprem.services.HelperService;
import com.se.onprem.strategy.CpnSearchStrategy;
import com.se.onprem.strategy.LoggerStrategy;
import com.se.onprem.util.JsonHandler;
import com.se.onprem.util.UaaConstants;

@Service
public class CpnSearchStrategyImpl implements CpnSearchStrategy
{

	SolrClient aclSolrServer;
	JsonHandler<FeatureDTO> featureJsonHandler;
	JsonHandler<PartResult> partsJsonHandler;
	JsonHandler<PartSearchResponse> resultWrapperJsonHandler;
	JsonHandler<CustomerPart> partJsonHandler;
	HelperService helperService;

	@Autowired
	public CpnSearchStrategyImpl(HelperService helperService, RestTemplate restTemplate, SolrClient aclCore)
	{
		this.helperService = helperService;
		this.aclSolrServer = aclCore;
		featureJsonHandler = new JsonHandler<>();
		partsJsonHandler = new JsonHandler<>();
		resultWrapperJsonHandler = new JsonHandler<PartSearchResponse>();
		partJsonHandler = new JsonHandler<CustomerPart>();
	}

	@Override
	public PartSearchResponse getCpnSearch(PartSearchRequest cpnSearchRequest, LoggerStrategy databaseLoggerStrategy, HelperService helperService)
	{
		PartSearchResponse restResponseWrapper = new PartSearchResponse(
				new PartSearchStatus(PartSearchOperationMessages.SUCCESSFULL_OPERATION, true));
		try
		{
			List<CustomerPart> RequestedCpnList = partJsonHandler.convertJSONToList(cpnSearchRequest.getPartNumber(), CustomerPart.class);
			ArrayList<String> pcnList = new ArrayList<>();
			for(CustomerPart part : RequestedCpnList)
			{
				pcnList.add(part.getCpn());
			}
			QueryResponse response = getCpnQueryResponse(pcnList);
			if(!response.getResults().isEmpty())
			{
				SolrDocumentList documents = response.getResults();
				List<PartSearchResult> resultListForNotFoundComIds = new ArrayList<>();
				Map<String, PartSearchResult> resultMap = fillAclData(documents, resultListForNotFoundComIds);
				getSePartData(resultMap, cpnSearchRequest.getToken());
				List<PartSearchResult> finalResult = resultListForNotFoundComIds;
				List<PartSearchResult> foundList = new ArrayList<>(resultMap.values());
				finalResult.addAll(foundList);
				restResponseWrapper.setPartList(finalResult);
				return restResponseWrapper;
			}
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

	public QueryResponse getCpnQueryResponse(ArrayList<String> pcnList) throws SolrServerException, IOException
	{
		SolrQuery query = formateCPNQuery(pcnList);
		return helperService.executeSorlQuery(query, aclSolrServer);
	}

	public void getSePartData(Map<String, PartSearchResult> resultMap, String token) throws UnsupportedEncodingException
	{
		if(resultMap == null || resultMap.isEmpty())
		{
			return;
		}
		Set<String> comIds = resultMap.keySet();

		Map<String, String> customHeaders = new HashMap<>();
		customHeaders.put(UaaConstants.AUTHORIZATION, token);

		RestResponseWrapper restResponseWrapper = helperService.getComidsRequestResult(comIds, customHeaders);
		if(restResponseWrapper == null || restResponseWrapper.getKeywordResults() == null)
		{
			return;
		}
		for(PartSearchResult partResult : restResponseWrapper.getKeywordResults())
		{
			for(PartSearchDTO partDto : partResult.getPartResult())
			{
				PartSearchResult resultList = resultMap.get(partDto.getComID());
				PartSearchDTO customerDto = resultList.getPartResult().get(0);
				partDto.setCustomerDataDTOList(customerDto.getCustomerDataDTOList());
				resultList.getPartResult().set(0, partDto);
			}
		}
	}

	public Map<String, PartSearchResult> fillAclData(SolrDocumentList documents, List<PartSearchResult> resultListForNotFoundComIds)
	{
		Map<String, PartSearchResult> searchResultMap = new HashMap<String, PartSearchResult>();
		if(documents == null || documents.isEmpty())
		{
			return searchResultMap;
		}
		for(SolrDocument doc : documents)
		{
			if(doc.getFieldValue("COM_ID") != null && !doc.getFieldValue("COM_ID").toString().isEmpty())
			{
				PartSearchResult partSearchResult = new PartSearchResult();
				String comId = doc.getFieldValue("COM_ID").toString();
				CustomerPart customerDataDTO = new CustomerPart();
				customerDataDTO.setPartID((Long) doc.getFieldValue("PART_ID"));
				customerDataDTO.setCpn((String) doc.getFieldValue("CPN"));
				customerDataDTO.setMan((String) doc.getFieldValue("MAN_NAME"));
				customerDataDTO.setMpn((String) doc.getFieldValue("MPN"));
				String features = (String) doc.getFieldValue("CUSTOM_FEATURES");
				customerDataDTO.setCustomFeatures(featureJsonHandler.convertJSONToList(features, FeatureDTO.class));
				PartSearchDTO partResult = new PartSearchDTO();
				partResult.addCustomerPart(customerDataDTO);

				List<PartSearchDTO> partList = new ArrayList<PartSearchDTO>();
				partList.add(partResult);
				partSearchResult.setPartResult(partList);

				searchResultMap.put(comId, partSearchResult);
			}
			else
			{
				PartSearchResult partSearchResult = new PartSearchResult();
				CustomerPart customerDataDTO = new CustomerPart();
				customerDataDTO.setPartID((Long) doc.getFieldValue("PART_ID"));
				customerDataDTO.setCpn((String) doc.getFieldValue("CPN"));
				customerDataDTO.setMan((String) doc.getFieldValue("MAN_NAME"));
				customerDataDTO.setMpn((String) doc.getFieldValue("MPN"));
				String features = (String) doc.getFieldValue("CUSTOM_FEATURES");
				customerDataDTO.setCustomFeatures(featureJsonHandler.convertJSONToList(features, FeatureDTO.class));
				partSearchResult.setRequestedCpn(customerDataDTO.getCpn());
				PartSearchDTO partResult = new PartSearchDTO();
				partResult.addCustomerPart(customerDataDTO);
				List<PartSearchDTO> partList = new ArrayList<PartSearchDTO>();
				partList.add(partResult);
				partSearchResult.setPartResult(partList);

				resultListForNotFoundComIds.add(partSearchResult);
			}
		}
		// partResult.setCustomerDataDTO(customerDataDTOs);
		return searchResultMap;

	}

	private SolrQuery formateCPNQuery(ArrayList<String> pcnList)
	{
		SolrQuery query = new SolrQuery();
		StringBuilder q = new StringBuilder();
		q.append("CPN:(");
		String delimeter = "";
		for(String comID : pcnList)
		{
			q.append(delimeter);
			q.append(comID);
			delimeter = " OR ";
		}
		q.append(")");
		query.set("q", q.toString());
		query.setRows(pcnList.size());
		return query;
	}

}
