package com.se.part.search.services.keywordSearch;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.solr.client.solrj.SolrServerException;

import com.se.part.search.dto.keyword.RequestParameters;
import com.se.part.search.dto.keyword.RestResponseWrapper;

public interface KeywordSearchService
{
	RestResponseWrapper getKeywordSearch(RequestParameters filterDTO, boolean addSponserData) throws SolrServerException, IOException;

	RestResponseWrapper facetMap(RequestParameters filterDTO) throws SolrServerException, IOException;

	RestResponseWrapper getAutoComplete(RequestParameters filterDTO) throws SolrServerException, IOException, InterruptedException, ExecutionException;

}
