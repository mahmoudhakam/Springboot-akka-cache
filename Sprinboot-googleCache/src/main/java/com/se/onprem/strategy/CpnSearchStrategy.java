package com.se.onprem.strategy;

import com.se.onprem.dto.ws.PartSearchRequest;
import com.se.onprem.dto.ws.PartSearchResponse;
import com.se.onprem.services.HelperService;


public interface CpnSearchStrategy
{

	PartSearchResponse getCpnSearch(PartSearchRequest pcnSearchRequest, LoggerStrategy databaseLoggerStrategy, HelperService helperService);
	

}
