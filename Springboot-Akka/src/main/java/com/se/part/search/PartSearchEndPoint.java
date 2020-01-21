package com.se.part.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.se.part.search.controller.PartSearchServiceDelegate;
import com.se.part.search.dto.authentication.ArrowAuthenticationRequest;
import com.se.part.search.dto.authentication.PartSearchAuthenticationResponse;
import com.se.part.search.dto.export.BomExportRequest;
import com.se.part.search.dto.export.BomExportResponse;
import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.keyword.OperationMessages;
import com.se.part.search.dto.keyword.RequestParameters;
import com.se.part.search.dto.keyword.RestResponseWrapper;
import com.se.part.search.dto.keyword.Status;
import com.se.part.search.dto.partSearch.PartSearchRequest;
import com.se.part.search.dto.partSearch.PartSearchResponse;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.PartSearchloggerService;
import com.se.part.search.services.authentication.PartUserAuthentication;
import com.se.part.search.services.authentication.TokenBasedAuthentication;
import com.se.part.search.services.export.BomExportService;
import com.se.part.search.services.keywordSearch.KeywordSearchSearch;
import com.se.part.search.services.keywordSearch.KeywordSearchTransformation;
import com.se.part.search.services.keywordSearch.KeywordSearchValidation;
import com.se.part.search.services.partSearch.PartSearchSearch;
import com.se.part.search.services.partSearch.PartSearchTransformation;
import com.se.part.search.services.partSearch.PartSearchValidation;
import com.se.part.search.util.PartSearchServiceConstants;

/**
 * @author mahmoud_abdelhakam
 */

@RestController
@CrossOrigin(maxAge = 3600)
public class PartSearchEndPoint
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private PartSearchServiceDelegate delegate;
	private PartSearchTransformation partSearchTransformation;
	private PartSearchValidation partSearchValidation;
	private PartSearchSearch partSearchSearch;
	private KeywordSearchSearch keywordSearchSearch;
	private KeywordSearchTransformation keywordSearchTransformation;
	private KeywordSearchValidation keywordSearchValidation;
	private PartSearchloggerService dbLoggerService;
	private PartSearchHelperService helperService;
	private PartUserAuthentication userAuthStrategy;
	private TokenBasedAuthentication tokenAuthService;
	private BomExportService bomExportService;

	@Autowired
	public PartSearchEndPoint(PartSearchServiceDelegate delegate, PartSearchloggerService dbLoggerService, PartSearchHelperService helperService,
			PartSearchTransformation partSearchTransformation, PartSearchValidation partSearchValidation, PartSearchSearch partSearchSearch,
			KeywordSearchSearch keywordSearchSearch, KeywordSearchTransformation keywordSearchTransformation,
			KeywordSearchValidation keywordSearchValidation, PartUserAuthentication userAuthStrategy, TokenBasedAuthentication tokenAuthService,
			BomExportService bomExportService)
	{
		this.delegate = delegate;
		this.dbLoggerService = dbLoggerService;
		this.helperService = helperService;
		this.partSearchSearch = partSearchSearch;
		this.partSearchTransformation = partSearchTransformation;
		this.partSearchValidation = partSearchValidation;
		this.keywordSearchSearch = keywordSearchSearch;
		this.keywordSearchTransformation = keywordSearchTransformation;
		this.keywordSearchValidation = keywordSearchValidation;
		this.userAuthStrategy = userAuthStrategy;
		this.tokenAuthService = tokenAuthService;
		this.bomExportService = bomExportService;
	}

	// @RequestMapping(value = "partDetails", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_XML_VALUE,
	// MediaType.APPLICATION_JSON_VALUE })
	// public ResponseEntity<PartDetailsResponse> getpartDetailsGET(HttpServletRequest request, HttpServletResponse response,
	// @RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.COM_ID) String comIDs,
	// @RequestParam(defaultValue = "1", name = PartSearchServiceConstants.Parameters.PAGE_NUMBER) String pageNumber, @RequestParam(defaultValue =
	// "20", name = PartSearchServiceConstants.Parameters.PAGE_SIZE) String pageSize,
	// @RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.SE_TOKEN) String token, @RequestParam(defaultValue = "", name =
	// PartSearchServiceConstants.Parameters.DEBUG_MODE) String debugMode)
	// {
	//
	// PartDetailsRequest partDetailsRequest = new PartDetailsRequest();
	// partDetailsRequest.setDebugMode(debugMode);
	// partDetailsRequest.setSeToken(token);
	// partDetailsRequest.setComIDs(comIDs);
	// partDetailsRequest.setPageNumber(pageNumber);
	// partDetailsRequest.setPageSize(pageSize);
	// partDetailsRequest.setRequestID(System.nanoTime() + "");
	// partDetailsRequest.setRequest(request);
	// partDetailsRequest.setFullURL(helperService.getFullUrl(request));
	// partDetailsRequest.setRemoteAddress(helperService.getRemoteAddress(request));
	// long start = System.currentTimeMillis();
	// Optional<PartDetailsResponse> result = delegate.partDetails(partDetailsRequest, partDetailsValidation, partDetailsSearch,
	// partDetailsTransformation, dbLoggerService, tokenAuthService);
	// logger.info("PartDetails takes about:{}", (System.currentTimeMillis() - start));
	// PartDetailsResponse resp = result.get();
	// return new ResponseEntity<>(resp, HttpStatus.OK);
	// }
	//
	// @RequestMapping(value = "partDetails", method = { RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
	// MediaType.APPLICATION_JSON_VALUE })
	// public ResponseEntity<PartDetailsResponse> getpartDetailsPOST(HttpServletRequest request, HttpServletResponse response, @RequestBody(required =
	// true) PartDetailsRequest partDetailsRequest)
	// {
	//
	// partDetailsRequest.setRequestID(System.nanoTime() + "");
	// partDetailsRequest.setRequest(request);
	// partDetailsRequest.setFullURL(helperService.getFullUrl(request));
	// partDetailsRequest.setRemoteAddress(helperService.getRemoteAddress(request));
	// long start = System.currentTimeMillis();
	// Optional<PartDetailsResponse> result = delegate.partDetails(partDetailsRequest, partDetailsValidation, partDetailsSearch,
	// partDetailsTransformation, dbLoggerService, tokenAuthService);
	// logger.info("PartDetails takes about:{}", (System.currentTimeMillis() - start));
	// PartDetailsResponse resp = result.get();
	// return new ResponseEntity<>(resp, HttpStatus.OK);
	// }

	@RequestMapping(value = "/keywordSearch", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> getKeywordSearch(RequestParameters requestParameters, HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = delegate.getKeywordSearch(requestParameters);

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [getKeywordSearch] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/keywordSearchQuery", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<String> getKeywordSearchQuery(RequestParameters requestParameters, HttpServletRequest request)
	{
		try
		{
			String query = delegate.getKeywordSearchQuery(requestParameters);

			return new ResponseEntity<String>(query, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [getKeywordSearchdata] ", e);
			return new ResponseEntity<String>("Query couldn't be created", HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "partSearch", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<PartSearchResponse> getPartSearchGET(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.PART_NUMBER) String partNumber,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.SEARCH_MODE) String mode,
			@RequestParam(defaultValue = "1", name = PartSearchServiceConstants.Parameters.PAGE_NUMBER) String pageNumber,
			@RequestParam(defaultValue = "25", name = PartSearchServiceConstants.Parameters.PAGE_SIZE) String pageSize,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.WILDCARD_SINGLE) String wildcardSingle,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.WILDCARD_MULTI) String wildCardMulti,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.SE_TOKEN) String token,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.DEBUG_MODE) String debugMode,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.START) String start,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.EXCLUDED_PARTS) String excludedParts)
	{

		PartSearchRequest partSearchRequest = new PartSearchRequest();
		partSearchRequest.setDebugMode(debugMode);
		partSearchRequest.setSeToken(token);
		partSearchRequest.setPartNumber(partNumber);
		partSearchRequest.setPageNumber(pageNumber);
		partSearchRequest.setPageSize(pageSize);
		partSearchRequest.setStart(start);
		partSearchRequest.setExcludedParts(excludedParts);
		partSearchRequest.setMode(mode);
		partSearchRequest.setWildCardMulti(wildCardMulti);
		partSearchRequest.setWildcardSingle(wildcardSingle);
		partSearchRequest.setRequestID(System.nanoTime() + "");
		partSearchRequest.setRequest(request);
		partSearchRequest.setFullURL(helperService.getFullUrl(request));
		partSearchRequest.setRemoteAddress(helperService.getRemoteAddress(request));

		Optional<PartSearchResponse> result = delegate.partSearch(partSearchRequest, partSearchValidation, partSearchSearch, partSearchTransformation,
				dbLoggerService, tokenAuthService);
		return new ResponseEntity<>(result.get(), HttpStatus.OK);
	}

	@RequestMapping(value = "partSearch", method = { RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<PartSearchResponse> getPartSearchPOST(HttpServletRequest request, HttpServletResponse response,
			@RequestBody(required = true) PartSearchRequest partSearchRequest)
	{

		partSearchRequest.setRequestID(System.nanoTime() + "");
		partSearchRequest.setRequest(request);
		partSearchRequest.setFullURL(helperService.getFullUrl(request));
		partSearchRequest.setRemoteAddress(helperService.getRemoteAddress(request));
		Optional<PartSearchResponse> result = delegate.partSearch(partSearchRequest, partSearchValidation, partSearchSearch, partSearchTransformation,
				dbLoggerService, tokenAuthService);
		return new ResponseEntity<>(result.get(), HttpStatus.OK);
	}

	@RequestMapping(value = "getToken", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<PartSearchAuthenticationResponse> getTokenGET(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.USER_NAME) String userName,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.API_KEY) String apiKey)
	{
		ArrowAuthenticationRequest arrowAuthenticationRequest = new ArrowAuthenticationRequest();
		arrowAuthenticationRequest.setApiKey(apiKey);
		arrowAuthenticationRequest.setUserName(userName);
		arrowAuthenticationRequest.setRequestID(System.nanoTime() + "");
		arrowAuthenticationRequest.setRequest(request);
		arrowAuthenticationRequest.setFullURL(helperService.getFullUrl(request));
		arrowAuthenticationRequest.setRemoteAddress(helperService.getRemoteAddress(request));
		Optional<PartSearchAuthenticationResponse> result = delegate.getToken(arrowAuthenticationRequest, userAuthStrategy);
		return new ResponseEntity<>(result.get(), HttpStatus.OK);
	}

	@RequestMapping(value = "getToken", method = { RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<PartSearchAuthenticationResponse> getTokenPOST(HttpServletRequest request, HttpServletResponse response,
			@RequestBody(required = true) ArrowAuthenticationRequest arrowAuthenticationRequest)
	{
		arrowAuthenticationRequest.setRequestID(System.nanoTime() + "");
		arrowAuthenticationRequest.setRequest(request);
		arrowAuthenticationRequest.setFullURL(helperService.getFullUrl(request));
		arrowAuthenticationRequest.setRemoteAddress(helperService.getRemoteAddress(request));
		Optional<PartSearchAuthenticationResponse> result = delegate.getToken(arrowAuthenticationRequest, userAuthStrategy);
		return new ResponseEntity<>(result.get(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getAutoComplete", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> getAutoComplete(RequestParameters requestParameters, HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = delegate.getAutoComplete(requestParameters);

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [getKeywordSearch] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/validateBOM", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> validateBOM(RequestParameters requestParameters, HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = delegate.validateBOM(requestParameters);

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [getKeywordSearch] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/similarParts", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> getSimilarParts(
			@RequestParam(name = PartSearchServiceConstants.Parameters.PART_NUMBER) String partNumber,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.MAN_ID) String manufacturer,
			@RequestParam(defaultValue = "", name = PartSearchServiceConstants.Parameters.SIMILAR_TYPE) String similarStep,
			HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = delegate.getSimilarParts(partNumber, manufacturer, similarStep);

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [getKeywordSearch] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/facetDistribution", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> getFacetswithComIds(RequestParameters requestParameters, HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = delegate.createFacetMap(requestParameters);

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [getKeywordSearch] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "bomExport", method = { RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<BomExportResponse> getBOMExportPOST(HttpServletRequest request, HttpServletResponse response,
			@RequestBody(required = true) BomExportRequest bomExportRequest)
	{
		bomExportRequest.setRequestID(System.nanoTime() + "");
		bomExportRequest.setRequest(request);
		bomExportRequest.setFullURL(helperService.getFullUrl(request));
		bomExportRequest.setRemoteAddress(helperService.getRemoteAddress(request));
		BomExportResponse res = delegate.bomExport(bomExportRequest, bomExportService);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}


}
