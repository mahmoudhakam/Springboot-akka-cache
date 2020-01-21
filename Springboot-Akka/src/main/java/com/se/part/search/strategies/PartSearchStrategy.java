package com.se.part.search.strategies;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.codehaus.jettison.json.JSONException;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.ParentSearchRequest;
import com.se.part.search.dto.PartSearchStep;

@Service
public interface PartSearchStrategy
{
	Object partSearchRequest(ParentSearchRequest request, PartSearchStrategy searchStrategy, List<PartSearchStep> steps) throws SolrServerException, IOException, InterruptedException, JSONException;
}
