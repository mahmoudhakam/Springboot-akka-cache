package com.se.part.search.services.keywordSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.keyword.AutoCompleteResult;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.dto.keyword.KeywordFacet;
import com.se.part.search.dto.keyword.KeywordFacetsWrapper;
import com.se.part.search.dto.keyword.OperationMessages;
import com.se.part.search.dto.keyword.RequestParameters;
import com.se.part.search.dto.keyword.RestResponseWrapper;
import com.se.part.search.dto.keyword.Status;
import com.se.part.search.dto.keyword.parametric.FeatureDTO;
import com.se.part.search.dto.keyword.parametric.FeatureValueDTO;
import com.se.part.search.dto.keyword.parametric.SearchStep;
import com.se.part.search.dto.partSearch.PartSearchDTO;
import com.se.part.search.dto.partSearch.PartSearchResult;
import com.se.part.search.util.ConstantSolrFields;

@Service("keywordSearchServiceImpl")
public class KeywordSearchServiceImpl implements KeywordSearchService
{
	@Autowired
	private Util<?> util;
	@Autowired
	private SolrClient manBasicSolrServer;
	@Autowired
	private SolrClient descSolrServer;
	@Autowired
	private SolrClient partsSummarySolrServer;
	JsonHandler<FeatureDTO> featureConverter = new JsonHandler<>();

	@Autowired
	private ParametricSearchServiceHelper parametricSearchServiceHelper;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("#{environment['part.keyword.drop.limit']}")
	Integer keywordDropLimit;

	@Override
	public RestResponseWrapper getAutoComplete(RequestParameters filterDTO) throws SolrServerException, IOException, InterruptedException, ExecutionException
	{
		logger.info("getKeywordSearch: and filterDTO =" + filterDTO);
		RestResponseWrapper wrapper = new RestResponseWrapper();
		List<SearchStep> steps = new ArrayList<>();
		String keyword = filterDTO.getKeyword();
		CountDownLatch autoCompletelatch = new CountDownLatch(5);
		Future<List<String>> partsSugesstionFuture = util.getSugesstions(keyword, partsSummarySolrServer, ConstantSolrFields.AUTO_COMPLETE_DICTIONARY_NAME, "part auto complete", autoCompletelatch, steps);
		Future<List<String>> partsNANSugesstionFuture = util.getSugesstions(util.removeSpecialCharacters(keyword,true), partsSummarySolrServer, ConstantSolrFields.NAN_AUTO_COMPLETE_DICTIONARY_NAME, "part auto complete", autoCompletelatch, steps);
		Future<List<String>> manSuggestionFuture = util.getSugesstions(keyword, manBasicSolrServer, ConstantSolrFields.AUTO_COMPLETE_DICTIONARY_NAME, "man auto complete", autoCompletelatch, steps);
		Future<List<String>> descSugesstionFuture = util.getSugesstions(keyword, descSolrServer, ConstantSolrFields.AUTO_COMPLETE_DICTIONARY_NAME, "desc auto complete", autoCompletelatch, steps);
		Future<List<String>> categorySuggestions = parametricSearchServiceHelper.getParametricCategoryForaAutoComplete(keyword, autoCompletelatch, steps);
		if(!autoCompletelatch.await(3, TimeUnit.SECONDS))
		{
			wrapper.setStatus(new Status(OperationMessages.NO_RESULT_FOUND, false));
			return wrapper;
		}
		List<String> partsResult = partsSugesstionFuture.get();
		List<String> nanPartsResult = partsNANSugesstionFuture.get();
		List<String> manResult = manSuggestionFuture.get();
		List<String> descResult = descSugesstionFuture.get();
		List<String> categoryResult = categorySuggestions.get();
		AutoCompleteResult result = new AutoCompleteResult();
		result.setDescResult(descResult);
		result.setManResult(manResult);
		partsResult.addAll(nanPartsResult);
		partsResult=partsResult.stream().distinct().limit(5).collect(Collectors.toList());
		result.setPartResult(partsResult);
		result.setCategoryResult(categoryResult);
		wrapper.setAutoCompleteResult(result);
		wrapper.setStatus(new Status(OperationMessages.SUCCESSFULL_OPERATION, true));
		if(filterDTO.isDebug())
		{
			wrapper.setSteps(steps);
		}

		return wrapper;
	}

	@Override
	public RestResponseWrapper facetMap(RequestParameters filterDTO) throws SolrServerException, IOException
	{
		RestResponseWrapper wrapper = new RestResponseWrapper();
		List<SearchStep> steps = new ArrayList<>();

		QueryResponse response = searchByComIdsForBOM(filterDTO, steps);
		if(util.hasNoResults(response))
		{
			wrapper.setStatus(new Status(OperationMessages.NO_RESULT_FOUND, false));
			return wrapper;
		}
		Map<String, Map<String, List<String>>> facetsWrapper = getFacetMap(response);

		wrapper.setFacetMap(facetsWrapper);
		wrapper.setStatus(new Status(OperationMessages.SUCCESSFULL_OPERATION, true));
		if(filterDTO.isDebug())
		{
			wrapper.setSteps(steps);
		}

		return wrapper;

	}

	@Override
	public RestResponseWrapper getKeywordSearch(RequestParameters filterDTO, boolean addSponserData) throws SolrServerException, IOException
	{
		logger.info("getKeywordSearch: and filterDTO =" + filterDTO);
		RestResponseWrapper wrapper = new RestResponseWrapper();
		List<SearchStep> steps = new ArrayList<>();

		logger.info("getKeywordSearch step zero  exact ");

		QueryResponse response = searchByPartNumber(filterDTO, false, addSponserData, steps);

		if(response == null)
		{
			return null;
		}

		if(util.hasNoResults(response))
		{
			logger.info("getKeywordSearch step zero return nothing so will move to step 1");

			response = getKeywordSearchQueryResponse(filterDTO, wrapper, true, addSponserData, steps);

			if(response == null)
			{
				return null;
			}

		}
		if(util.hasNoResults(response) && StringUtils.isNotEmpty(filterDTO.getRerankComIDs()))
		{
			response = searchByPartNumber(filterDTO, true, addSponserData, steps);

		}
		KeywordFacetsWrapper facetsWrapper = getKeywordFacets(response);

		SolrDocumentList solrDocuments = response.getResults();
		List<PartSearchResult> searchResults = mapKeywordResultList(solrDocuments, addSponserData, steps, filterDTO.getKeyword());

		wrapper.setTotalItems(solrDocuments.getNumFound());
		wrapper.setKeywordResults(searchResults);
		wrapper.setKeywordFacetsWrapper(facetsWrapper);
		wrapper.setStatus(new Status(OperationMessages.SUCCESSFULL_OPERATION, true));
		if(filterDTO.isDebug())
		{
			wrapper.setSteps(steps);
		}

		return wrapper;
	}

	private void addSortQuery(RequestParameters filterDTO, SolrQuery solrQuery)
	{

		String sortQuery = filterDTO.getOrder();
		if(StringUtils.isNotBlank(sortQuery))
		{
			String[] featuresSortsArray = sortQuery.split(",");
			for(String featureSort : featuresSortsArray)
			{
				String[] sortParameters = featureSort.split(":");
				String fetName = sortParameters[0];
				String sortType = "asc";
				if(sortParameters.length > 1 && sortParameters[1] != null)
				{
					sortType = sortParameters[1];
				}

				String sortField = getSolrSortField(fetName);
				if(sortField != null)
				{

					solrQuery.addSort(sortField, ORDER.valueOf(sortType.toLowerCase()));

				}
			}
		}
		boostResultByQuery(filterDTO.getRerankComIDs(), solrQuery);

	}

	private String getSolrSortField(String fetName)
	{

		if(fetName.equalsIgnoreCase("partnumber"))
		{
			return ConstantSolrFields.NAN_PARTNUM_EXACT;
		}

		if(fetName.equalsIgnoreCase(ConstantSolrFields.VENDOR) || fetName.equalsIgnoreCase("Manufacturer"))
		{
			return ConstantSolrFields.MAN_NAME_EXACT;
		}

		if(fetName.equalsIgnoreCase(ConstantSolrFields.ROHS))
		{
			return ConstantSolrFields.ROHS + "_RANK";
		}

		if(fetName.equalsIgnoreCase(ConstantSolrFields.LIFE_CYCLE) || fetName.equalsIgnoreCase("lifecycle"))
		{
			return ConstantSolrFields.LIFE_CYCLE + "_RANK";
		}

		return "";
	}

	private QueryResponse searchByPartNumber(RequestParameters filterDTO, boolean searchByComIds, boolean addSponserData, List<SearchStep> steps)
	{
		long start = System.currentTimeMillis();
		String searchWord = filterDTO.getKeyword();
		searchWord = util.removeSpecialCharacters(searchWord, true);
		searchWord = ClientUtils.escapeQueryChars(searchWord);

		try
		{
			// if(StringUtils.isNotEmpty(filterDTO.getAutocompleteSection())){
			// return getKeywordByAutoCompleteSelection(filterDTO,steps);
			// }
			if(StringUtils.isEmpty(searchWord))
			{
				return null;
			}
			Set<String> plIds = parametricSearchServiceHelper.getParametricCategory(searchWord, CetgoryLookupType.Main);

			StringBuilder nanQuery = new StringBuilder();

			if(searchByComIds)
			{
				nanQuery.append(" (  COM_ID:(").append(filterDTO.getRerankComIDs()).append(" ) )");
			}
			else
			{
				nanQuery.append(" (  NAN_PARTNUM_EXACT:").append(searchWord).append(filterDTO.isExact() ? "" : "*").append(")");
				if(plIds != null)
				{
					nanQuery.append(" OR ");
					nanQuery.append(util.generateOrQuery(ConstantSolrFields.PL_ID, plIds));

				}
				// if (subIds != null) {
				// nanQuery.append(" OR ");
				// nanQuery.append(util.generateOrQuery(ConstantSolrFields.SUB_CAT_IDS,
				// subIds));
				//
				// }
				// define query objects

			}

			// nanQuery.append(" ( NAN_PARTNUM_EXACT:").append(searchWord).append("* AND -IS_CUSTOM:1)");
			String filterationSolrQuery = getKeywordFilterQuery(filterDTO.getFilters());

			if(StringUtils.isNotEmpty(filterationSolrQuery))
			{

				nanQuery.append(" AND (");
				nanQuery.append(filterationSolrQuery).append(")");
			}
			SolrQuery query = new SolrQuery();

			String qStr1 = nanQuery.toString();
			// +dscQuery.toString()+") "
			query.set("q", qStr1);
			setupPaging(query, filterDTO.getPageSize(), filterDTO.getPageNumber());
			addRequiredFileds(query);
			addSortQuery(filterDTO, query);

			if(filterDTO.isBoostResults())
				query.addSort("strdist(\"" + ClientUtils.escapeQueryChars(filterDTO.getKeyword()) + "\",COM_PARTNUM,edit)", ORDER.desc);
			// if(addSponserData)
			// {
			// query.set("fq", util.generateOrQuery(ConstantSolrFields.MAN_ID, sponsers.keySet()));
			// }

			addFacetParameters(query);
			QueryResponse documents = partsSummarySolrServer.query(query);
			long serviceTime = (System.currentTimeMillis() - start);
			steps.add(new SearchStep("part number begin with", query.toString(), documents.getQTime(), serviceTime));
			logger.info("getKeywordSearch step zero took qtime = {},  and total is: {}", documents.getQTime(), documents.getElapsedTime());

			return documents;

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;

	}

	private QueryResponse searchByComIdsForBOM(RequestParameters filterDTO, List<SearchStep> steps)
	{
		long start = System.currentTimeMillis();

		try
		{

			StringBuilder comIdQuery = new StringBuilder();

			comIdQuery.append(" COM_ID:(").append(filterDTO.getRerankComIDs()).append(" ) ");

			String filterationSolrQuery = getKeywordFilterQuery(filterDTO.getFilters());

			SolrQuery query = new SolrQuery();
			query.set("fq", comIdQuery.toString());
			query.set("q", "*:*");
			if(StringUtils.isNotEmpty(filterationSolrQuery))
			{
				comIdQuery.setLength(0);
				comIdQuery.append("  (");
				comIdQuery.append(filterationSolrQuery).append(")");
				query.set("q", comIdQuery.toString());
			}

			setupPaging(query, filterDTO.getRerankComIDs().split(" ").length, filterDTO.getPageNumber());
			addRequiredFiledsForFacetMap(query);
			QueryResponse documents = partsSummarySolrServer.query(query, METHOD.POST);
			long serviceTime = (System.currentTimeMillis() - start);
			steps.add(new SearchStep("get data for BOM", query.toString(), documents.getQTime(), serviceTime));
			logger.info("get facests for BOM qtime = {},  and total is: {}", documents.getQTime(), documents.getElapsedTime());

			return documents;

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;

	}

	private void setupPaging(SolrQuery query, Integer pageSize, int pageNumber)
	{
		query.setRows(pageSize);
		query.setStart((pageNumber - 1) * pageSize);
	}

	private QueryResponse getKeywordByAutoCompleteSelection(RequestParameters filterDTO, List<SearchStep> steps) throws SolrServerException, IOException
	{
		StringBuilder queryBuilder = new StringBuilder(1024);
		switch(filterDTO.getAutocompleteSection()){
			case Constants.AUTOCOMPLETE_PART:
				queryBuilder.append(ConstantSolrFields.COM_PART_NUM).append(":").append("\"").append(filterDTO.getKeyword()).append("\"");
				break;
			case Constants.AUTOCOMPLETE_MAN:
				queryBuilder.append(ConstantSolrFields.MAN_NAME_EXACT).append(":").append("\"").append(filterDTO.getKeyword()).append("\"");
				break;
			case Constants.AUTOCOMPLETE_DESC:
				queryBuilder.append(ConstantSolrFields.PART_SUMMARY_DESCRIPTION).append(":").append("\"").append(filterDTO.getKeyword()).append("\"");
				break;
			case Constants.AUTOCOMPLETE_CAT:
				queryBuilder.append(ConstantSolrFields.COM_PART_NUM).append(":").append("\"").append(filterDTO.getKeyword()).append("\"");
				break;
		}
		SolrQuery query = new SolrQuery();

		// +dscQuery.toString()+") "
		query.set("q", queryBuilder.toString());
		setupPaging(query, filterDTO.getPageSize(), filterDTO.getPageNumber());
		addRequiredFileds(query);
		addSortQuery(filterDTO, query);

		if(filterDTO.isBoostResults())
			query.addSort("strdist(\"" + ClientUtils.escapeQueryChars(filterDTO.getKeyword()) + "\",COM_PARTNUM,edit)", ORDER.desc);
		// if(addSponserData)
		// {
		// query.set("fq", util.generateOrQuery(ConstantSolrFields.MAN_ID, sponsers.keySet()));
		// }

		addFacetParameters(query);
		QueryResponse documents = partsSummarySolrServer.query(query);

		steps.add(new SearchStep("part number begin with", query.toString(), documents.getQTime(), documents.getElapsedTime()));
		logger.info("getKeywordSearch step zero took qtime = {},  and total is: {}", documents.getQTime(), documents.getElapsedTime());

		return documents;
	}

	public String getKeywordFilterQuery(String filters)
	{
		StringBuilder query = new StringBuilder();
		List<FeatureDTO> selectedFilters = featureConverter.convertJSONToList(filters, FeatureDTO.class);
		if(selectedFilters != null && selectedFilters.size() == 1)
			selectedFilters = util.updateFilterDtoSeletedFilters(selectedFilters);
		if(selectedFilters != null && !selectedFilters.isEmpty())
		{
			String andJoiner = "";
			for(FeatureDTO feature : selectedFilters)
			{

				query.append(andJoiner);
				String hColName = util.getFetHColnameSolrQuery(feature);
				if(StringUtils.isEmpty(hColName))
				{
					continue;
				}
				query.append(hColName + ":");
				query.append("(");
				for(FeatureValueDTO value : feature.getValues())
				{
					if(hColName.toUpperCase().endsWith("_TOKEN"))
					{
						util.handleNumericValues(value);
						query.append(value.getValue());
					}
					else
					{
						query.append("\"");
						query.append(value.getValue());
						query.append("\"");
						query.append(" ");
					}

				}
				// query.deleteCharAt(query.length() - 1); // remove last
				// space before the )
				query.append(")");
				andJoiner = " AND ";

			}
		}

		return query.toString();
	}

	private void boostResultByQuery(String comIds, SolrQuery query)
	{
		if(StringUtils.isNotEmpty(comIds))
		{

			query.addSort("query($rx,0)", ORDER.desc);
			query.set("rx", "COM_ID:(" + comIds + ")");
		}
	}

	private void addRequiredFileds(SolrQuery query)
	{
		query.setFields(ConstantSolrFields.COM_ID, ConstantSolrFields.PASSIVE_CORE_PART_NUMBER, ConstantSolrFields.MAN_NAME, ConstantSolrFields.MAN_ID, ConstantSolrFields.PL_NAME, ConstantSolrFields.PART_SUMMARY_DESCRIPTION,
				ConstantSolrFields.LIFE_CYCLE_SUMMARY, ConstantSolrFields.DATASHEET_URL, ConstantSolrFields.ROHS, ConstantSolrFields.ROHS_VERSION, ConstantSolrFields.IMAGE_URL, ConstantSolrFields.NAN_PARTNUM_EXACT, ConstantSolrFields.MAN_ID,
				ConstantSolrFields.PL_ID);

	}

	private void addRequiredFiledsForFacetMap(SolrQuery query)
	{
		query.setFields(ConstantSolrFields.COM_ID, ConstantSolrFields.MAN_NAME, ConstantSolrFields.LIFE_CYCLE_SUMMARY, ConstantSolrFields.ROHS);

	}

	private boolean keyWordHasSpaces(String keyword)
	{
		keyword = keyword.trim();

		if(StringUtils.isEmpty(keyword) || util.removeSpecialCharacters(keyword, false).length() < 1)
		{
			return false;
		}

		if(StringUtils.isNotBlank(keyword))
		{
			String[] wordSplited = keyword.split("\\s+");

			if(wordSplited != null && wordSplited.length > 1)
			{
				return true;
			}
		}
		return false;
	}

	private List<PartSearchResult> mapKeywordResultList(SolrDocumentList solrDocuments, boolean addSponserData, List<SearchStep> steps, String keyword)
	{
		List<PartSearchResult> keywordResults = new LinkedList<>();

		PartSearchResult result = new PartSearchResult();
		result.setRequestedKeyword(keyword);
		List<PartSearchDTO> partResult = new LinkedList<>();
		result.setPartResult(partResult);
		keywordResults.add(result);
		for(SolrDocument solrDocument : solrDocuments)
		{
			String comId = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.PART_ID));
			String manName = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.MAN_NAME));
			String lifeCycle = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.LIFE_CYCLE_SUMMARY));
			String rohs = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.ROHS));
			if(StringUtils.isBlank(comId))
			{
				comId = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.PASSIVE_CORE_PART_ID));
			}
			String fullPart = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.PASSIVE_CORE_PART_NUMBER));
			String nanPart = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.NAN_PARTNUM_EXACT));

			String manId = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.MAN_ID));
			String plName = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.PL_NAME));
			String description = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.PART_SUMMARY_DESCRIPTION));

			String plId = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.PL_ID));

			String rohsVersion = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.ROHS_VERSION));
			String smallImage = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.IMAGE_URL));

			PartSearchDTO keywordResult = new PartSearchDTO();

			keywordResult.setComID(comId);
			keywordResult.setPartNumber(fullPart);
			keywordResult.setManufacturer(manName);
			keywordResult.setManufacturerId(manId);
			keywordResult.setPlName(plName);
			keywordResult.setDescription(description);
			keywordResult.setLifecycle(lifeCycle);
			keywordResult.setRohs(rohs);
			keywordResult.setRohsVersion(rohsVersion);
			keywordResult.setSmallImage(smallImage);
			keywordResult.setNanPartNumber(nanPart);
			keywordResult.setPlId(plId);
			keywordResult.setManufacturerId(manId);
			// if(addSponserData)
			// {
			// keywordResult.setPdfURL(pdfURL);
			// keywordResult.setMfrHomePage(stringOrEmpty(sponsers.get(manId)));
			//
			// }
			partResult.add(keywordResult);

		}
		// if(addSponserData && !keywordResults.isEmpty())
		// {
		// completeInventoryData(keywordResults, steps);
		// }
		return keywordResults;
	}

	// private void completeInventoryData(List<KeywordResult> keywordResults, List<SearchStep> steps)
	// {
	// long start = System.currentTimeMillis();
	// Map<String, KeywordResult> comIdstoPartsMap = new HashMap<>();
	// for(KeywordResult keyword : keywordResults)
	// {
	// comIdstoPartsMap.put(keyword.getComID(), keyword);
	// }
	// SolrQuery query = new SolrQuery(util.generateOrQuery(ConstantSolrFields.INV_COM_ID, comIdstoPartsMap.keySet()));
	// query.set("group", true);
	// query.set("rows", keywordResults.size());
	// query.set("group.limit", 10);
	// query.set("group.field", ConstantSolrFields.INV_COM_ID);
	// try
	// {
	// QueryResponse response = invSolrServer.query(query, METHOD.POST);
	// GroupResponse groupResponse = response.getGroupResponse();
	// if(groupResponse != null)
	// {
	// List<GroupCommand> groupCommands = groupResponse.getValues();
	// for(GroupCommand command : groupCommands)
	// {
	//
	// List<Group> groups = command.getValues();
	// for(Group group : groups)
	// {
	// SolrDocumentList documents = group.getResult();
	// KeywordResult keyword = comIdstoPartsMap.get(group.getGroupValue());
	// if(documents != null && documents.size() > 0)
	// {
	// for(SolrDocument document : documents)
	// {
	// String quantity = stringOrEmpty(document.getFieldValue(ConstantSolrFields.INV_QUANTITY));
	// String dist = stringOrEmpty(document.getFieldValue(ConstantSolrFields.INV_DEST_NAME));
	// String buyLink = stringOrEmpty(document.getFieldValue(ConstantSolrFields.INV_BUYNOW_LINK_DS));
	// InventoryDTO inventoryDTO = new InventoryDTO();
	// inventoryDTO.setBuyNowLink(buyLink);
	// inventoryDTO.setDistributor(dist);
	// inventoryDTO.setQuantity(quantity);
	// keyword.addInventoryEntry(inventoryDTO);
	// }
	// }
	// }
	// }
	// long serviceTime = System.currentTimeMillis() - start;
	// steps.add(new SearchStep("inventory fill step", query.toString(), response.getQTime(), serviceTime));
	// }
	// }
	// catch(SolrServerException | IOException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	private KeywordFacetsWrapper getKeywordFacets(QueryResponse response)
	{
		KeywordFacetsWrapper facetsWrapper = new KeywordFacetsWrapper();
		List<KeywordFacet> mans = new ArrayList<>();
		List<KeywordFacet> lifeCycle = new ArrayList<>();
		List<KeywordFacet> rohs = new ArrayList<>();
		List<KeywordFacet> mainCategories = new ArrayList<>();
		List<KeywordFacet> subCategories = new ArrayList<>();

		List<FacetField> facetFields = response.getFacetFields();
		if(facetFields == null)
		{
			return facetsWrapper;
		}
		for(FacetField facetField : facetFields)
		{
			List<Count> counts = facetField.getValues();
			List<KeywordFacet> loopFacet = null;

			switch(facetField.getName()){
				case ConstantSolrFields.MAN_NAME_EXACT:
					loopFacet = mans;
					facetsWrapper.setManufacturer(loopFacet);
					break;
				case ConstantSolrFields.LIFE_CYCLE_SUMMARY:
					loopFacet = lifeCycle;
					facetsWrapper.setLifeCycle(loopFacet);
					break;
				case ConstantSolrFields.ROHS:
					loopFacet = rohs;
					facetsWrapper.setRohs(loopFacet);
					break;
				case ConstantSolrFields.MAIN_CAT_NAMES:
					loopFacet = mainCategories;
					facetsWrapper.setMainCategories(loopFacet);
					break;
				case ConstantSolrFields.SUB_CAT_NAMES:
					loopFacet = subCategories;
					facetsWrapper.setSubCategories(loopFacet);
					break;
			}
			if(loopFacet == null)
			{
				return facetsWrapper;
			}
			for(Count count : counts)
			{
				KeywordFacet facet = new KeywordFacet(count.getName(), count.getCount());
				loopFacet.add(facet);
			}
		}

		return facetsWrapper;
	}

	private Map<String, Map<String, List<String>>> getFacetMap(QueryResponse response)
	{
		SolrDocumentList documents = response.getResults();
		Map<String, Map<String, List<String>>> facetMap = new HashMap<>();
		Map<String, List<String>> lcMap = new HashMap<>();
		Map<String, List<String>> rohsMap = new HashMap<>();
		Map<String, List<String>> manufacturerMap = new HashMap<>();
		facetMap.put(ConstantSolrFields.LIFE_CYCLE_SUMMARY, lcMap);
		facetMap.put(ConstantSolrFields.MAN_NAME, manufacturerMap);
		facetMap.put(ConstantSolrFields.ROHS, rohsMap);
		for(SolrDocument solrDocument : documents)
		{
			String comId = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.COM_ID));
			String manName = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.MAN_NAME));
			String lifeCycle = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.LIFE_CYCLE_SUMMARY));
			String rohs = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.ROHS));
			appendValueToFacetMap(lcMap, comId, lifeCycle);
			appendValueToFacetMap(rohsMap, comId, rohs);
			appendValueToFacetMap(manufacturerMap, comId, manName);

		}

		return facetMap;
	}

	private void appendValueToFacetMap(Map<String, List<String>> facetMap, String comId, String facetValue)
	{
		List<String> addedComIds = facetMap.get(facetValue);
		if(addedComIds == null)
		{
			addedComIds = new LinkedList<>();
			facetMap.put(facetValue, addedComIds);
		}
		addedComIds.add(comId);
	}

	private Map<String, List<Object>> createPivotList(List<PivotField> value)
	{
		Map<String, List<Object>> facetValueToComIdMap = new LinkedHashMap<>();
		for(PivotField pivotField : value)
		{
			List<Object> list = pivotField.getPivot().stream().map(pivot -> pivot.getValue()).collect(Collectors.toList());
			facetValueToComIdMap.put(pivotField.getValue().toString(), list);
		}

		return facetValueToComIdMap;
	}

	private QueryResponse getKeywordSearchQueryResponse(RequestParameters filterDTO, RestResponseWrapper wrapper, boolean firstSearch, boolean addSponserData, List<SearchStep> steps) throws SolrServerException, IOException
	{
		SolrQuery keywordSolrQuery = null;

		String mainOperator = " AND ";
		String internalOperator = " OR ";
		long startTime = System.currentTimeMillis();
		keywordSolrQuery = buildKeywordSearchQuerySolr(filterDTO, mainOperator, internalOperator, addSponserData, firstSearch);

		if(keywordSolrQuery == null)
		{
			return null;
		}

		// if(addSponserData)
		// {
		// keywordSolrQuery.set("fq", util.generateOrQuery(ConstantSolrFields.MAN_ID, sponsers.keySet()));
		// }

		addRequiredFileds(keywordSolrQuery);
		addFacetParameters(keywordSolrQuery);
		addSortQuery(filterDTO, keywordSolrQuery);
		QueryResponse response = partsSummarySolrServer.query(keywordSolrQuery, METHOD.POST);

		steps.add(new SearchStep("keyword anding step", keywordSolrQuery.toString(), response.getQTime(), response.getElapsedTime()));
		if(util.hasNoResults(response) && firstSearch && !addSponserData)
		{
			// response = searchInPassiveCore(filterDTO);
		}
		if(util.hasNoResults(response) && keyWordHasSpaces(filterDTO.getKeyword()))
		{
			// STEP 2 : AND between fields and ANR between internal items
			startTime = System.currentTimeMillis();
			mainOperator = " OR ";
			keywordSolrQuery = buildKeywordSearchQuerySolr(filterDTO, mainOperator, internalOperator, addSponserData, firstSearch);
			if(keywordSolrQuery == null)
			{
				return null;
			}
			addRequiredFileds(keywordSolrQuery);
			addFacetParameters(keywordSolrQuery);
			addSortQuery(filterDTO, keywordSolrQuery);
			response = partsSummarySolrServer.query(keywordSolrQuery, METHOD.POST);

			steps.add(new SearchStep("keyword oring step", keywordSolrQuery.toString(), response.getQTime(), response.getElapsedTime()));
		}

		if(util.hasNoResults(response) && StringUtils.isEmpty(filterDTO.getRerankComIDs()) && util.canDropOneCharacterFromLast(filterDTO.getKeyword(), keywordDropLimit))
		{
			// STEP 3: Cut character one by one
			logger.info("STEP 3: Cut character one by one");

			String joinedTerms = util.dropOneCharacterFromLast(filterDTO.getKeyword(), keywordDropLimit);
			// Start function again
			filterDTO.setKeyword(joinedTerms);

			logger.info("STEP 3: joinedTerms = " + joinedTerms);
			return getKeywordSearchQueryResponse(filterDTO, wrapper, false, addSponserData, steps);
		}

		wrapper.setKeywordOperator(mainOperator);
		return response;
	}

	// private QueryResponse searchInPassiveCore(FilterDTO filterDTO) throws SolrServerException, IOException
	// {
	//
	// String keyword = util.removeSpecialCharacters(filterDTO.getKeyword().trim(), true);
	// logger.info("searchInPassiveCore keyword=" + keyword);
	//
	// if(StringUtils.isBlank(keyword) || keyword.length() < 1)
	// {
	// return null;
	// }
	//
	// SolrQuery query = new SolrQuery("NAN_PARTNUM:" + keyword);
	//
	// if("Manufacturer".equals(filterDTO.getSortQuery()))
	// {
	// parametricSearchService.addSortQuery(filterDTO, query, null, ConstantSolrFields.PASSIVE_CORE_MAN_NAME);
	// }
	//
	// // query.setFacet(true);
	// // query.setFacetSort(FacetParams.FACET_SORT_COUNT);
	// // query.setFacetLimit(-1);
	// // query.setFacetMinCount(1);
	// //
	// // query.addFacetField(ConstantSolrFields.MAN_NAME);
	//
	// query.set("rows", filterDTO.getPageSize());
	// query.set("start", (filterDTO.getStartPage() - 1) * filterDTO.getPageSize());
	//
	// logger.info("Will query passive core now");
	// logger.info("Will query solr now by query=\t" + query.toString());
	// query.setFields(ConstantSolrFields.PASSIVE_CORE_PART_ID, ConstantSolrFields.PASSIVE_CORE_PART_NUMBER, ConstantSolrFields.MAN_NAME,
	// ConstantSolrFields.MAN_ID, ConstantSolrFields.PL_NAME, ConstantSolrFields.PASSIVE_CORE_DESCRIPTION);
	// QueryResponse response = passiveSolrServer.query(query, METHOD.POST);
	// return response;
	//
	// }

	private SolrQuery buildKeywordSearchQuerySolr(RequestParameters filterDTO, String mainOperator, String internalOperator, boolean addSponserData, boolean firstSearch)
	{
		// replace dash with spaces in case the user separated the terms with
		// dashes
		String keyword = filterDTO.getKeyword().trim();// .replaceAll("-", " ");
		StringBuilder queryStringBuilder = new StringBuilder("");

		String keywordQuery = util.buildKeywordSearchQueryString(keyword, filterDTO.getAutocompleteSection(), mainOperator, internalOperator, firstSearch, ConstantSolrFields.PART_SUMMARY_DESCRIPTION);

		if(StringUtils.isEmpty(keywordQuery))
		{
			return null;
		}

		queryStringBuilder.append(keywordQuery);
		String filterationSolrQuery = getKeywordFilterQuery(filterDTO.getFilters());

		if(StringUtils.isNotEmpty(filterationSolrQuery))
		{

			queryStringBuilder.append(" AND (");
			queryStringBuilder.append(filterationSolrQuery).append(")");
		}
		SolrQuery query = new SolrQuery(queryStringBuilder.toString());

		// util.appendShardQuery(query);

		query.setRows(filterDTO.getPageSize());
		query.setStart((filterDTO.getPageNumber() - 1) * filterDTO.getPageSize());
		// TODO add start from input parameters
		if(filterDTO.isBoostResults())
			query.addSort("strdist(\"" + ClientUtils.escapeQueryChars(filterDTO.getKeyword().split(" ")[0]) + "\",COM_PARTNUM,edit)", ORDER.desc);
		// parametricSearchService.addSortQuery(filterDTO, query, null);
		// if(addSponserData)
		// {
		// query.set("fq", util.generateOrQuery(ConstantSolrFields.MAN_ID, sponsers.keySet()));
		// }

		return query;
	}

	private void addFacetParameters(SolrQuery query)
	{
		query.setFacet(true);
		query.setFacetSort(FacetParams.FACET_SORT_COUNT);
		query.setFacetLimit(-1);
		query.setFacetMinCount(1);
		util.addFacetMethod(query);
		query.addFacetField(ConstantSolrFields.MAN_NAME_EXACT);
		query.addFacetField(ConstantSolrFields.LIFE_CYCLE_SUMMARY);
		query.addFacetField(ConstantSolrFields.ROHS);
		// query.addFacetField(ConstantSolrFields.MAIN_CAT_NAMES);
		// query.addFacetField(ConstantSolrFields.SUB_CAT_NAMES);
	}

	private void addFacetPivotParameters(SolrQuery query)
	{
		query.setFacet(true);
		query.setFacetSort(FacetParams.FACET_SORT_COUNT);
		query.setFacetLimit(-1);
		query.setFacetMinCount(1);
		util.addFacetMethod(query);
		query.addFacetPivotField(ConstantSolrFields.MAN_NAME_EXACT + "," + ConstantSolrFields.COM_ID);
		query.addFacetPivotField(ConstantSolrFields.LIFE_CYCLE_SUMMARY + "," + ConstantSolrFields.COM_ID);
		query.addFacetPivotField(ConstantSolrFields.ROHS + "," + ConstantSolrFields.COM_ID);
		// query.addFacetField(ConstantSolrFields.MAIN_CAT_NAMES);
		// query.addFacetField(ConstantSolrFields.SUB_CAT_NAMES);
	}

	private String stringOrEmpty(Object obj)
	{
		return (obj == null) ? "" : obj.toString();
	}

}
