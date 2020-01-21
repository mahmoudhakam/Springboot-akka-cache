package com.se.onprem.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.onprem.dto.business.bom.BOMDto;
import com.se.onprem.dto.business.bom.BOMMesssage;
import com.se.onprem.dto.business.bom.BOMRow;
import com.se.onprem.dto.business.bom.BomExportFeature;
import com.se.onprem.dto.business.bom.BomRequestParameters;
import com.se.onprem.dto.ws.BomExportRequest;
import com.se.onprem.dto.ws.KeywordFacet;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.dto.ws.Status;
import com.se.onprem.messages.OperationMessages;
import com.se.onprem.services.FileService;
import com.se.onprem.services.HelperService;
import com.se.onprem.services.cache.ExportPartsCache;
import com.se.onprem.strategy.LoggerStrategy;
import com.se.onprem.strategy.bom.IBOMActions;
import com.se.onprem.util.JsonHandler;
import com.se.onprem.util.ParametricConstants;
import com.se.onprem.util.UaaConstants;

@Service
public class NormalBomController extends BomController
{

	private static final String ACLDATA = "ACLData";
	private static final String MATCH_STATUS = "MATCH_STATUS";
	private static final String MANUFACTURER = "SE_MAN";
	private static final String PART_NUMBER = "SE_MPN";
	private static final String UPLOADED_MAN = "UPLOADED_MAN";
	private static final String UPLOADED_MPN = "UPLOADED_MPN";
	private static final String ROW_COLUMN_NAME = "Row #";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	JsonHandler<BOMRow> bomConverter = new JsonHandler<>();
	@Autowired
	private Environment env;

	@Autowired
	FileService fileService;

	@Autowired
	HelperService helper;

	@Autowired
	ExportPartsCache exportPartsCache;

	private final String PARTSLIST = "PartList";

	/**
	 * @deprecated Use {@link #saveBom(BomRequestParameters,IBOMActions,LoggerStrategy,HelperService,String)} instead
	 */
	public RestResponseWrapper saveBom(String bomData, String bomName, String bomId, int rowId, IBOMActions saveAction, LoggerStrategy databaseLoggerStrategy, HelperService helperService, String url, String token)
	{
		return saveBom(new BomRequestParameters(bomData, bomName, bomId, rowId), saveAction, databaseLoggerStrategy, helperService, url, token);
	}

	public RestResponseWrapper saveBom(BomRequestParameters parameterObject, IBOMActions saveAction, LoggerStrategy databaseLoggerStrategy, HelperService helperService, String url, String token)
	{
		RestResponseWrapper wrapper = new RestResponseWrapper();
		LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

		String bomDataParams = parameterObject.getBomData();
		String originalUploadedPartNumber = null;
		String originalUploadedManufacturer = null;

		if(StringUtils.isNotEmpty(parameterObject.getBomId()))
		{
			List<BOMRow> requestBomData = bomConverter.convertJSONToList(parameterObject.getBomData(), BOMRow.class);
			BOMRow requestBomRow = new BOMRow();

			if(!requestBomData.isEmpty())
			{
				requestBomRow = requestBomData.get(0);
			}

			originalUploadedPartNumber = new String(requestBomRow.getUploadedMpn());
			originalUploadedManufacturer = new String(requestBomRow.getUploadedManufacturer());

			requestBomRow.setUploadedMpn(new String(requestBomRow.getPartNumber()));
			requestBomRow.setUploadedManufacturer(new String(requestBomRow.getManufacturer()));

			bomDataParams = bomConverter.convertListToJon(requestBomData);
		}

		params.add("bomData", bomDataParams);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set(UaaConstants.AUTHORIZATION, token);

		try
		{
			RestResponseWrapper validationWrapper = helperService.getResposneFromURL(params, headers, url);
			List<BOMRow> bomResults = validationWrapper.getBomResult();

			if(StringUtils.isNotEmpty(parameterObject.getBomId()))
			{
				if(bomResults != null && !bomResults.isEmpty())
				{
					bomResults.get(0).setUploadedMpn(originalUploadedPartNumber);
					bomResults.get(0).setUploadedManufacturer(originalUploadedManufacturer);
				}
			}

			BOMMesssage requestMessage = new BOMMesssage();
			requestMessage.setBomParts(bomResults);
			requestMessage.setBomName(parameterObject.getBomName());
			requestMessage.setBomId(parameterObject.getBomId());
			requestMessage.setRowId(parameterObject.getRowId());
			BOMMesssage message = saveAction.doAction(requestMessage);
			wrapper.setBomSaveResult(message.getBomSaveResult());
		}
		catch(UnsupportedEncodingException e)
		{

			e.printStackTrace();
		}

		return wrapper;
	}

	@SuppressWarnings("unchecked")
	public RestResponseWrapper openBom(String bomId, String filters, String matchFilters, String lastFilter, String sortField, String sortType, int page, IBOMActions openAction, LoggerStrategy databaseLoggerStrategy, HelperService helperService,
			String bomValidationURL, String token)
	{
		RestResponseWrapper wrapper = new RestResponseWrapper();
		BOMMesssage requestMessage = BOMMesssage.builder().bomId(bomId).pageNumber(page).lastFilter(lastFilter).token(token).sortField(sortField).sortType(sortType).build();
		try
		{
			if(!StringUtils.isEmpty(filters))
			{
				Map<String, List<String>> filtersJsonMap = new ObjectMapper().readValue(filters, HashMap.class);
				requestMessage.setFiltersRequest(filtersJsonMap);
			}

			if(!StringUtils.isEmpty(matchFilters))
			{
				Map<String, List<String>> matchFiltersJsonMap = new ObjectMapper().readValue(matchFilters, HashMap.class);
				requestMessage.setMatchFiltersRequest(matchFiltersJsonMap);
			}

		}
		catch(IOException e)
		{

			e.printStackTrace();
		}

		BOMMesssage message = openAction.doAction(requestMessage);
		if(message.getResultCount() < 1 || message.getBomParts().isEmpty())
		{
			return new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));
		}

		Map<String, List<KeywordFacet>> bomFacet = createFacetWrapperFromFacetMap(message.getBomFacets());

		for(Map.Entry<String, List<KeywordFacet>> entry : bomFacet.entrySet())
		{
			List<KeywordFacet> keywordFacet = entry.getValue();
			Long facetCount = 0L, NAECount = 0L, NAZCount = 0L, NATotal = 0L;
			int NAEIndex = -1, NAZIndex = -1;
			for(int i = 0; i < keywordFacet.size(); i++)
			{
				facetCount += keywordFacet.get(i).getCount();
				if(keywordFacet.get(i).getName().equals("N/A_E"))
				{
					NAEIndex = i;
					NAECount = keywordFacet.get(i).getCount();
				}
				else if(keywordFacet.get(i).getName().equals("N/A_Z"))
				{
					NAZIndex = i;
					NAZCount = keywordFacet.get(i).getCount();
				}
			}
			// if (facetCount < message.getResultCount()) {
			if(NAEIndex > -1)
			{
				bomFacet.get(entry.getKey()).remove(NAEIndex);
				NAZIndex--;
				NATotal += NAECount;
			}
			if(NAZIndex > -1)
			{
				bomFacet.get(entry.getKey()).remove(NAZIndex);
				NATotal += NAZCount;
			}
			if(message.isExact() && !StringUtils.equals(message.getLastFilter(), entry.getKey()))
			{
				NATotal -= NAZCount;
			}
			if(facetCount < message.getResultCount())
			{
				NATotal += (message.getResultCount() - facetCount);
			}
			if(NATotal > 0)
			{
				bomFacet.get(entry.getKey()).add(new KeywordFacet("N/A", NATotal));
			}

			// }
		}

		bomFacet.putAll(message.getBomMatchFacets());
		bomFacet.putAll(createFacetWrapperFromFacetMap(message.getBomRiskFacets()));
		wrapper.setBomResult(message.getBomParts());
		wrapper.setBomFacets(bomFacet);
		wrapper.setTotalItems(message.getResultCount());

		return wrapper;
	}

	private Map<String, List<KeywordFacet>> createFacetWrapperFromFacetMap(Map<String, Map<String, Set<String>>> bomFacets)
	{
		Map<String, List<KeywordFacet>> keywordFacetMap = new LinkedHashMap<>();
		if(bomFacets != null)
		{
			for(String element : bomFacets.keySet())
			{
				keywordFacetMap.put(element, createFacetListFromfacetMap(bomFacets.get(element)));
			}
		}
		return keywordFacetMap;
	}

	private List<KeywordFacet> createFacetListFromfacetMap(Map<String, Set<String>> map)
	{
		List<KeywordFacet> facetList = new LinkedList<>();
		for(String facet : map.keySet())
		{
			KeywordFacet keywordFacet = new KeywordFacet(facet, (long) map.get(facet).size());
			facetList.add(keywordFacet);

		}
		return facetList;
	}

	public RestResponseWrapper replaceBOMPart(String bomData, String bomId, int rowId, IBOMActions saveAction, LoggerStrategy databaseLoggerStrategy, HelperService helperService, String url, String token)
	{
		RestResponseWrapper wrapper = new RestResponseWrapper();
		JsonHandler<BOMRow> bomRowJsonHandler = new JsonHandler<>();

		try
		{
			List<BOMRow> bomResults = bomRowJsonHandler.convertJSONToList(bomData, BOMRow.class);
			BOMMesssage requestMessage = new BOMMesssage();
			requestMessage.setBomParts(bomResults);
			requestMessage.setRowId(rowId);
			requestMessage.setBomId(bomId);
			requestMessage.setToken(token);
			BOMMesssage message = saveAction.doAction(requestMessage);
			wrapper.setBomSaveResult(message.getBomSaveResult());
		}
		catch(Exception e)
		{

			e.printStackTrace();
		}

		return wrapper;
	}

	public RestResponseWrapper bomExport(String bomId, String columns, String matchStatus, IBOMActions bomExportAction, LoggerStrategy databaseLoggerStrategy, HelperService helperService, String bomValidationURL, String token)
	{

		RestResponseWrapper wrapper = new RestResponseWrapper();

		Map<String, List<String>> columnsJsonMap = new LinkedHashMap<>();
		Map<String, List<String>> columnsJsonMapRequested = new LinkedHashMap<>();
		List<String> matchStatusJsonList = new ArrayList<>();

		addingACLByDefault(columnsJsonMap);

		try
		{
			if(!StringUtils.isEmpty(columns))
			{
				columnsJsonMapRequested = new ObjectMapper().readValue(columns, LinkedHashMap.class);
				columnsJsonMap.putAll(columnsJsonMapRequested);
			}
			if(!StringUtils.isEmpty(matchStatus))
			{
				matchStatusJsonList = new ObjectMapper().readValue(matchStatus, ArrayList.class);
			}
		}
		catch(IOException e)
		{

			e.printStackTrace();
		}

		List<String> excelColumns = new ArrayList<>();
		List<String> categories = new ArrayList<>();
		excelColumns.add(ROW_COLUMN_NAME);
		StringBuilder bomPartsQuery = new StringBuilder();
		String commaSeperator = "";
		for(Map.Entry<String, List<String>> columnsJsonMapEntry : columnsJsonMap.entrySet())
		{
			for(String feature : columnsJsonMapEntry.getValue())
			{
				if(columnsJsonMapEntry.getKey().equals(ACLDATA))
				{
					bomPartsQuery.append(commaSeperator).append(feature);
					commaSeperator = ",";
				}
				excelColumns.add(feature);
			}
			categories.add(columnsJsonMapEntry.getKey());
		}

		BOMMesssage requestMessage = new BOMMesssage();
		requestMessage.setBomId(bomId);
		requestMessage.setBomExportQuery(bomPartsQuery.toString());
		requestMessage.setMatchStatusExportRequest(matchStatusJsonList);
		BOMMesssage message = bomExportAction.doAction(requestMessage);

		List<BOMRow> partsToRequest = new LinkedList<>(message.getBomParts());
		Map<String, Map<String, List<BomExportFeature>>> cachedPartsData = getPartsDataCachedMap(message.getBomParts(), partsToRequest);
		if(cachedPartsData.isEmpty())
		{
			logger.info("Still no cached parts till now");
		}
		Map<String, Map<String, List<BomExportFeature>>> partsData = new LinkedHashMap<>();
		if(!partsToRequest.isEmpty())
		{
			logger.info("Getting parts result from api");
			if(categories.size() > 1)
			{
				partsData = getPartsDataFromAPI(message.getBomParts(), columnsJsonMap, token);
				cachePartsForNext(partsData);
			}
		}
		if(partsToRequest.isEmpty())
		{
			logger.info("All Bom id:{} result are got from cached parts", bomId);
		}
		partsData = Stream.concat(cachedPartsData.entrySet().stream(), partsData.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));

		for(BOMRow bomRow : message.getBomParts())
		{
			List<BomExportFeature> aclFeatures = new ArrayList<>();

			aclFeatures.add(new BomExportFeature(MATCH_STATUS, bomRow.getMatchStatus()));
			aclFeatures.add(new BomExportFeature(MANUFACTURER, bomRow.getManufacturer()));
			aclFeatures.add(new BomExportFeature(PART_NUMBER, bomRow.getNanPartNumber()));
			aclFeatures.add(new BomExportFeature(UPLOADED_MAN, bomRow.getUploadedManufacturer()));
			aclFeatures.add(new BomExportFeature(UPLOADED_MPN, bomRow.getUploadedMpn()));

			if(partsData.get(String.valueOf(bomRow.getComID())) == null)
			{
				partsData.put(String.valueOf(bomRow.getRowKey()), new LinkedHashMap<>());
			}
			else
			{
				partsData.put(String.valueOf(bomRow.getRowKey()), new LinkedHashMap<>(partsData.get(String.valueOf(bomRow.getComID()))));
				partsData.remove(String.valueOf(bomRow.getComID()));

			}
			partsData.get(String.valueOf(bomRow.getRowKey())).put(ACLDATA, aclFeatures);
		}

		Map<String, Map<String, String>> excelRows = new LinkedHashMap<>();

		for(Map.Entry<String, Map<String, List<BomExportFeature>>> partsDataEntry : partsData.entrySet())
		{
			if(partsDataEntry.getValue() == null || partsDataEntry.getValue().size() == 0)
			{
				continue;
			}
			Map<String, String> partColumnsMap = new LinkedHashMap<>();
			String cellDefaultValue = "";

			for(String excelColumn : excelColumns)
			{
				if(StringUtils.equals(excelColumn, ROW_COLUMN_NAME))
				{
					cellDefaultValue = partsDataEntry.getKey().substring(0, partsDataEntry.getKey().indexOf('|'));
				}
				else
				{
					cellDefaultValue = "";
				}
				partColumnsMap.put(excelColumn, cellDefaultValue);
			}

			for(Map.Entry<String, List<BomExportFeature>> featuresEntry : partsDataEntry.getValue().entrySet())
			{
				if(featuresEntry.getValue() != null)
				{
					for(BomExportFeature bomExportFeature : featuresEntry.getValue())
					{
						if(partColumnsMap.get(bomExportFeature.getFeatureName()) != null)
						{
							partColumnsMap.put(bomExportFeature.getFeatureName(), bomExportFeature.getFeatureValue());
						}
					}
				}
			}

			excelRows.put(partsDataEntry.getKey(), partColumnsMap);
		}

		wrapper.setExcelFile(fileService.createExcelForDownlaod(excelRows, excelColumns, message));

		wrapper.setBomData((new BOMDto()).builder().bomName(message.getBomName()).build());

		return wrapper;
	}

	private void cachePartsForNext(Map<String, Map<String, List<BomExportFeature>>> partsData)
	{
		logger.info("Start caching export result");
		partsData.entrySet().forEach(part -> {
			String comId = part.getKey();
			Map<String, List<BomExportFeature>> result = part.getValue();
			exportPartsCache.addPartToCache(comId, result);
		});
	}

	private Map<String, Map<String, List<BomExportFeature>>> getPartsDataCachedMap(List<BOMRow> bomParts, List<BOMRow> partsToRequest)
	{
		// check sent parts in cache layer
		Map<String, Map<String, List<BomExportFeature>>> cachedPartsData = new LinkedHashMap<>();
		bomParts.forEach(part -> {
			Map<String, List<BomExportFeature>> partCachedResult = exportPartsCache.getPartOrNull(part.getComID() + "");
			if(partCachedResult != null)
			{
				cachedPartsData.put(String.valueOf(part.getComID()), partCachedResult);
				partsToRequest.remove(part);
			}
		});
		logger.info("Parts From cached layer:{}", cachedPartsData);
		return cachedPartsData;
	}

	private void addingACLByDefault(Map<String, List<String>> columnsJsonMap)
	{
		List<String> aclFeatures = new ArrayList<>();
		aclFeatures.add(MATCH_STATUS);
		aclFeatures.add(UPLOADED_MPN);
		aclFeatures.add(UPLOADED_MAN);

		columnsJsonMap.put(ACLDATA, aclFeatures);

	}

	private Map<String, Map<String, List<BomExportFeature>>> getPartsDataFromAPI(List<BOMRow> bomParts, Map<String, List<String>> columnsJsonMap, String token)
	{
		BomExportRequest bomExportRequest = new BomExportRequest();

		RestResponseWrapper responseWrapper = new RestResponseWrapper();

		Map<String, String> headers = new HashMap<>();

		headers.put(UaaConstants.AUTHORIZATION, token);

		List<String> comIds = new ArrayList<>();

		for(BOMRow bomRow : bomParts)
		{
			if(bomRow.getComID() == 0L)
			{
				continue;
			}
			comIds.add(String.valueOf(bomRow.getComID()));
		}

		bomExportRequest.setComIDs(comIds);
		bomExportRequest.setCategories(columnsJsonMap);

		try
		{
			if(bomExportRequest.getComIDs() != null && bomExportRequest.getComIDs().size() > 0)
			{
				String url = env.getProperty(ParametricConstants.BOM_EXPORT_API_URL);

				ZonedDateTime start = ZonedDateTime.now();
				responseWrapper = helper.getResposneFromURL(null, bomExportRequest, url, headers, HttpMethod.POST.toString());

				ZonedDateTime end = ZonedDateTime.now();
				logger.info("KESHO took {} milli seconds" + ChronoUnit.MILLIS.between(start, end));
			}

		}
		catch(IOException e)
		{

			e.printStackTrace();
		}

		return responseWrapper.getPartList();
	}

}
