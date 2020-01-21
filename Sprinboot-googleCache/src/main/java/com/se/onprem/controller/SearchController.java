package com.se.onprem.controller;

import java.util.List;

import org.springframework.stereotype.Service;

import com.se.onprem.dto.business.bom.BOMRow;
import com.se.onprem.dto.ws.CustomerPart;
import com.se.onprem.dto.ws.DebugStep;
import com.se.onprem.dto.ws.PartSearchRequest;
import com.se.onprem.dto.ws.PartSearchResponse;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.dto.ws.Status;
import com.se.onprem.services.HelperService;
import com.se.onprem.strategy.AddPartsStrategy;
import com.se.onprem.strategy.CpnSearchStrategy;
import com.se.onprem.strategy.KeywordSearchStrategy;
import com.se.onprem.strategy.LoggerStrategy;
import com.se.onprem.strategy.PartSearchStrategy;
import com.se.onprem.util.JsonHandler;

@Service
public class SearchController
{

	JsonHandler<CustomerPart> jsonConverter = new JsonHandler<>();
	JsonHandler<BOMRow> bomConverter = new JsonHandler<>();

	public RestResponseWrapper getPartSearchResult(PartSearchRequest partSearchRequest, PartSearchStrategy searchResultStrategy,
			LoggerStrategy databaseLoggerStrategy, HelperService helperService, List<DebugStep> steps) throws Exception
	{
		RestResponseWrapper wrapper = new RestResponseWrapper();
		wrapper = searchResultStrategy.getPartSearch(partSearchRequest, databaseLoggerStrategy, steps);

		return wrapper;
	}

	public RestResponseWrapper addPartsToCustomerList(PartSearchRequest searchRequest, String partsInputJson, AddPartsStrategy addPartsStrategy,
			LoggerStrategy loggerStrategy, HelperService helperService) throws Exception
	{
		RestResponseWrapper wrapper = new RestResponseWrapper();
		List<CustomerPart> addedParts = addPartsStrategy.addParts(searchRequest.getToken(),
				jsonConverter.convertJSONToList(partsInputJson, CustomerPart.class));
		if(addedParts == null || addedParts.isEmpty())
		{
			wrapper = new RestResponseWrapper(new Status("No results found", false));
			return wrapper;
		}
		wrapper.setAddpartsResult(addedParts);
		return wrapper;
	}

	public PartSearchResponse getCpnSearchResult(PartSearchRequest pcnSearchRequest, CpnSearchStrategy pcnSearchStrategy,
			LoggerStrategy databaseLoggerStrategy, HelperService helperService)
	{
		return pcnSearchStrategy.getCpnSearch(pcnSearchRequest, databaseLoggerStrategy, helperService);
	}

	public RestResponseWrapper getKeywordSearchResult(PartSearchRequest keywordSearchRequest, KeywordSearchStrategy keywordSearchStrategy,
			LoggerStrategy databaseLoggerStrategy, HelperService helperService)
	{
		return keywordSearchStrategy.getkewordSearch(keywordSearchRequest, databaseLoggerStrategy, helperService);
	}

	public RestResponseWrapper getAutoComplete(PartSearchRequest keywordSearchRequest, KeywordSearchStrategy keywordSearchStrategy,
			LoggerStrategy databaseLoggerStrategy, HelperService helperService)
	{

		return keywordSearchStrategy.getAutoCompleteResults(keywordSearchRequest, databaseLoggerStrategy, helperService);
	}

}
