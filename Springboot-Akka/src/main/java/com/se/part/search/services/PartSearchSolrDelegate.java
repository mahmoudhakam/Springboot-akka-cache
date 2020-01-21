package com.se.part.search.services;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartSearchSolrDelegate
{
	private SolrClient partsSummarySolrServer;
	private SolrClient partsLookupSolrServer;
	private SolrClient passiveSolrServer;
	private SolrClient manSolrServer;
	private SolrClient partsAliasSolrServer;
	private SolrClient partACLSolrServer;

	@Autowired
	public PartSearchSolrDelegate(SolrClient partsSummarySolrServer, SolrClient manSolrServer)
	{
		super();
		this.partsSummarySolrServer = partsSummarySolrServer;
		this.manSolrServer = manSolrServer;
	}

	public SolrClient getPartsSummarySolrServer()
	{
		return partsSummarySolrServer;
	}

	public void setPartsSummarySolrServer(SolrClient partsSummarySolrServer)
	{
		this.partsSummarySolrServer = partsSummarySolrServer;
	}

	public SolrClient getPartsLookupSolrServer()
	{
		return partsLookupSolrServer;
	}

	public void setPartsLookupSolrServer(SolrClient partsLookupSolrServer)
	{
		this.partsLookupSolrServer = partsLookupSolrServer;
	}

	public SolrClient getPassiveSolrServer()
	{
		return passiveSolrServer;
	}

	public void setPassiveSolrServer(SolrClient passiveSolrServer)
	{
		this.passiveSolrServer = passiveSolrServer;
	}

	public SolrClient getManSolrServer()
	{
		return manSolrServer;
	}

	public void setManSolrServer(SolrClient manSolrServer)
	{
		this.manSolrServer = manSolrServer;
	}

	public SolrClient getPartsAliasSolrServer()
	{
		return partsAliasSolrServer;
	}

	public void setPartsAliasSolrServer(SolrClient partsAliasSolrServer)
	{
		this.partsAliasSolrServer = partsAliasSolrServer;
	}

	public SolrClient getPartACLSolrServer()
	{
		return partACLSolrServer;
	}

	public void setPartACLSolrServer(SolrClient partACLSolrServer)
	{
		this.partACLSolrServer = partACLSolrServer;
	}

}
