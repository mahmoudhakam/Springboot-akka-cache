package com.se.part.search.services.keywordSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.keyword.Facet;
import com.se.part.search.dto.keyword.FacetCategory;
import com.se.part.search.dto.keyword.FilterFeature;
import com.se.part.search.dto.keyword.KeywordFacet;
import com.se.part.search.dto.keyword.KeywordFacetsWrapper;
import com.se.part.search.dto.keyword.OperationMessages;
import com.se.part.search.dto.keyword.PartDTO;
import com.se.part.search.dto.keyword.Payload;
import com.se.part.search.dto.keyword.PlFeatures;
import com.se.part.search.dto.keyword.RequestParameters;
import com.se.part.search.dto.keyword.RestResponseWrapper;
import com.se.part.search.dto.keyword.ResultObject;
import com.se.part.search.dto.keyword.Status;
import com.se.part.search.dto.keyword.parametric.FeatureDTO;
import com.se.part.search.dto.keyword.parametric.FeatureValueDTO;
import com.se.part.search.dto.keyword.parametric.FilterDTO;
import com.se.part.search.dto.keyword.parametric.PLTypeDTO;
import com.se.part.search.dto.keyword.parametric.ParametricSearchResultDTO;
import com.se.part.search.dto.keyword.parametric.ResultWrapper;
import com.se.part.search.util.ConstantSolrFields;

@Service
public class TransformerService
{
	private static final int MAX_KEYWORD_LENGTH = 50;

	@Autowired
	Util<?> util;

	private JsonHandler<FilterFeature> jsonHandler = new JsonHandler<>();

	public RestResponseWrapper transformAllTaxonomy(List<PLTypeDTO> cachedTaxonomyTree)
	{

		if(cachedTaxonomyTree == null)
		{
			return null;
		}

		RestResponseWrapper restResponseWrapper = new RestResponseWrapper();

		transformStatus(new ResultWrapper(), restResponseWrapper);

		Payload payload = new Payload();
		payload.setProductLineTypes(cachedTaxonomyTree);
		restResponseWrapper.setPayload(payload);

		return restResponseWrapper;
	}

	public RestResponseWrapper transformParametricFitlers(ResultWrapper resultWrapper, FilterDTO filterDTO)
	{

		if(resultWrapper == null)
		{
			return null;
		}

		RestResponseWrapper restResponseWrapper = new RestResponseWrapper();

		transformStatus(resultWrapper, restResponseWrapper);

		PlFeatures plFeatures = transformPlFeatures(resultWrapper, restResponseWrapper, filterDTO);
		ResultObject resultObject = new ResultObject();

		resultObject.setPlFeatures(plFeatures);
		restResponseWrapper.setResultObj(resultObject);
		restResponseWrapper.setSteps(resultWrapper.getSteps());
		return restResponseWrapper;
	}

	public RestResponseWrapper transformParametricPartDetailsSearchResults(ResultWrapper resultWrapper)
	{
		if(resultWrapper == null)
		{
			return null;
		}

		RestResponseWrapper restResponseWrapper = new RestResponseWrapper();

		transformStatus(resultWrapper, restResponseWrapper);

		List<PartDTO> partDTOsList = new ArrayList<>();
		for(ParametricSearchResultDTO parametricSearchResultDTO : resultWrapper.getParts())
		{
			if(parametricSearchResultDTO.getSearchResultParametricFeatures() != null)
			{
				PartDTO partDTO = new PartDTO();
				List<FeatureDTO> features = new ArrayList<>();

				features.addAll(parametricSearchResultDTO.getSearchResultParametricFeatures());
				partDTO.setFeatures(features);
				partDTOsList.add(partDTO);
			}
		}

		ResultObject resultObject = new ResultObject();

		resultObject.setTotalItems(resultWrapper.getResultCount());
		resultObject.setPartsList(partDTOsList);
		restResponseWrapper.setResultObj(resultObject);

		return restResponseWrapper;
	}

	public RestResponseWrapper transformParametricSearchResults(ResultWrapper resultWrapper)
	{
		if(resultWrapper == null)
		{
			return null;
		}

		RestResponseWrapper restResponseWrapper = new RestResponseWrapper();

		transformStatus(resultWrapper, restResponseWrapper);

		List<PartDTO> partsList = transformPartDTOsList(resultWrapper.getParts());
		ResultObject resultObject = new ResultObject();

		resultObject.setTotalItems(resultWrapper.getResultCount());
		resultObject.setPartsList(partsList);
		restResponseWrapper.setResultObj(resultObject);

		return restResponseWrapper;
	}

	private void transformStatus(ResultWrapper resultWrapper, RestResponseWrapper restResponseWrapper)
	{

		Status status = resultWrapper.getStatus();

		if(status == null)
		{
			status = new Status(OperationMessages.SUCCESSFULL_OPERATION, true);
		}

		restResponseWrapper.setStatus(status);
	}

	private PlFeatures transformPlFeatures(ResultWrapper resultWrapper, RestResponseWrapper restResponseWrapper, FilterDTO filterDTO)
	{
		PlFeatures plFeatures = new PlFeatures();

		Facet facet = transformFacets(resultWrapper.getTaxonomyCountsMap());

		plFeatures.setPlName(filterDTO.getCategoryName());
		plFeatures.setFeatures(resultWrapper.getFacets());
		plFeatures.setFacet(facet);

		return plFeatures;
	}

	private PartDTO transformPartDTO(ParametricSearchResultDTO parametricSearchResultDTO)
	{
		if(parametricSearchResultDTO == null)
		{
			return null;
		}

		PartDTO partDTO = new PartDTO();
		List<FeatureDTO> features = new ArrayList<>();

		FeatureDTO dataProviderID = new FeatureDTO();
		FeatureDTO partNumber = new FeatureDTO();
		FeatureDTO pl_Name = new FeatureDTO();
		FeatureDTO partDescription = new FeatureDTO();
		FeatureDTO roHSStatus = new FeatureDTO();
		FeatureDTO roHSVersion = new FeatureDTO();
		FeatureDTO partStatus = new FeatureDTO();
		FeatureDTO manufacturer = new FeatureDTO();
		FeatureDTO manufacturerId = new FeatureDTO();

		dataProviderID.setFetName("DataProviderID");
		dataProviderID.setFeatureValue(parametricSearchResultDTO.getComId());
		dataProviderID.setUnit("");
		features.add(dataProviderID);

		partNumber.setFetName("PartNumber");
		partNumber.setFeatureValue(parametricSearchResultDTO.getFullPart());
		partNumber.setUnit("");
		features.add(partNumber);

		pl_Name.setFetName("PL_NAME");
		pl_Name.setFeatureValue(parametricSearchResultDTO.getPlName());
		pl_Name.setUnit("");
		features.add(pl_Name);

		partDescription.setFetName("PartDescription");
		partDescription.setFeatureValue(parametricSearchResultDTO.getDescription());
		partDescription.setUnit("");
		features.add(partDescription);

		roHSStatus.setFetName("ROHS");
		roHSStatus.setFeatureValue(parametricSearchResultDTO.getRohs());
		roHSStatus.setUnit("");
		features.add(roHSStatus);

		roHSVersion.setFetName("ROHSVersion");
		roHSVersion.setFeatureValue(parametricSearchResultDTO.getRohsVersion());
		roHSVersion.setUnit("");
		features.add(roHSVersion);

		partStatus.setFetName("LifeCycle");
		partStatus.setFeatureValue(parametricSearchResultDTO.getLifeCycle());
		partStatus.setUnit("");
		features.add(partStatus);

		manufacturer.setFetName("Manufacturer");
		manufacturer.setFeatureValue(parametricSearchResultDTO.getManName());
		manufacturer.setUnit("");
		features.add(manufacturer);

		manufacturerId.setFetName("ManufacturerId");
		manufacturerId.setFeatureValue(parametricSearchResultDTO.getManId());
		manufacturerId.setUnit("");
		features.add(manufacturerId);

		if(parametricSearchResultDTO.getSearchResultParametricFeatures() != null)
		{
			features.addAll(parametricSearchResultDTO.getSearchResultParametricFeatures());
		}

		partDTO.setFeatures(features);
		return partDTO;
	}

	private List<PartDTO> transformPartDTOsList(List<ParametricSearchResultDTO> parametricSearchResultDTOsList)
	{
		if(parametricSearchResultDTOsList == null)
		{
			return null;
		}

		List<PartDTO> partDTOsList = new ArrayList<>();
		for(ParametricSearchResultDTO parametricSearchResultDTO : parametricSearchResultDTOsList)
		{
			PartDTO partDTO = transformPartDTO(parametricSearchResultDTO);
			partDTOsList.add(partDTO);
		}
		return partDTOsList;
	}

	public RestResponseWrapper transformTaxonomyFacets(ResultWrapper resultWrapper)
	{
		if(resultWrapper == null)
		{
			return null;
		}

		RestResponseWrapper restResponseWrapper = new RestResponseWrapper();

		transformStatus(resultWrapper, restResponseWrapper);
		List<KeywordFacet> mainCategories = new ArrayList<>();
		List<KeywordFacet> subCategories = new ArrayList<>();

		KeywordFacetsWrapper keywordFacetsWrapper = new KeywordFacetsWrapper();

		Map<String, Map<String, Long>> taxonomyCountsMap = resultWrapper.getTaxonomyCountsMap();

		Map<String, Long> mainCategoriesMap = taxonomyCountsMap.get(ConstantSolrFields.MAIN_CAT_NAMES);
		Map<String, Long> subCategoriesMap = taxonomyCountsMap.get(ConstantSolrFields.SUB_CAT_NAMES);

		for(Entry<String, Long> entry : mainCategoriesMap.entrySet())
		{
			KeywordFacet facet = new KeywordFacet(entry.getKey(), entry.getValue());
			mainCategories.add(facet);
		}
		for(Entry<String, Long> entry : subCategoriesMap.entrySet())
		{
			KeywordFacet facet = new KeywordFacet(entry.getKey(), entry.getValue());
			subCategories.add(facet);
		}

		keywordFacetsWrapper.setMainCategories(mainCategories);
		keywordFacetsWrapper.setSubCategories(subCategories);

		restResponseWrapper.setKeywordFacetsWrapper(keywordFacetsWrapper);

		return restResponseWrapper;
	}

	private Facet transformFacets(Map<String, Map<String, Long>> taxonomyCountsMap)
	{
		if(taxonomyCountsMap == null)
		{
			return null;
		}

		Facet facet = new Facet();

		Map<String, Long> mainCategoriesMap = taxonomyCountsMap.get(ConstantSolrFields.MAIN_CAT_NAMES);
		Map<String, Long> subCategoriesMap = taxonomyCountsMap.get(ConstantSolrFields.SUB_CAT_NAMES);

		List<FacetCategory> mainCategories = mapFacets(mainCategoriesMap);
		List<FacetCategory> subCategories = mapFacets(subCategoriesMap);

		facet.setMainCategories(mainCategories);
		facet.setSubCategories(subCategories);

		return facet;
	}

	private List<FacetCategory> mapFacets(Map<String, Long> categoriesMap)
	{
		if(categoriesMap == null)
		{
			return null;
		}
		List<FacetCategory> categories = new ArrayList<>();

		for(Entry<String, Long> entry : categoriesMap.entrySet())
		{
			String mainCategoryName = entry.getKey();
			Long count = entry.getValue();
			FacetCategory category = new FacetCategory();
			category.setName(mainCategoryName);
			category.setCount(count);
			categories.add(category);
		}
		return categories;
	}

	public FilterDTO transformFilterDTO(RequestParameters requestParameters)
	{
		if(requestParameters == null)
		{
			return null;
		}

		FilterDTO filterDTO = new FilterDTO();
		String categoryName = requestParameters.getPlName();

		// Main@Sub
		// Ex: Capacitors@Accessories and Resistors@Accessories and
		// Magnetics@Accessories
		if(!util.isNullSpacesOrEmpty(categoryName))
		{
			if(categoryName.indexOf('@') != -1)
			{
				filterDTO.setMainCategoryName(categoryName.split("@")[0].trim());
				filterDTO.setCategoryName(categoryName.split("@")[1].trim());
			}
			else
			{
				filterDTO.setCategoryName(categoryName.trim());
			}
		}

		filterDTO.setCategoryId(requestParameters.getPlId());
		filterDTO.setLevel(requestParameters.getLevel());
		filterDTO.setDebug(requestParameters.isDebug());
		filterDTO.setKeyword(StringUtils.substring(requestParameters.getKeyword(), 0, MAX_KEYWORD_LENGTH));

		filterDTO.setPageSize(requestParameters.getPageSize());
		filterDTO.setSortQuery(requestParameters.getOrder());
		filterDTO.setStartPage(requestParameters.getPageNumber());
		filterDTO.setLastFilter(requestParameters.getLast_select());
		filterDTO.setCollectFacetsEnabled(requestParameters.isFacetsEnabled());
		String filtersJSON = requestParameters.getFilters();
		List<FeatureDTO> selectedFilters = transformSelectedFilters(filtersJSON);
		filterDTO.setSelectedFilters(selectedFilters);
		filterDTO.setMainOperator(requestParameters.getKeywordOperator());
		filterDTO.setPartDetailsComId(requestParameters.getPartDetailsComId());

		return filterDTO;
	}

	private List<FeatureDTO> transformSelectedFilters(String filtersJSON)
	{
		List<FeatureDTO> selectedFilters = null;
		if(filtersJSON != null && !filtersJSON.isEmpty())
		{
			try
			{
				List<FilterFeature> filters = jsonHandler.convertJSONToList(filtersJSON, FilterFeature.class);
				if(filters != null && !filters.isEmpty())
				{
					selectedFilters = new ArrayList<>();
					for(FilterFeature filterFeature : filters)
					{
						FeatureDTO selectedFilter = new FeatureDTO();
						String filterFeatureName = mapFeatureNamesToParametricFeatures(filterFeature.getName());
						selectedFilter.setFetName(filterFeatureName);

						List<String> filterValues = filterFeature.getValues();
						if(filterValues != null && !filterValues.isEmpty())
						{
							List<FeatureValueDTO> values = new ArrayList<>();
							for(String value : filterValues)
							{
								FeatureValueDTO valueDTO = new FeatureValueDTO();
								valueDTO.setValue(value);
								values.add(valueDTO);
							}

							selectedFilter.setValues(values);
							selectedFilters.add(selectedFilter);
						}
					}
				}
			}
			catch(Exception e)
			{
			}
		}
		return selectedFilters;
	}

	private String mapFeatureNamesToParametricFeatures(String featureName)
	{
		if(featureName.equals("PartStatus"))
		{
			return "Life Cycle";
		}
		return featureName;
	}
}
