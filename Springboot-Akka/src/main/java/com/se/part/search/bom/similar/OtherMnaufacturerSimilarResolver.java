package com.se.part.search.bom.similar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.bom.BOMRow;
import com.se.part.search.services.keywordSearch.Util;
import com.se.part.search.util.ConstantSolrFields;

@Service("5")
public class OtherMnaufacturerSimilarResolver implements SimilarPartsResolver
{
	@Autowired
	Util<BOMRow> util;
	@Autowired
	SolrClient partsSummarySolrServer;

	@Override
	public List<BOMRow> similarParts(String partNumber, String manId)
	{
		StringBuilder qb = new StringBuilder(512);

		try
		{

			String query = util.getPartManQuery(partNumber, manId, qb, true, true, ConstantSolrFields.NAN_PARTNUM_EXACT);
			List<BOMRow> similarParts = similarList(query, partNumber);
			if(!similarParts.isEmpty())
			{
				return similarParts;

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
		
		SolrDocumentList documents = util.getDocumentsFromQuery(query, partsSummarySolrServer, 25, originalPartNumber);
		return util.extractPartListFromSolrDocuments(originalPartNumber,  documents);
	}

	

}
