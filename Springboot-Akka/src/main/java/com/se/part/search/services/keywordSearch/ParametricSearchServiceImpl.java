package com.se.part.search.services.keywordSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.keyword.parametric.FeatureDTO;
import com.se.part.search.dto.keyword.parametric.FilterDTO;
import com.se.part.search.dto.keyword.parametric.PLTypeDTO;
import com.se.part.search.dto.keyword.parametric.ResultWrapper;

@Service("parametricServiceImpl")
public class ParametricSearchServiceImpl implements ParametricSearchService
{
	private static final int FEATURES_PER_THREAD = 10;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String, Long> plPartsCountMap;

	@Autowired
	private ParametricSearchServiceHelper parametricSearchServiceHelper;

	@Autowired
	private Util util;

	@PostConstruct
	public void init()
	{
		plPartsCountMap = new HashMap<String, Long>();
		// parametricSearchServiceHelper.fillPlsMap(plPartsCountMap);
	}

	@Override
	public List<PLTypeDTO> getAllTaxonomy() throws SolrServerException, IOException
	{
		Map<String, PLTypeDTO> out = parametricSearchServiceHelper.getAllPlTypes();

		if(out != null)
		{
			List<PLTypeDTO> values = new ArrayList<PLTypeDTO>(out.values());
			return values;
		}
		return null;
	}

	// @Override
	// public ResultWrapper getParametricFitlers(FilterDTO filterDTO) throws SolrServerException, IOException, InterruptedException
	// {
	// List<FeatureDTO> fullFeaturesFromSolr = parametricSearchServiceHelper.getPlFeatures(filterDTO);
	//
	// List<FeatureDTO> selectedFilters = filterDTO.getSelectedFilters();
	// ResultWrapper mergeWrapper = null;
	// FeatureDTO lastFeature = null;
	// if(filterDTO.getLastFilter() != null && selectedFilters != null && !selectedFilters.isEmpty())
	// {
	// lastFeature = new FeatureDTO(filterDTO.getLastFilter());
	// int featureIndex = selectedFilters.indexOf(lastFeature);
	// if(featureIndex != -1)
	// {
	// List<FeatureDTO> latestFeatureList = new ArrayList<>();
	// int indexOfEmptyFeature = fullFeaturesFromSolr.indexOf(lastFeature);
	//
	// if(indexOfEmptyFeature != -1)
	// {
	// lastFeature = (FeatureDTO) util.deepCopy(fullFeaturesFromSolr.get(indexOfEmptyFeature));
	//
	// }
	// latestFeatureList.add(lastFeature);
	// List<FeatureDTO> selectedFilterForMerge = new ArrayList<>(selectedFilters);
	// FilterDTO mergedFilterDto = (FilterDTO) util.deepCopy(filterDTO);
	// selectedFilterForMerge.remove(featureIndex);
	// mergedFilterDto.setSelectedFilters(selectedFilterForMerge);
	// mergeWrapper = new ResultWrapper();
	// fillFeaturesValue(fullFeaturesFromSolr, mergedFilterDto, mergeWrapper, latestFeatureList);
	// }
	//
	// }
	//
	// ResultWrapper wrapper = new ResultWrapper();
	//
	// if(fullFeaturesFromSolr != null && !fullFeaturesFromSolr.isEmpty())
	// {
	// fillFeaturesValue(fullFeaturesFromSolr, filterDTO, wrapper, null);
	// }
	// if(mergeWrapper != null)
	// {
	// fullFeaturesFromSolr.remove(lastFeature);
	// fullFeaturesFromSolr.add(lastFeature);
	// }
	//
	// return wrapper;
	// }

	// private void fillFeaturesValue(List<FeatureDTO> fullFeaturesFromSolr, FilterDTO filterDTO, ResultWrapper wrapper,List<FeatureDTO>
	// featuresToGetValues)
	// throws SolrServerException, InterruptedException, IOException
	// {
	// List<SearchStep> steps = new ArrayList<>();
	// if(featuresToGetValues==null){
	// featuresToGetValues=fullFeaturesFromSolr;
	// }
	//
	// if (filterDTO.getLevel() < 3)
	// {
	// FeatureDTO plFeature = new FeatureDTO();
	// plFeature.setFetName(ConstantSolrFields.PL_NAME);
	// fullFeaturesFromSolr.add(0, plFeature);
	// }
	// int numberOfFeatures = featuresToGetValues.size();
	// SolrQuery query = parametricSearchServiceHelper.getParametricSolrQuery(fullFeaturesFromSolr, filterDTO);
	//
	//
	// if (query == null)
	// {
	// return;
	// }
	//
	// query.setRows(0);
	// query.setFacet(true);
	// // query.setFacetSort(FacetParams.FACET_SORT_INDEX);
	// query.setFacetLimit(-1);
	// query.setFacetMinCount(1);
	// query.set("group", false);
	//
	// int numberOfThreads = numberOfFeatures % FEATURES_PER_THREAD == 0 ? numberOfFeatures / FEATURES_PER_THREAD : (numberOfFeatures /
	// FEATURES_PER_THREAD) + 1;
	// numberOfThreads++; // +1 the thread which gets the taxonomy counts fillTaxonomyCounts
	//
	// CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
	//
	// parametricSearchServiceHelper.fillTaxonomyCountsAsync(wrapper, query, countDownLatch, filterDTO);
	//
	// for (int i = 0; i < numberOfFeatures; i += FEATURES_PER_THREAD)
	// {
	// parametricSearchServiceHelper.fillFeatureValues(wrapper, query,
	// featuresToGetValues.subList(i, Math.min(i + FEATURES_PER_THREAD, numberOfFeatures)), countDownLatch,steps);
	// }
	//
	// if (!countDownLatch.await(60, TimeUnit.SECONDS))
	// {
	// throw new IllegalStateException(
	// countDownLatch.getCount() * FEATURES_PER_THREAD + " missing features for category: " + filterDTO.getCategoryName());
	// }
	// wrapper.setFacets(fullFeaturesFromSolr);
	// if(filterDTO.isDebug()){
	// wrapper.setSteps(steps);
	// }
	//
	// }

	// @Override
	// public ResultWrapper getTaxonomyFacets(FilterDTO filterDTO) throws SolrServerException, IOException, InterruptedException
	// {
	// ResultWrapper resultWrapper = new ResultWrapper();
	//
	// List<FeatureDTO> fullFeaturesFromSolr = parametricSearchServiceHelper.getPlFeatures(filterDTO);
	// SolrQuery query = parametricSearchServiceHelper.getParametricSolrQuery(fullFeaturesFromSolr, filterDTO);
	// Map<String, Map<String, Long>> taxonomyCountsMap = parametricSearchServiceHelper.fillTaxonomyCounts(query, filterDTO);
	//
	// resultWrapper.setTaxonomyCountsMap(taxonomyCountsMap);
	//
	// logger.info("getTaxonomyFacets \t taxonomyCountsMap=\t{}", taxonomyCountsMap.toString());
	// return resultWrapper;
	// }

	// @Override
	// public ResultWrapper getParametricSearchResults(FilterDTO filterDTO) throws SolrServerException, IOException
	// {
	// List<FeatureDTO> fullFeaturesFromSolr = parametricSearchServiceHelper.getPlFeatures(filterDTO);
	//
	// if(fullFeaturesFromSolr == null)
	// {
	// return null;
	// }
	// if(filterDTO.getLevel() < 3)
	// {
	// FeatureDTO plFeature = new FeatureDTO();
	// plFeature.setFetName(ConstantSolrFields.PL_NAME);
	// fullFeaturesFromSolr.add(0, plFeature);
	// }
	// QueryResponse response = parametricSearchServiceHelper.getParametricResultFromSolr(fullFeaturesFromSolr, filterDTO);
	//
	// if(response == null)
	// {
	// return null;
	// }
	//
	// SolrDocumentList solrDocuments = response.getResults();
	// ResultWrapper wrapper = new ResultWrapper();
	// wrapper.setResultCount(solrDocuments.getNumFound());
	// List<ParametricSearchResultDTO> parametricSearchResultDTOs = parametricSearchServiceHelper.mapParametricResultList(solrDocuments,
	// fullFeaturesFromSolr);
	// wrapper.setParts(parametricSearchResultDTOs);
	//
	// return wrapper;
	// }

	// @Override
	// public ResultWrapper getParametricPartDetailsSearchResults(FilterDTO filterDTO) throws SolrServerException, IOException
	// {
	// String partDetailsComId = filterDTO.getPartDetailsComId();
	// String plName = parametricSearchServiceHelper.getPLNameByComId(partDetailsComId);
	//
	// if(plName == null)
	// {
	// return null;
	// }
	//
	// List<String> partIds = new ArrayList<>();
	// partIds.add(partDetailsComId);
	//
	// filterDTO.setSheetView(true);
	// filterDTO.setCategoryName(plName);
	// filterDTO.setLevel(3);
	// filterDTO.setPartIds(partIds);
	//
	// return getParametricSearchResults(filterDTO);
	// }

	@Override
	public void addSortQuery(FilterDTO filterDTO, SolrQuery solrQuery, List<FeatureDTO> fullFeaturesFromSolr, String staticSortField)
	{
		parametricSearchServiceHelper.addSortQuery(filterDTO, solrQuery, fullFeaturesFromSolr, staticSortField);
	}

	@Override
	public String getParametricSolrQueryFromSelectedFilters(List<FeatureDTO> selectedFilters, List<FeatureDTO> fullFeaturesFromSolr)
	{
		return parametricSearchServiceHelper.getParametricSolrQueryFromSelectedFilters(selectedFilters, fullFeaturesFromSolr);
	}

	@Override
	public ResultWrapper getParametricFitlers(FilterDTO filterDTO) throws SolrServerException, InterruptedException, IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultWrapper getParametricSearchResults(FilterDTO filterDTO) throws SolrServerException, IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultWrapper getParametricPartDetailsSearchResults(FilterDTO filterDTO) throws SolrServerException, IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultWrapper getTaxonomyFacets(FilterDTO filterDTO) throws SolrServerException, IOException, InterruptedException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
