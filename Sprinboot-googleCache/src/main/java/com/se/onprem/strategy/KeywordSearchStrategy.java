package com.se.onprem.strategy;

import org.springframework.stereotype.Service;

import com.se.onprem.dto.ws.PartSearchRequest;
import com.se.onprem.dto.ws.PartSearchResponse;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.services.HelperService;

@Service
public interface KeywordSearchStrategy
{

	RestResponseWrapper getkewordSearch(PartSearchRequest pcnSearchRequest, LoggerStrategy databaseLoggerStrategy, HelperService helperService);
	RestResponseWrapper getAutoCompleteResults(PartSearchRequest keywordRequest, LoggerStrategy databaseLoggerStrategy, HelperService helperService);
}
