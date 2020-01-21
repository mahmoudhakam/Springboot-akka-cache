package com.se.onprem.services.cache.loader;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheLoader;
import com.se.onprem.dto.business.ACLQueryResult;
import com.se.onprem.dto.ws.CustomerPart;
import com.se.onprem.dto.ws.PartSearchDTO;
import com.se.onprem.util.ConstantSolrFields;

@Component("customerComidCacheLoader")
public class CustomerPartsCacheLoader extends CacheLoader<String, ACLQueryResult>
{

	private static final Integer MAX_ACL_ROWS = 500;
	SolrClient aclCore;

	@Autowired
	public CustomerPartsCacheLoader(SolrClient aclCore)
	{
		this.aclCore = aclCore;
	}
	@Override
	public ACLQueryResult load(String queryString) throws Exception
	{
		SolrQuery query = new SolrQuery(queryString);
		query.setRows(MAX_ACL_ROWS);
//		query.setFields(ConstantSolrFields.COM_ID);
		QueryResponse response;
		try
		{
			response = aclCore.query(query);
			if(response.getResults() != null)
			{
				return transformDocumentList(response.getResults());

			}
		}
		catch(SolrServerException | IOException e)
		{

			e.printStackTrace();
		}
		return null;
	}
	private ACLQueryResult transformDocumentList(SolrDocumentList results)
	{
		ACLQueryResult result=new ACLQueryResult();
		List<String>comIds=new LinkedList<>();
		 List<PartSearchDTO> partsWithNoComid=new LinkedList<>();
		results.stream().forEach(doc->{
			Object comId = doc.getFieldValue(ConstantSolrFields.COM_ID);
			if(comId!=null){
				
				comIds.add(comId.toString());
			}else{
				partsWithNoComid.add(createPartDTOFromDocument(doc));
			}
		});
		result.setFoundComIds(comIds);
		result.setPartsWithNoComid(partsWithNoComid);
		return result;
	}
	private PartSearchDTO createPartDTOFromDocument(SolrDocument doc)
	{
		PartSearchDTO part=new PartSearchDTO();
		CustomerPart customerPart=new CustomerPart();
		part.addCustomerPart(customerPart);
		customerPart.setCpn(doc.getFieldValue(ConstantSolrFields.CPN).toString());
		customerPart.setMpn(doc.getFieldValue(ConstantSolrFields.MPN).toString());
		customerPart.setMan(doc.getFieldValue(ConstantSolrFields.MAN_NAME).toString());
		return part;
	}

}
