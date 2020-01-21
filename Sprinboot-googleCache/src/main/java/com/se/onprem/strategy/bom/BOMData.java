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
public class BOMData implements IBOMActions
{

	@Autowired
	private SolrReader<BOMDto> solrReader;
	@Autowired
	private SolrClient bomCore;
	
	@Autowired
	private SolrClient bomPartsCore;
	
	@Override
	public BOMMesssage doAction(BOMMesssage message)
	{
		SolrQuery bomQuery=new SolrQuery("BOM_ID:" + message.getBomId());
		SolrQuery partsQuery=new SolrQuery("BOM_ID:" + message.getBomId()).setRows(0);
		QueryResponse bomResponse, partsResponse;
		BOMDto bomDto;
		try
		{
			bomResponse = bomCore.query(bomQuery);
			partsResponse = bomPartsCore.query(partsQuery);
			if(bomResponse==null){
				return message;
			}
			List<BOMDto> partsList = solrReader.objectsListFromSolr(bomResponse.getResults(), BOMDto.class);
			if(partsList.size() > 0) {
				bomDto = partsList.get(0);
				bomDto.setPartsCount(partsResponse.getResults().getNumFound());
			}
			BOMMesssage resultMessage=new BOMMesssage();
			resultMessage.setSavedBoms(partsList);
			resultMessage.setResultCount(bomResponse.getResults().getNumFound());
			return resultMessage;
		}
		catch(SolrServerException | IOException e)
		{

			e.printStackTrace();
		}
		return message;
	}

}
