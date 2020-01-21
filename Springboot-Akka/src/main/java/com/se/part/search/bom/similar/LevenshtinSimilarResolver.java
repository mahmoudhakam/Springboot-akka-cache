package com.se.part.search.bom.similar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.bom.BOMRow;
import com.se.part.search.services.keywordSearch.Util;

@Service("7")
public class LevenshtinSimilarResolver implements SimilarPartsResolver
{
	@Autowired
	Util<BOMRow> util;
	@Autowired
	SolrClient partsSummarySolrServer;

	@Override
	public List<BOMRow> similarParts(String partNumber, String manId)
	{
		List<Suggestion> suggestions = getSuggestions(util.removeSpecialCharacters(partNumber, true));
		if(suggestions == null || suggestions.isEmpty())
		{
			return null;
		}

		String query = util.getSimilarPartManQuery(suggestions.get(0).getAlternatives(), manId, false).trim();
		try
		{
			List<BOMRow> similarParts = similarList(query, partNumber);
			if(!similarParts.isEmpty())
			{
				return similarParts;
			}
			if(StringUtils.isNotEmpty(manId))
			{
				query = util.getSimilarPartManQuery(suggestions.get(0).getAlternatives(), manId, true).trim();
				similarParts = similarList(query, partNumber);
				if(!similarParts.isEmpty())
				{
					return similarParts;
				}
			}

		}
		catch(SolrServerException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	private List<BOMRow> similarList(String query, String originalPartNumber) throws SolrServerException, IOException
	{
		List<BOMRow> similarParts = new ArrayList<>();
		SolrDocumentList documents = util.getDocumentsFromQuery(query, partsSummarySolrServer, 25, originalPartNumber);
		if(documents != null && documents.size() > 0)
		{
			documents.forEach(document -> {
				BOMRow part = new BOMRow();
				util.fillFields(part, document);
				int distance = StringUtils.getLevenshteinDistance(util.removeSpecialCharacters(originalPartNumber, true).toLowerCase(), part.getNanPartNumber().toLowerCase());
				float length = (float) originalPartNumber.length();
				float matchConfidence = length / ((float) distance + length);
				part.setMatchConfidence(matchConfidence);
				similarParts.add(part);
			});

		}
		return similarParts;
	}

	private List<Suggestion> getSuggestions(String nanPart)
	{
		SolrQuery params = new SolrQuery();
		params.set("qt", "/spell");
		params.set("spellcheck.q", nanPart);
		params.set("spellcheck.count", 50);
		params.set("spellcheck.maxCollations", 0);
		params.set("spellcheck.extendedResults", false);

		QueryResponse response;
		try
		{
			response = partsSummarySolrServer.query(params);
			List<Suggestion> result = response.getSpellCheckResponse().getSuggestions();
			return result;
		}
		catch(SolrServerException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
