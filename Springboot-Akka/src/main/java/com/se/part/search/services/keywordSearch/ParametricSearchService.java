package com.se.part.search.services.keywordSearch;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.keyword.parametric.FeatureDTO;
import com.se.part.search.dto.keyword.parametric.FilterDTO;
import com.se.part.search.dto.keyword.parametric.PLTypeDTO;
import com.se.part.search.dto.keyword.parametric.ResultWrapper;

@Service
public interface ParametricSearchService
{

	List<PLTypeDTO> getAllTaxonomy() throws SolrServerException, IOException;

	ResultWrapper getParametricFitlers(FilterDTO filterDTO) throws SolrServerException, InterruptedException, IOException;

	ResultWrapper getParametricSearchResults(FilterDTO filterDTO) throws SolrServerException, IOException;

	ResultWrapper getParametricPartDetailsSearchResults(FilterDTO filterDTO) throws SolrServerException, IOException;

	String getParametricSolrQueryFromSelectedFilters(List<FeatureDTO> selectedFilters, List<FeatureDTO> fullFeaturesFromSolr);

	void addSortQuery(FilterDTO filterDTO, SolrQuery solrQuery, List<FeatureDTO> fullFeaturesFromSolr, String staticSortField);

	ResultWrapper getTaxonomyFacets(FilterDTO filterDTO) throws SolrServerException, IOException, InterruptedException;
}
