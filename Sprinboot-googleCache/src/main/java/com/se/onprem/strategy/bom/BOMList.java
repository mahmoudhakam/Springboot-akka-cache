package com.se.onprem.strategy.bom;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.onprem.dto.business.bom.BOMDto;
import com.se.onprem.dto.business.bom.BOMMesssage;
import com.se.onprem.dto.business.bom.BOMRow;
import com.se.onprem.util.ParametricConstants;
import com.se.onprem.util.SolrReader;

@Service
public class BOMList implements IBOMActions
{

	@Autowired
	private SolrReader<BOMDto> solrReader;
	@Autowired
	private SolrClient bomCore;
	@Override
	public BOMMesssage doAction(BOMMesssage message)
	{
		int pageSize=ParametricConstants.ServiceDefaults.MAX_PARTS_PER_PAGE*4;
		SolrQuery query=new SolrQuery("*:*").setRows(pageSize).setStart((message.getPageNumber() - 1) * pageSize);
		QueryResponse response;
		try
		{
			response = bomCore.query(query);
			if(response==null){
				return message;
			}
			List<BOMDto> partsList = solrReader.objectsListFromSolr(response.getResults(), BOMDto.class);
			BOMMesssage resultMessage=new BOMMesssage();
			resultMessage.setSavedBoms(partsList);
			resultMessage.setResultCount(response.getResults().getNumFound());
			return resultMessage;
		}
		catch(SolrServerException | IOException e)
		{

			e.printStackTrace();
		}
		return message;
	}

}
