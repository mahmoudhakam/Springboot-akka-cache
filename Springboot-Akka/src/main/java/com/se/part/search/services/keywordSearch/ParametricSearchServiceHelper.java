package com.se.part.search.services.keywordSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.dto.keyword.parametric.FeatureDTO;
import com.se.part.search.dto.keyword.parametric.FeatureValueDTO;
import com.se.part.search.dto.keyword.parametric.FilterDTO;
import com.se.part.search.dto.keyword.parametric.MainCategoryDTO;
import com.se.part.search.dto.keyword.parametric.PLTypeDTO;
import com.se.part.search.dto.keyword.parametric.ParametricSearchResultDTO;
import com.se.part.search.dto.keyword.parametric.ProductLineDTO;
import com.se.part.search.dto.keyword.parametric.SearchStep;
import com.se.part.search.dto.keyword.parametric.SubCategoryDTO;
import com.se.part.search.util.ConstantSolrFields;

@Service
public class ParametricSearchServiceHelper
{
	private static final int MAX_FEATURES_TO_RETURN = 4;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SolrClient taxonomySolrServer;
	@Autowired
	private Util<?> util;

	private Map<String, Set<String>> categoryToPlIdMap;
	private Map<String, Set<String>> categorySearchMap;
	private Map<String, Set<String>> subCategoryIdMap;
	private Map<String, Set<String>> PlIdMap;
	@Value("#{environment['part.keyword.drop.limit']}")
	Integer keywordDropLimit;

	private final Set<String> notParametricFeaturesLower = new HashSet<String>();

	@PostConstruct
	private void init() throws IOException, SolrServerException
	{
		categoryToPlIdMap = new HashMap<>();
		subCategoryIdMap = new HashMap<>();
		categorySearchMap = new HashMap<>();
		PlIdMap = new HashMap<>();
		getAllPlTypes();
	}

	@Async
	public Future<List<String>> getParametricCategoryForaAutoComplete(String categoyryName, CountDownLatch latch, List<SearchStep> steps)
	{
		Set<String> data = new HashSet<>();
		List<String> result = new ArrayList<>();
		long startTime = System.currentTimeMillis();
		try
		{
			String searchCargory = util.removeSpecialCharacters(categoyryName, true).toLowerCase();

			if(categorySearchMap == null)
			{
				return null;
			}
			for(String category : categorySearchMap.keySet())
			{
				if(category.contains(searchCargory))
				{
					data.addAll(categorySearchMap.get(category));
					if(data.size() >= 5)
					{
						result.addAll(data);
						result = result.subList(0, 5);
						break;
					}
				}
			}
			if(result.isEmpty())
			{

				result.addAll(data);
			}
		}
		finally
		{
			latch.countDown();

			steps.add(new SearchStep("category auto complete step", "", (System.currentTimeMillis() - startTime), (System.currentTimeMillis() - startTime)));
		}

		return new AsyncResult<List<String>>(result);

	}

	public Set<String> getParametricCategory(String categoyryName, CetgoryLookupType lookupType)
	{
		Map<String, Set<String>> searchMap = null;
		String searchCargory = util.removeSpecialCharacters(categoyryName, true).toLowerCase();
		switch(lookupType){
			case Main:
				searchMap = categoryToPlIdMap;
				break;
			case Sub:
				searchMap = subCategoryIdMap;
				break;
			default:
				searchMap = PlIdMap;
				break;
		}
		if(searchMap == null)
		{
			return null;
		}
		for(String category : searchMap.keySet())
		{
			if(category.equals(searchCargory))
			{
				return searchMap.get(category);
			}
		}
		return null;

	}

	public Map<String, PLTypeDTO> getAllPlTypes() throws IOException, SolrServerException
	{

		SolrQuery query = new SolrQuery();
		StringBuilder builder = new StringBuilder("*:*");

		query.setQuery(builder.toString());
		query.setRows(0);
		QueryResponse response = taxonomySolrServer.query(query, METHOD.POST);
		// query.setRows(Integer.parseInt(env.getProperty(Constants.MAX_TAXONOMY_ROWS)));
		if(response.getResults() != null)
			query.setRows((int) response.getResults().getNumFound());
		Map<String, PLTypeDTO> plTypes = new LinkedHashMap<String, PLTypeDTO>();
		// query.addSort(ConstantSolrFields.TAX_PL_TYPE_SORT, ORDER.asc);
		// query.addSort(ConstantSolrFields.TAX_MAIN_NAME, ORDER.asc);
		// query.addSort(ConstantSolrFields.TAX_SUB_NAME, ORDER.asc);
		// query.addSort(ConstantSolrFields.TAX_PL_NAME, ORDER.asc);
		logger.info("Will query solr now by query=\t" + query.toString());
		response = taxonomySolrServer.query(query, METHOD.POST);

		for(SolrDocument document : response.getResults())
		{

			String plType = (String) document.getFieldValue(ConstantSolrFields.TAX_PL_TYPE_NAME);
			String mainCategory = (String) document.getFieldValue(ConstantSolrFields.TAX_MAIN_NAME);
			String mainId = (String) document.getFieldValue(ConstantSolrFields.TAX_MAIN_ID);
			String subCategory = (String) document.getFieldValue(ConstantSolrFields.TAX_SUB_NAME);
			String subId = (String) document.getFieldValue(ConstantSolrFields.TAX_SUB_ID);
			String plName = (String) document.getFieldValue(ConstantSolrFields.TAX_PL_NAME);
			String plId = (String) document.getFieldValue(ConstantSolrFields.TAX_PL_ID);
			String subSearchableString = (String) document.getFieldValue(ConstantSolrFields.TAX_SUBSEARCHABLE_FLAG);
			String mainSearchableString = (String) document.getFieldValue(ConstantSolrFields.TAX_MAINSEARCHABLE_FLAG);

			boolean subSearchable = (subSearchableString != null && subSearchableString.equals("1"));
			boolean mainSearchable = (mainSearchableString != null && mainSearchableString.equals("1"));
			if(mainCategory.equalsIgnoreCase("others") || subCategory.equalsIgnoreCase("others") || plName.equalsIgnoreCase("others"))
			{
				continue;
			}

			PLTypeDTO plTypeDTO = plTypes.get(plType);
			if(plTypeDTO == null)
			{
				plTypeDTO = new PLTypeDTO();
				plTypeDTO.setPlType(plType);
			}

			MainCategoryDTO mainCategoryDTO = new MainCategoryDTO();
			mainCategoryDTO.setMainCategory(mainCategory);
			mainCategoryDTO.setMainId(mainId);

			if(plTypeDTO.getMainCategoryList().contains(mainCategoryDTO))
			{
				mainCategoryDTO = plTypeDTO.getMainCategoryList().get(plTypeDTO.getMainCategoryList().indexOf(mainCategoryDTO));
			}
			mainCategoryDTO.setSearchableAsBase(mainSearchable);
			SubCategoryDTO subCategoryDTO = new SubCategoryDTO();
			subCategoryDTO.setSubName(subCategory);
			subCategoryDTO.setSubID(subId);
			if(mainCategoryDTO.getSubCategoryDTOs().contains(subCategoryDTO))
			{
				subCategoryDTO = mainCategoryDTO.getSubCategoryDTOs().get(mainCategoryDTO.getSubCategoryDTOs().indexOf(subCategoryDTO));
			}
			String mainCategorySearch = util.removeSpecialCharacters(mainCategory, true).toLowerCase();
			String subCatgoruSearch = util.removeSpecialCharacters(subCategory, true).toLowerCase();
			String plSearch = util.removeSpecialCharacters(plName, true).toLowerCase();
			// auto complete setup
			Set<String> mainNames = categorySearchMap.get(mainCategorySearch);
			if(mainNames == null)
			{
				mainNames = new HashSet<>();
			}
			Set<String> subNames = categorySearchMap.get(subCatgoruSearch);
			if(subNames == null)
			{
				subNames = new HashSet<>();
			}
			Set<String> plIds = PlIdMap.get(plSearch);
			if(plIds == null)
			{
				plIds = new HashSet<>();
			}
			plIds.add(plId);
			subNames.add(subCategory);
			mainNames.add(mainCategory);

			if(!categorySearchMap.containsKey(mainCategorySearch))
			{
				addCategorySeparatedNames(categorySearchMap, mainCategory, mainCategorySearch);
			}
			categorySearchMap.put(mainCategorySearch, mainNames);
			if(!categorySearchMap.containsKey(subCatgoruSearch))
			{
				addCategorySeparatedNames(categorySearchMap, subCategory, subCatgoruSearch);
			}
			categorySearchMap.put(subCatgoruSearch, subNames);

			Set<String> mainIds = categoryToPlIdMap.get(mainCategorySearch);
			Set<String> subIds = categoryToPlIdMap.get(subCatgoruSearch);
			if(mainIds == null)
			{
				mainIds = new HashSet<>();

			}
			if(subIds == null)
			{
				subIds = new HashSet<>();

			}
			mainIds.add(plId);
			subIds.add(plId);

			// if(!categoryToPlIdMap.containsKey(mainCategorySearch)){
			addCategorySeparatedWords(categoryToPlIdMap, mainCategory, plId);
			// }
			categoryToPlIdMap.put(mainCategorySearch, mainIds);
			// if(!categoryToPlIdMap.containsKey(subCatgoruSearch)){
			addCategorySeparatedWords(categoryToPlIdMap, subCategory, plId);
			// }
			categoryToPlIdMap.put(subCatgoruSearch, subIds);
			PlIdMap.put(plSearch, mainIds);
			subCategoryDTO.setSearchableAsBase(subSearchable);
			ProductLineDTO plDTO = new ProductLineDTO();
			plDTO.setPlID(plId);
			plDTO.setPlName(plName);
			if(!subCategoryDTO.getProductLineList().contains(plDTO))
			{
				subCategoryDTO.getProductLineList().add(plDTO);
			}

			if(!plTypeDTO.getMainCategoryList().contains(mainCategoryDTO))
			{
				plTypeDTO.getMainCategoryList().add(mainCategoryDTO);

			}
			if(!mainCategoryDTO.getSubCategoryDTOs().contains(subCategoryDTO))
			{
				mainCategoryDTO.getSubCategoryDTOs().add(subCategoryDTO);
			}
			plTypes.put(plType, plTypeDTO);

		}
		return plTypes;
	}

	private void addCategorySeparatedNames(Map<String, Set<String>> catgoryIDMap, String category, String categoryId)
	{
		String[] categoriesSplit = category.split(" ");
		if(categoriesSplit.length > 1)
		{
			for(String categoryWord : categoriesSplit)
			{
				String searchcategoryName = util.removeSpecialCharacters(categoryWord, true).toLowerCase();
				Set<String> addedIds = catgoryIDMap.get(searchcategoryName);
				if(addedIds == null)
				{
					addedIds = new HashSet<>();
				}
				addedIds.add(category);
				catgoryIDMap.put(searchcategoryName, addedIds);
			}
		}

	}

	private void addCategorySeparatedWords(Map<String, Set<String>> catgoryIDMap, String category, String plId)
	{
		String[] categoriesSplit = category.split(" ");

		if(categoriesSplit.length > 1)
		{
			for(String categoryWord : categoriesSplit)
			{
				String searchcategoryName = util.removeSpecialCharacters(categoryWord, true).toLowerCase();
				Set<String> addedIds = catgoryIDMap.get(searchcategoryName);
				if(addedIds == null)
				{
					addedIds = new HashSet<>();
				}
				addedIds.add(plId);
				catgoryIDMap.put(searchcategoryName, addedIds);
			}
		}

	}

	public List<FeatureDTO> getPlFeatures(FilterDTO filterDTO) throws SolrServerException, IOException
	{
		List<FeatureDTO> features = new ArrayList<FeatureDTO>();
		features.addAll(util.createKeywordFeatures());
		SolrQuery query = getPlFeaturesSolrQuery(filterDTO);

		logger.info("Will query solr now by query=\t" + query.toString());

		QueryResponse response = taxonomySolrServer.query(query, METHOD.POST);

		GroupResponse groupResponse = response.getGroupResponse();
		List<GroupCommand> groupCommands = groupResponse.getValues();
		for(GroupCommand command : groupCommands)
		{
			for(Group group : command.getValues())
			{
				for(SolrDocument document : group.getResult())
				{
					FeatureDTO currentFeature = new FeatureDTO();

					/* @formatter:off */
					currentFeature.setFetName(mapTaxonomyFeatureName(document));
					currentFeature.setFeatureDefinition(
							(String) document.getFieldValue(ConstantSolrFields.TAX_FET_DEFINITION));
					currentFeature.setHcolName((String) document.getFieldValue(ConstantSolrFields.TAX_FET_HCOLNAME));
					currentFeature.setPackageFlag((String) document.getFieldValue(ConstantSolrFields.TAX_PACKAGE_FLAG));
					currentFeature.setUnit((String) document.getFieldValue(ConstantSolrFields.TAX_FET_UNIT));
					currentFeature.setSortType((String) document.getFieldValue(ConstantSolrFields.TAX_SORT_TYPE));
					currentFeature.setFetId((String) document.getFieldValue(ConstantSolrFields.TAX_FET_ID));
					currentFeature.setSubName((String) document.getFieldValue(ConstantSolrFields.TAX_SUB_NAME));
					currentFeature.setMainName((String) document.getFieldValue(ConstantSolrFields.TAX_MAIN_NAME));
					/* @formatter:on */

					features.add(currentFeature);
				}
			}
		}
		Set<String> subIds = new HashSet<>();
		Set<String> mainNames = new HashSet<>();

		List<FacetField> facetFields = response.getFacetFields();
		for(FacetField facetField : facetFields)
		{
			List<Count> facetCount = facetField.getValues();
			if(facetCount != null)
			{
				if(ConstantSolrFields.TAX_SUB_ID.equals(facetField.getName()))
				{
					for(Count count : facetField.getValues())
					{
						subIds.add(count.getName());
					}
				}
				else if(ConstantSolrFields.TAX_MAIN_NAME.equals(facetField.getName()))
				{
					for(Count count : facetField.getValues())
					{
						mainNames.add(count.getName());
					}
				}
			}
		}
		filterDTO.setSubIds(subIds);
		filterDTO.setMainNames(mainNames);
		return features;
	}

	private String mapTaxonomyFeatureName(SolrDocument document)
	{
		String taxFetName = (String) document.getFieldValue(ConstantSolrFields.TAX_FET_NAME);
		if("vendor".equalsIgnoreCase(taxFetName))
		{
			return "Manufacturer";
		}
		else if("rohs".equalsIgnoreCase(taxFetName))
		{
			return "ROHS";
		}
		else if("lifecycle".equalsIgnoreCase(taxFetName))
		{
			return "LifeCycle";
		}
		return taxFetName;
	}

	private SolrQuery getPlFeaturesSolrQuery(FilterDTO filterDTO)
	{
		String category = filterDTO.getCategoryName();
		String mainCategoryName = filterDTO.getMainCategoryName();
		Integer level = filterDTO.getLevel();

		String queryString = getFilterquery(category, filterDTO.getCategoryId(), level, filterDTO.isSheetView());

		queryString += " AND -FEATURENAME:(ROHS \"Life Cycle\" Vendor)";
		if(StringUtils.isNotBlank(mainCategoryName))
		{
			queryString += " AND " + ConstantSolrFields.TAX_MAIN_NAME + ":\"" + mainCategoryName + "\"";
		}

		if(filterDTO.isSheetView())
		{
			queryString += " AND SHEETVIEWFLAG:(1 3) ";// AND PACKAGEFLAG:0
		}
		SolrQuery query = new SolrQuery(queryString);
		query.setFacet(true);
		query.addFacetField(ConstantSolrFields.TAX_SUB_ID);
		query.addFacetField(ConstantSolrFields.TAX_SUB_NAME);
		query.addFacetField(ConstantSolrFields.TAX_MAIN_NAME);
		query.setFacetLimit(-1);
		query.setFacetMinCount(1);

		query.set("group", true);
		query.set("group.field", ConstantSolrFields.TAX_FET_NAME);
		query.setSort(getPlFeaturesSortField(filterDTO), SolrQuery.ORDER.asc);

		if(filterDTO.isSheetView())
		{
			query.setRows(Integer.MAX_VALUE);
		}
		else
		{
			query.setRows(MAX_FEATURES_TO_RETURN);
		}

		return query;
	}

	private String getFilterquery(String category, String categoryId, int level, boolean b)
	{
		switch(level){
			case 1:
				if(categoryId != null)
				{
					return "MAINID:\"" + categoryId + "\" AND MAINSEARCHABLEFLAG:\"1\"";
				}
				return "MAINNAME:\"" + category + "\" AND MAINSEARCHABLEFLAG:\"1\"";
			case 2:
				if(categoryId != null)
				{
					return "SUBID:\"" + categoryId + "\" AND SUBSEARCHABLEFLAG:\"1\"";
				}
				return "SUBNAME:\"" + category + "\" AND SUBSEARCHABLEFLAG:\"1\"";
			case 3:
				String searchableFlag = b ? "" : " AND PLSEARCHABLEFLAG:\"1\"";
				if(categoryId != null)
				{

					return "PLID:\"" + categoryId + "\"" + searchableFlag;
				}
				return "PLNAME:\"" + category + "\" " + searchableFlag;
			default:
				return "PLNAME:\"" + category + "\" AND PLSEARCHABLEFLAG:\"1\"";
		}
	}

	private String getPlFeaturesSortField(FilterDTO filterDTO)
	{
		int level = filterDTO.getLevel();
		if(level == 3)
		{
			return ConstantSolrFields.TAX_DISPLAY_ORDER;
		}
		else if(level == 2)
		{
			return ConstantSolrFields.TAX_SUB_CATEGORY_DISPLAY_ORDER;
		}
		return ConstantSolrFields.TAX_MAIN_CATEGORY_DISPLAY_ORDER;
	}

	public SolrQuery getParametricSolrQuery(List<FeatureDTO> fullFeaturesFromSolr, FilterDTO filterDTO)
	{
		StringBuilder queryBuilder = new StringBuilder();

		// Either a parametric search with category, or a keyword search. In
		// case none, return null.
		if(StringUtils.isNotEmpty(filterDTO.getCategoryName()))
		{
			String categoryIdQuery = generateCategoryIdQuery(filterDTO);
			// @formatter:off
			// Append And because the main q is the pl_name or pl_id AND
			// anything else
			// @formatter:on
			queryBuilder.append(categoryIdQuery);

		}
		else if(StringUtils.isEmpty(filterDTO.getKeyword()))
		{
			return null;
		}

		List<FeatureDTO> selectedFilters = filterDTO.getSelectedFilters();

		if(selectedFilters != null)
		{
			util.fillFeatureDTOsFields(selectedFilters, fullFeaturesFromSolr);
		}

		String selectedFiltersQuery = getParametricSolrQueryFromSelectedFilters(selectedFilters, fullFeaturesFromSolr);

		appendToSolrQuery(queryBuilder, selectedFiltersQuery);
		appendToSolrQuery(queryBuilder, getSolrKeywordQuery(filterDTO));
		appendToSolrQuery(queryBuilder, getPartIdsToQuery(filterDTO.getPartIds()));

		SolrQuery solrQuery = new SolrQuery(queryBuilder.toString());

		addSortQuery(filterDTO, solrQuery, fullFeaturesFromSolr, null);

		solrQuery.setRows(filterDTO.getPageSize());
		solrQuery.setStart((filterDTO.getStartPage() - 1) * filterDTO.getPageSize());
		return solrQuery;
	}

	private void appendToSolrQuery(StringBuilder queryBuilder, String currentStepQuery)
	{
		if(StringUtils.isNotBlank(currentStepQuery))
		{
			if(queryBuilder.length() > 0)
			{

				queryBuilder.append(" AND ");
			}
			queryBuilder.append(currentStepQuery);
		}
	}

	private String generateCategoryIdQuery(FilterDTO filterDTO)
	{
		int level = filterDTO.getLevel();
		String category = filterDTO.getCategoryName();
		String categoryId = filterDTO.getCategoryId();
		String mainCategoryName = filterDTO.getMainCategoryName();

		// @formatter:off
		switch (level) {
		case 1:
			if (categoryId != null) {
				return ConstantSolrFields.MAIN_CAT_IDS + ":\"" + categoryId
						+ "\"" /*
								 * + " AND " +
								 * util.generateOrQuery(ConstantSolrFields.
								 * SUB_CAT_IDS, filterDTO.getSubIds())
								 */;
			}
			return ConstantSolrFields.MAIN_CAT_PARAMETRIC + ":\"" + category
					+ "\"" /*
							 * + " AND " +
							 * util.generateOrQuery(ConstantSolrFields.
							 * SUB_CAT_IDS, filterDTO.getSubIds())
							 */;
		case 2:
			if (categoryId != null) {
				return util.generateOrQuery(ConstantSolrFields.SUB_CAT_IDS, filterDTO.getSubIds());
			}
			return util.generateOrQuery(ConstantSolrFields.SUB_CAT_IDS, filterDTO.getSubIds()) + " AND "
					+ ConstantSolrFields.MAIN_CAT_PARAMETRIC + ":\"" + mainCategoryName + "\"";
		case 3:
			if (categoryId != null) {
				return ConstantSolrFields.PL_ID + ":\"" + categoryId + "\"";
			}
			return ConstantSolrFields.PL_NAME + ":\"" + category + "\"";
		default:
			return ConstantSolrFields.PL_ID + ":\"" + category + "\"";
		}
		// @formatter:ond
	}

	public String getParametricSolrQueryFromSelectedFilters(List<FeatureDTO> selectedFilters, List<FeatureDTO> fullFeaturesFromSolr)
	{
		StringBuilder query = new StringBuilder();
		if(selectedFilters != null && selectedFilters.size() == 1)
			selectedFilters = util.updateFilterDtoSeletedFilters(selectedFilters);
		if(selectedFilters != null && !selectedFilters.isEmpty())
		{
			String andJoiner = "";
			for(FeatureDTO feature : selectedFilters)
			{

				if(fullFeaturesFromSolr.contains(feature) || feature.getFetName().equalsIgnoreCase("hasfootprint") || feature.getFetName().equalsIgnoreCase("footprint") || feature.getFetName().equalsIgnoreCase("Manufacturer_name"))
				{
					query.append(andJoiner);
					String hColName = util.getFetHColnameSolrQuery(feature);
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
		}

		return query.toString();
	}

	public void addSortQuery(FilterDTO filterDTO, SolrQuery solrQuery, List<FeatureDTO> fullFeaturesFromSolr, String staticSortField)
	{
		String originalSearchWord = filterDTO.getKeyword();

		String[] keywords = originalSearchWord != null ? originalSearchWord.split(" ") : null;
		String sortQuery = filterDTO.getSortQuery();
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

				String sortField = getSolrSortField(fetName, null, fullFeaturesFromSolr, staticSortField);
				if(sortField != null)
				{
					if(sortField.contains("_SORT,"))
					{
						String[] sortFieldArray = sortField.split(",");
						String sortField_1 = sortFieldArray[0];
						String sortField_2 = sortFieldArray[1];
						solrQuery.addSort(sortField_1, ORDER.valueOf(sortType.toLowerCase()));
						solrQuery.addSort(sortField_2, ORDER.valueOf(sortType.toLowerCase()));
					}
					else
					{
						solrQuery.addSort(sortField, ORDER.valueOf(sortType.toLowerCase()));
					}
				}
			}
		}

		solrQuery.addSort(ConstantSolrFields.MAN_ORDER, ORDER.asc);
		// if (originalSearchWord != null && StringUtils.isBlank(sortQuery) &&
		// keywords != null && keywords.length == 1)
		// solrQuery.addSort("strdist(\"" + originalSearchWord +
		// "\",FULL_PART,edit)", ORDER.desc);

	}

	/** Part IDs is used by getParametricPartDetailsSearchResults **/
	private String getPartIdsToQuery(List<String> partIds)
	{
		if(partIds == null || partIds.isEmpty())
		{
			return "";
		}

		StringBuilder queryBuilder = new StringBuilder(" PART_ID:(");
		for(String partId : partIds)
		{
			queryBuilder.append(partId).append(" ");
		}
		queryBuilder.append(")");
		return queryBuilder.toString();
	}

	private String getSolrSortField(String fetName, String hcolName, List<FeatureDTO> fullFeaturesFromSolr, String staticSortField)
	{

		if(fetName.equalsIgnoreCase(Constants.PRODUCT_LINE) || fetName.equalsIgnoreCase(Constants.PRODUCTLINENAME) || fetName.equalsIgnoreCase(Constants.PRODUCT_LINE_NAME))
		{
			return ConstantSolrFields.PL_NAME;
		}

		if(fetName.equalsIgnoreCase(Constants.FULL_PART) || fetName.equalsIgnoreCase("partnumber"))
		{
			return ConstantSolrFields.COM_PART_NUM;
		}
		if(fetName.equalsIgnoreCase(Constants.AVAILABILITY) || fetName.equalsIgnoreCase(Constants.INVENTORY))
		{
			return ConstantSolrFields.INV_COUNT_ORIGINAL;
		}

		if(fetName.equalsIgnoreCase("Manufacturer") && StringUtils.isNotBlank(staticSortField))
		{
			return staticSortField;
		}

		if(fetName.equalsIgnoreCase(ConstantSolrFields.VENDOR) || fetName.equalsIgnoreCase(Constants.MFR_NAME) || fetName.equalsIgnoreCase("Manufacturer"))
		{
			return ConstantSolrFields.MAN_NAME;
		}

		if(fetName.equalsIgnoreCase(ConstantSolrFields.ROHS) || fetName.equalsIgnoreCase(Constants.EU_ROHS))
		{
			return ConstantSolrFields.ROHS + "_RANK";
		}

		if(fetName.equalsIgnoreCase(Constants.LIFE_CYCLE_FET_NAME) || fetName.equalsIgnoreCase(Constants.PART_STATUS) || fetName.equalsIgnoreCase("lifecycle"))
		{
			return ConstantSolrFields.LIFE_CYCLE + "_RANK";
		}

		if(fetName.equalsIgnoreCase(Constants.PART_RATING))
		{
			return ConstantSolrFields.STAR_RATING;
		}

		if(fetName.equalsIgnoreCase(Constants.DESCRIPTION))
		{
			return ConstantSolrFields.PART_DESCRIPTION;
		}

		if(hcolName == null)
		{
			hcolName = getHcolNameByFeatureNameIgnoreCase(fetName, fullFeaturesFromSolr);
		}
		// Will add , to check later if , exists will sort by _SORT and also
		// _VALUE to solve the issue of having some _SORT values empty
		return hcolName + "_SORT," + hcolName + "_VALUE,";
	}

	private String getHcolNameByFeatureNameIgnoreCase(String fetName, List<FeatureDTO> fullFeaturesFromSolr)
	{
		for(FeatureDTO fet : fullFeaturesFromSolr)
		{
			if(fet.getFetName() != null && fet.getFetName().equalsIgnoreCase(fetName))
			{
				return fet.getHcolName();
			}
		}
		return null;
	}

	// @Async
	// public Future<Void> fillFeatureValues(ResultWrapper wrapper, SolrQuery query, List<FeatureDTO> dtos, CountDownLatch countDown, List<SearchStep>
	// steps) throws SolrServerException
	// {
	// SolrQuery threadSolrQuery = null;
	// try
	// {
	// long start = System.currentTimeMillis();
	// Map<String, FeatureDTO> featuresMap = new HashMap<String, FeatureDTO>();
	// // SolrQuery threadSolrQuery = new SolrQuery(query.getQuery());
	// threadSolrQuery = query.getCopy();
	//
	// for(FeatureDTO fet : dtos)
	// {
	//
	// String fetName = fet.getFetName();
	//
	// if(fetName.equalsIgnoreCase(ConstantSolrFields.VENDOR) || fetName.equalsIgnoreCase(ConstantSolrFields.MAN_NAME))
	// {
	// threadSolrQuery.addFacetField(ConstantSolrFields.MAN_NAME);
	// featuresMap.put(ConstantSolrFields.MAN_NAME, fet);
	// }
	// else if(fetName.equalsIgnoreCase(ConstantSolrFields.ROHS))
	// {
	// threadSolrQuery.addFacetField(ConstantSolrFields.ROHS);
	// featuresMap.put(ConstantSolrFields.ROHS, fet);
	// }
	// else if(fetName.equalsIgnoreCase(ConstantSolrFields.PART_STATUS) || fetName.equalsIgnoreCase(Constants.LIFE_CYCLE_FET_NAME) ||
	// fetName.equalsIgnoreCase(Constants.LIFE_CYCLE_PARAMETRIC))
	// {
	// threadSolrQuery.addFacetField(ConstantSolrFields.LIFE_CYCLE);
	// featuresMap.put(ConstantSolrFields.LIFE_CYCLE, fet);
	// }
	// else if(fetName.equalsIgnoreCase(ConstantSolrFields.PL_NAME))
	// {
	// threadSolrQuery.addFacetField(ConstantSolrFields.PL_NAME);
	// featuresMap.put(ConstantSolrFields.PL_NAME, fet);
	// }
	// else
	// {
	// threadSolrQuery.addFacetField(fet.getHcolName() + "_ORIGINAL");
	// featuresMap.put(fet.getHcolName() + "_ORIGINAL", fet);
	// }
	// }
	//
	// util.addFacetMethod(threadSolrQuery);
	//
	//// util.appendShardQuery(threadSolrQuery);
	//
	// logger.info("Will query solr now by query=\t" + threadSolrQuery.toString());
	//
	// QueryResponse response = partsSolrServer.query(threadSolrQuery, METHOD.POST);
	//
	// wrapper.setResultCount(response.getResults().getNumFound());
	// List<FacetField> facetFields = response.getFacetFields();
	// for(FacetField facetField : facetFields)
	// {
	// FeatureDTO feature = featuresMap.get(facetField.getName());
	// List<Count> counts = facetField.getValues();
	// if(counts != null)
	// {
	// List<FeatureValueDTO> values = new ArrayList<>();
	// feature.setValues(values);
	//
	// for(Count count : counts)
	// {
	// FeatureValueDTO valueDTO = new FeatureValueDTO();
	// valueDTO.setValue(count.getName());
	// valueDTO.setValueCount(count.getCount());
	// values.add(valueDTO);
	// }
	//
	// if(facetField.getName().equalsIgnoreCase(ConstantSolrFields.MAN_NAME))
	// {
	// util.sortStringValues(feature);
	// }
	// else if(facetField.getName().contains("_ORIGINAL"))
	// {
	// util.sortFeatureValues(feature);
	// }
	// }
	// }
	// long serviceTime = (System.currentTimeMillis() - start);
	// steps.add(new SearchStep("get parametric filters for features sub set", threadSolrQuery.toString(), response.getQTime(), serviceTime));
	//
	// }
	// catch(Exception e)
	// {
	// logger.error("Feature value query failed. Query: {}. Features list: {}. Exception: {}", threadSolrQuery, dtos, e);
	// }
	// finally
	// {
	// countDown.countDown();
	// }
	// return new AsyncResult<Void>(null);
	// }

	// public QueryResponse getParametricResultFromSolr(List<FeatureDTO> fullFeaturesFromSolr, FilterDTO filterDTO) throws SolrServerException,
	// IOException
	// {
	// SolrQuery solrQuery = getParametricSolrQuery(fullFeaturesFromSolr, filterDTO);
	//
	// if(solrQuery == null)
	// {
	// return null;
	// }
	//
	//// util.appendShardQuery(solrQuery);
	//
	// logger.info("Will query solr now by query=\t" + solrQuery.toString());
	//
	// QueryResponse response = partsSolrServer.query(solrQuery, METHOD.POST);
	//
	// return response;
	// }

	public List<ParametricSearchResultDTO> mapParametricResultList(List<SolrDocument> solrDocuments, List<FeatureDTO> features)
	{
		List<ParametricSearchResultDTO> allResultDTOs = new ArrayList<ParametricSearchResultDTO>();
		for(SolrDocument solrDocument : solrDocuments)
		{
			ParametricSearchResultDTO searchResultDTO = convertToParametricSearchResultDTO(solrDocument, features);
			allResultDTOs.add(searchResultDTO);
		}
		return allResultDTOs;
	}

	public ParametricSearchResultDTO convertToParametricSearchResultDTO(SolrDocument solrDocument, List<FeatureDTO> features)
	{
		ParametricSearchResultDTO searchResultDTO = new ParametricSearchResultDTO();

		String comId = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.PART_ID));
		String fullPart = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.COM_PART_NUM));
		String description = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.PART_DESCRIPTION));
		String manId = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.MAN_ID));
		String manName = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.MAN_NAME));
		String plId = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.PL_ID));
		String plName = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.PL_NAME));
		String rohs = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.ROHS));
		String rohsVersion = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.ROHS_VERSION));

		String lifeCycle = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.LIFE_CYCLE));
		String starRatig = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.STAR_RATING));
		String smallImage = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.SMALL_IMAGE));
		String largeImage = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.LARGE_IMAGE));
		String pdfID = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.DATASHEET_ID));
		String pdfURL = stringOrEmpty(solrDocument.getFieldValue(ConstantSolrFields.DATASHEET_URL));
		pdfURL = getURLSchemeless(pdfURL);

		searchResultDTO.setComId(comId);
		searchResultDTO.setFullPart(fullPart);
		searchResultDTO.setDescription(description);
		searchResultDTO.setManId(manId);
		searchResultDTO.setManName(manName);
		searchResultDTO.setPlId(plId);
		searchResultDTO.setPlName(plName);
		searchResultDTO.setRohs(rohs);
		searchResultDTO.setLifeCycle(lifeCycle);
		searchResultDTO.setStarRatig(starRatig);
		searchResultDTO.setSmallImage(smallImage);
		searchResultDTO.setLargeImage(largeImage);
		searchResultDTO.setRohsVersion(rohsVersion);
		if(features != null)
		{
			addParametricFeaturestoDTO(searchResultDTO, solrDocument, features);
		}
		return searchResultDTO;
	}

	private String stringOrEmpty(Object obj)
	{
		return (obj == null) ? "" : obj.toString();
	}

	private String getURLSchemeless(String url)
	{
		if(url != null && url.toLowerCase().startsWith("http:"))
		{
			return url.substring("http:".length());
		}
		if(url != null && url.toLowerCase().startsWith("https:"))
		{
			return url.substring("https:".length());
		}
		return url;
	}

	private void addParametricFeaturestoDTO(ParametricSearchResultDTO searchResultDTO, SolrDocument solrDocument, List<FeatureDTO> features)
	{
		List<FeatureDTO> parametricFeatures = new ArrayList<>();
		for(FeatureDTO fet : features)
		{
			String fetName = fet.getFetName();
			if(fetName != null && !notParametricFeaturesLower.contains(fetName.toLowerCase()))
			{
				FeatureDTO featureDtoOfValue = new FeatureDTO();
				String featureValue = stringOrEmpty(solrDocument.getFieldValue(fet.getHcolName() + "_VALUE"));
				String unit = stringOrEmpty(fet.getUnit());

				featureDtoOfValue.setFeatureValue(featureValue);
				featureDtoOfValue.setFetName(fetName);
				featureDtoOfValue.setUnit(unit);
				featureDtoOfValue.setPackageFlag(fet.getPackageFlag());

				parametricFeatures.add(featureDtoOfValue);
			}
		}
		searchResultDTO.setSearchResultParametricFeatures(parametricFeatures);
	}

	// @Async
	// public Future<Void> fillTaxonomyCountsAsync(ResultWrapper wrapper, SolrQuery query, CountDownLatch countDown, FilterDTO filterDTO) throws
	// SolrServerException, IOException
	// {
	// try
	// {
	// Map<String, Map<String, Long>> taxonomyCountsMap = fillTaxonomyCounts(query, filterDTO);
	// wrapper.setTaxonomyCountsMap(taxonomyCountsMap);
	// }
	// finally
	// {
	// countDown.countDown();
	// }
	// return new AsyncResult<Void>(null);
	// }

	// public Map<String, Map<String, Long>> fillTaxonomyCounts(SolrQuery query, FilterDTO filterDTO) throws SolrServerException, IOException
	// {
	// Map<String, Map<String, Long>> taxonomyCountsMap = new HashMap<>();
	// SolrQuery taxSolrQuery = query.getCopy();
	//
	// taxSolrQuery.addFacetField(ConstantSolrFields.MAIN_CAT_NAMES);
	// taxSolrQuery.addFacetField(ConstantSolrFields.SUB_CAT_NAMES);
	// taxSolrQuery.setFacetLimit(-1);
	// taxSolrQuery.setFacetMinCount(1);
	// taxSolrQuery.setFacetSort(FacetParams.FACET_SORT_COUNT);
	// taxSolrQuery.setRows(0);
	// taxSolrQuery.removeSort(ConstantSolrFields.MAN_ORDER);
	//
	// // util.appendShardQuery(taxSolrQuery);
	//
	// logger.info("Will query solr now by query=\t" + taxSolrQuery.toString());
	//
	// QueryResponse response = partsSolrServer.query(taxSolrQuery, METHOD.POST);
	//
	// Set<String> subNames = getSubNamesOfMain(filterDTO.getCategoryName());
	//
	// List<FacetField> facetFields = response.getFacetFields();
	//
	// for(FacetField facetField : facetFields)
	// {
	// Map<String, Long> facetValues = new LinkedHashMap<>();
	//
	// List<Count> counts = facetField.getValues();
	// if(counts != null)
	// {
	//
	// for(Count count : counts)
	// {
	// // Either coming from keyword search or coming from normal
	// // parametric
	// // ex: keyword=bav&filters=....
	// // ex: plName=Capacitors&level=1&filters=...
	// if(StringUtils.isBlank(filterDTO.getCategoryName()) || filterDTO.getMainNames().contains(count.getName()) || (subNames != null &&
	// subNames.contains(count.getName())))
	// {
	// facetValues.put(count.getName(), count.getCount());
	// }
	// }
	// }
	//
	// taxonomyCountsMap.put(facetField.getName(), facetValues);
	// }
	//
	// return taxonomyCountsMap;
	// }

	private Set<String> getSubNamesOfMain(String category) throws SolrServerException, IOException
	{
		if(StringUtils.isBlank(category))
		{
			return null;
		}
		Set<String> subNames = new HashSet<>();

		String queryString = "MAINNAME:\"" + category + "\" AND SUBSEARCHABLEFLAG:\"1\"";

		SolrQuery query = new SolrQuery(queryString);
		query.setRows(0);
		query.setFacet(true);
		query.addFacetField(ConstantSolrFields.TAX_SUB_NAME);
		query.setFacetLimit(-1);
		query.setFacetMinCount(1);

		logger.info("getSubNamesOfMain will query solr now by query=\t" + query.toString());

		QueryResponse response = taxonomySolrServer.query(query, METHOD.POST);

		List<FacetField> facetFields = response.getFacetFields();
		for(FacetField facetField : facetFields)
		{
			List<Count> facetCount = facetField.getValues();
			if(facetCount != null)
			{
				for(Count count : facetField.getValues())
				{
					subNames.add(count.getName());
				}
			}
		}

		return subNames;
	}
	//
	// public String getPLNameByComId(String comId) throws SolrServerException, IOException
	// {
	// SolrQuery solrQuery = new SolrQuery(ConstantSolrFields.PART_ID + ":" + comId);
	// solrQuery.setFields(ConstantSolrFields.PL_NAME);
	//
	// // util.appendShardQuery(solrQuery);
	//
	// logger.info("Will query solr now by query=\t" + solrQuery.toString());
	//
	// QueryResponse response = partsSolrServer.query(solrQuery, METHOD.POST);
	//
	// SolrDocumentList docs = response.getResults();
	// if(docs != null && !docs.isEmpty())
	// {
	// return (String) docs.get(0).get(ConstantSolrFields.PL_NAME);
	// }
	// return null;
	// }

	private String getSolrKeywordQuery(FilterDTO filterDTO)
	{
		try
		{
			String keyword = filterDTO.getKeyword();
			if(StringUtils.isBlank(keyword))
			{
				return "";
			}
			/*
			 * String beginWithSearchQuery = getBeginWithQuery(filterDTO.getKeyword()); if (StringUtils.isBlank(beginWithSearchQuery)) { return null;
			 * } SolrQuery keywordSolrQuery = new SolrQuery(beginWithSearchQuery);
			 * 
			 * QueryResponse response = partsSolrServer.query(keywordSolrQuery, METHOD.POST);
			 * 
			 * if (!util.hasNoResults(response)) { return beginWithSearchQuery; }
			 */

			String internalOperator = " OR ";
			String mainOperator = (filterDTO.getMainOperator() != null) ? filterDTO.getMainOperator() : " AND ";
			String keywordQueryString = util.buildKeywordSearchQueryString(keyword, "", mainOperator, internalOperator, true, ConstantSolrFields.PART_DESCRIPTION);
			return keywordQueryString;
			/*
			 * if (StringUtils.isBlank(keywordQueryString)) { return null; }
			 * 
			 * if (filterDTO.isAutoComplete()) { return keywordQueryString; }
			 * 
			 * keywordSolrQuery = new SolrQuery(keywordQueryString);
			 * 
			 * util.appendShardQuery(keywordSolrQuery);
			 * 
			 * logger.info("getSolrKeywordQuery will query solr now by query=\t" + keywordSolrQuery.toString());
			 * 
			 * response = partsSolrServer.query(keywordSolrQuery, METHOD.POST);
			 * 
			 * if (util.hasNoResults(response) && util.canDropOneCharacterFromLast(filterDTO.getKeyword(), keywordDropLimit)) { // STEP 3: Cut
			 * character one by one logger.info("STEP 3: Cut character one by one");
			 * 
			 * String joinedTerms = util.dropOneCharacterFromLast(filterDTO.getKeyword(), keywordDropLimit); // Start function again
			 * filterDTO.setKeyword(joinedTerms);
			 * 
			 * logger.info("STEP 3: joinedTerms = " + joinedTerms); return getSolrKeywordQuery(filterDTO); }
			 * 
			 * return keywordQueryString;
			 */
		}
		catch(Exception e)
		{
			logger.error("Error happened while trying to getSolrKeywordQuery for parametric", e);
			return null;
		}
	}

	private String getBeginWithQuery(String keyword)
	{
		String searchWord = util.removeSpecialCharacters(keyword, true);
		searchWord = ClientUtils.escapeQueryChars(searchWord);
		if(searchWord != null && searchWord.trim().isEmpty())
		{
			return null;
		}

		StringBuilder nanQuery = new StringBuilder();

		// nanQuery.append(" NAN_PARTNUM_EXACT:").append(searchWord).append("* AND -IS_CUSTOM:1");
		nanQuery.append(" NAN_PARTNUM_EXACT:").append(searchWord).append("*  ");
		return nanQuery.toString();
	}
}
