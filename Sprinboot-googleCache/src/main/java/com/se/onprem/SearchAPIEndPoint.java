package com.se.onprem;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.se.onprem.controller.SearchController;
import com.se.onprem.dto.ws.DebugStep;
import com.se.onprem.dto.ws.PartSearchRequest;
import com.se.onprem.dto.ws.PartSearchResponse;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.dto.ws.Status;
import com.se.onprem.messages.OperationMessages;
import com.se.onprem.messages.PartSearchOperationMessages;
import com.se.onprem.messages.PartSearchStatus;
import com.se.onprem.services.HelperService;
import com.se.onprem.strategy.AddPartsStrategy;
import com.se.onprem.strategy.CpnSearchStrategy;
import com.se.onprem.strategy.KeywordSearchStrategy;
import com.se.onprem.strategy.LoggerStrategy;
import com.se.onprem.strategy.PartSearchStrategy;
import com.se.onprem.strategy.impl.PartsSolrAddingStartegy;
import com.se.onprem.strategy.impl.SearchResultStrategyImpl;
import com.se.onprem.util.PartSearchServiceConstants;
import com.se.onprem.util.UaaConstants;

@RestController
@CrossOrigin(maxAge = 3600)
public class SearchAPIEndPoint
{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private SearchController searchController;
	private HelperService helperService;
	private PartSearchStrategy searchResultStrategy;
	private CpnSearchStrategy pcnSearchStrategy;
	private LoggerStrategy databaseLoggerStrategy;
	private AddPartsStrategy addPartsStrategy;
	private KeywordSearchStrategy keywordSearchStrategy;

	@Autowired
	public SearchAPIEndPoint(SearchController searchController, HelperService helperService, SearchResultStrategyImpl searchResultStrategy,
			PartsSolrAddingStartegy solrAddingStrategy, CpnSearchStrategy pcnSearchStrategy, KeywordSearchStrategy keywordSearchStrategy)
	{

		super();
		this.searchController = searchController;
		this.helperService = helperService;
		this.searchResultStrategy = searchResultStrategy;
		this.addPartsStrategy = solrAddingStrategy;
		this.pcnSearchStrategy = pcnSearchStrategy;
		this.keywordSearchStrategy = keywordSearchStrategy;
	}

	@RequestMapping(value = "/partSearch", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> getPartSearch(HttpServletRequest request,
			@RequestParam(defaultValue = "", name = "debugMode") String debugMode,
			@RequestParam(name = "partNumber", defaultValue = "") String partNumber, @RequestParam(name = "mode", defaultValue = "") String mode,
			@RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(name = "pageSize", defaultValue = "25") int pageSize,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.WILDCARD_SINGLE) String wildcardSingle,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.WILDCARD_MULTI) String wildCardMulti,
			@RequestParam(name = "customerDataFirst", defaultValue = "false") boolean boostResult)
	{

		long start = System.currentTimeMillis();
		PartSearchRequest partListSearchRequest = new PartSearchRequest();

		List<DebugStep> steps = new ArrayList<>();
		RestResponseWrapper restResponseWrapper = null;

		try
		{
			partListSearchRequest = PartSearchRequest.builder().request(request).fullURL(helperService.getFullUrl(request))
					.remoteAddress(helperService.getRemoteAddress(request)).partNumber(partNumber).debugMode(debugMode).mode(mode).pageSize(pageSize)
					.pageNumber(pageNumber).wildCardMulti(wildCardMulti).wildcardSingle(wildcardSingle).boostResults(boostResult)
					.token(request.getHeader(UaaConstants.AUTHORIZATION)).build();

			restResponseWrapper = searchController.getPartSearchResult(partListSearchRequest, searchResultStrategy, databaseLoggerStrategy,
					helperService, steps);
			// debugger
			if(!partListSearchRequest.getDebugMode().isEmpty() && partListSearchRequest.getDebugMode().equals("true"))
			{
				DebugStep setpAPI = new DebugStep("Part List Search Customer API", request.getQueryString(), (System.currentTimeMillis() - start));
				steps.add(setpAPI);
				restResponseWrapper.setSteps(steps);
			}

		}
		catch(Exception e)
		{

			logger.error("Error During getting data [getAllTaxonomy] ", e);
			String timeTaken = (System.currentTimeMillis() - start) + "";
			restResponseWrapper = helperService.handleInternalErrors("Part Search", e, timeTaken);

		}
		return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);
	}

	@RequestMapping(value = "/addPartsByComID", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> addParts(HttpServletRequest request, @RequestParam(name = "parts", defaultValue = "") String parts)
	{

		long start = System.currentTimeMillis();
		PartSearchRequest searchRequest = new PartSearchRequest();
		try
		{

			searchRequest = PartSearchRequest.builder().fullURL(helperService.getFullUrl(request)).request(request)
					.remoteAddress(helperService.getRemoteAddress(request)).token(request.getHeader(UaaConstants.AUTHORIZATION)).build();

			RestResponseWrapper restResponseWrapper = searchController.addPartsToCustomerList(searchRequest, parts, addPartsStrategy, databaseLoggerStrategy,
					helperService);

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{

			logger.error("Error During getting data [add parts by com id] ", e);

			String timeTaken = (System.currentTimeMillis() - start) + "";

			RestResponseWrapper restResponseWrapper = helperService.handleInternalErrors("Part Search", e, timeTaken);

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/cpnSearch", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<PartSearchResponse> listCpnSearch(HttpServletRequest request,
			@RequestParam(name = "cpnData", defaultValue = "") String partNumber)
	{
		long start = System.currentTimeMillis();
		PartSearchRequest pcnSearchRequest = new PartSearchRequest();

		try
		{
			pcnSearchRequest = PartSearchRequest.builder().request(request).fullURL(helperService.getFullUrl(request))
					.remoteAddress(helperService.getRemoteAddress(request)).partNumber(partNumber)
					.token(request.getHeader(UaaConstants.AUTHORIZATION)).build();
			PartSearchResponse partSearchResponseWrapper = searchController.getCpnSearchResult(pcnSearchRequest, pcnSearchStrategy,
					databaseLoggerStrategy, helperService);
			return new ResponseEntity<PartSearchResponse>(partSearchResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [list Pcn Search] ", e);

			PartSearchResponse restResponseWrapper = new PartSearchResponse(new PartSearchStatus(PartSearchOperationMessages.INTERNAL_ERROR, false));

			return new ResponseEntity<PartSearchResponse>(restResponseWrapper, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/keywordSearch", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> keywordSearch(HttpServletRequest request,
			@RequestParam(name = "keyword", defaultValue = "") String partNumber,
			@RequestParam(name = "partNumberBoost", defaultValue = "false") boolean boostResult,
			@RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(name = "pageSize", defaultValue = "25") int pageSize,
			@RequestParam(name = "filters", defaultValue = "") String filters, @RequestParam(name = "order", defaultValue = "") String order,
			@RequestParam(name = "autocompleteSection", defaultValue = "") String autocompleteSection,
			@RequestParam(name = "exact", defaultValue = "false") boolean isExact)
	{

		PartSearchRequest keywordSearchRequest = new PartSearchRequest();

		try
		{
			keywordSearchRequest = PartSearchRequest.builder().request(request).fullURL(helperService.getFullUrl(request))
					.remoteAddress(helperService.getRemoteAddress(request)).partNumber(partNumber).pageSize(pageSize).pageNumber(pageNumber)
					.boostResults(boostResult).filters(filters).order(order).exact(isExact).autocompleteSection(autocompleteSection)
					.token(request.getHeader(UaaConstants.AUTHORIZATION)).build();
			RestResponseWrapper partSearchResponseWrapper = searchController.getKeywordSearchResult(keywordSearchRequest, keywordSearchStrategy,
					databaseLoggerStrategy, helperService);

			return new ResponseEntity<RestResponseWrapper>(partSearchResponseWrapper, HttpStatus.OK);
		}
		catch(Exception e)
		{
			logger.error("Error During getting data [list Pcn Search] ", e);

			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.INTERNAL_ERROR, false));

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/autoComplete", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> autoComplete(HttpServletRequest request,
			@RequestParam(name = "keyword", defaultValue = "") String partNumber, @RequestParam(name = "debug", defaultValue = "false") String debug)
	{

		PartSearchRequest keywordSearchRequest = new PartSearchRequest();

		try
		{
			keywordSearchRequest = PartSearchRequest.builder().request(request).fullURL(helperService.getFullUrl(request))
					.remoteAddress(helperService.getRemoteAddress(request)).partNumber(partNumber).debugMode(debug)
					.token(request.getHeader(UaaConstants.AUTHORIZATION)).build();

			RestResponseWrapper partSearchResponseWrapper = searchController.getAutoComplete(keywordSearchRequest, keywordSearchStrategy,
					databaseLoggerStrategy, helperService);

			return new ResponseEntity<RestResponseWrapper>(partSearchResponseWrapper, HttpStatus.OK);
		}
		catch(Exception e)
		{
			logger.error("Error During getting data [list Pcn Search] ", e);

			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.INTERNAL_ERROR, false));

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);
		}
	}

}
