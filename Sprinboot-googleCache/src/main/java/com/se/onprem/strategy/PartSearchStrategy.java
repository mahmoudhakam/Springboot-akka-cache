package com.se.onprem.strategy;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.se.onprem.dto.ws.CustomerPart;
import com.se.onprem.dto.ws.DebugStep;
import com.se.onprem.dto.ws.PartSearchRequest;
import com.se.onprem.dto.ws.RestResponseWrapper;

@Service
public interface PartSearchStrategy
{
	RestResponseWrapper getPartSearch(PartSearchRequest request,LoggerStrategy logger, List<DebugStep> steps);
	List<CustomerPart> completePartsWithMissingComIds(List<CustomerPart> inputParts, String token) throws UnsupportedEncodingException;

}
