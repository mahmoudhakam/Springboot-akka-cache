package com.se.onprem.services.cache.loader;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheLoader;

@Component("customerPartCountCacheLoader")
public class CustomerPartsCountCacheLoader extends CacheLoader<String, Integer>
{
	SolrClient aclCore;

	@Autowired
	public CustomerPartsCountCacheLoader(SolrClient aclCore)
	{
		this.aclCore = aclCore;
	}

	@Override
	public Integer load(String queryString)
	{
		SolrQuery query = new SolrQuery(queryString);
		query.setRows(0);
		QueryResponse response;
		try
		{
			response = aclCore.query(query);
			if(response.getResults() != null)
			{
				return (int) response.getResults().getNumFound();

			}
		}
		catch(SolrServerException | IOException e)
		{

			e.printStackTrace();
		}

		return null;
	}

}
