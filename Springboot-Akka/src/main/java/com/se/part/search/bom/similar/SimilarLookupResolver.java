package com.se.part.search.bom.similar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.part.search.bom.messages.PartValidationStatuses;
import com.se.part.search.dto.bom.BOMRow;
import com.se.part.search.services.keywordSearch.Util;
import com.se.part.search.util.ConstantSolrFields;

@Service("8")
public class SimilarLookupResolver implements SimilarPartsResolver
{
	@Autowired
	Util<BOMRow> util;
	@Autowired
	SolrClient lookupSolrServer;
	@Autowired
	SolrClient partsSummarySolrServer;

	@Override
	public List<BOMRow> similarParts(String partNumber, String manId)
	{
		
		BOMRow part=new BOMRow();
		part.setUploadedMpn(partNumber);
		try
		{
			List<String> newComIds = util.getNewComId(part, true, lookupSolrServer);
			return similarList(util.generateOrQuery("COM_ID", newComIds), partNumber);
			
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
