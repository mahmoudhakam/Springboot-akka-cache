package com.se.onprem.strategy.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.onprem.dto.ws.CustomerPart;
import com.se.onprem.dto.ws.FeatureDTO;
import com.se.onprem.strategy.AddPartsStrategy;
import com.se.onprem.strategy.PartSearchStrategy;
import com.se.onprem.util.JsonHandler;
import com.se.onprem.util.SolrWriter;

@Service
public class PartsSolrAddingStartegy implements AddPartsStrategy
{

	SolrWriter<CustomerPart> solrIndexer;
	JsonHandler<FeatureDTO> featureConverter;
	PartSearchStrategy partSearchStrategy;
	private SolrClient aclCore;

	@Autowired
	public PartsSolrAddingStartegy(SolrClient aclCore, PartSearchStrategy partSearchStrategy)
	{
		solrIndexer = new SolrWriter<>();
		featureConverter = new JsonHandler<>();
		this.aclCore = aclCore;
		this.partSearchStrategy = partSearchStrategy;
	}

	@Override
	public List<CustomerPart> addParts(String token, List<CustomerPart> inputParts)
	{

		if(inputParts == null || inputParts.isEmpty())
			return null;
		try
		{
			addMissingComIds(inputParts, token);
		}
		catch(UnsupportedEncodingException e)
		{

			e.printStackTrace();
		}
		inputParts.stream().forEach(part -> {

			part.setPartID(System.nanoTime());
			part.setCompositeKey(StringUtils.defaultString(part.getCpn()) + ";" + StringUtils.defaultString(part.getMpn()) + ";"
					+ StringUtils.defaultString(part.getMan()));
			if(part.getNanPart() == null)
			{
				part.setNanPart(part.getMpn());
			}
			if(part.getCustomFeatures() != null)
			{

				part.setCustomFeaturesJson(featureConverter.convertListToJon(part.getCustomFeatures()));
			}
		});

		boolean success = solrIndexer.indexParts(inputParts, aclCore);
		if(success)
		{

			return inputParts;
		}
		return null;
	}

	private void addMissingComIds(List<CustomerPart> inputParts, String token) throws UnsupportedEncodingException
	{
		List<CustomerPart> missingComIdsList = inputParts.stream().filter(e -> StringUtils.isEmpty(e.getComID())).collect(Collectors.toList());
		System.out.println(missingComIdsList);
		partSearchStrategy.completePartsWithMissingComIds(missingComIdsList, token);

	}

}
