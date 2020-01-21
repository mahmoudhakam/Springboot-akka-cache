package com.se.part.search.bom.actors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.se.part.search.bom.messages.BOMRequestType;
import com.se.part.search.bom.messages.PartValidationMessage;
import com.se.part.search.bom.messages.PartValidationStatuses;
import com.se.part.search.bom.messages.ValidationResponseMessage;
import com.se.part.search.dto.bom.BOMRow;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.util.ConstantSolrFields;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.BOM_VALIDATION_STRATEGY_LOOKUP)
public class LookupPartValidationActor extends BOMValidator
{
	@Autowired
	SolrClient lookupSolrServer;

	@Override
	public Receive createReceive()
	{
		// TODO Auto-generated method stub
		return receiveBuilder().match(BOMValidator.ValidationMessage.class, row -> validatePart(row)).build();
	}

	@Override
	public void validatePart(ValidationMessage message) throws SolrServerException, IOException
	{

		Collection<BOMRow> parts = message.getReqeustedPart();
		validateInputMans(parts);
		Set<BOMRow> partsWithNoResult = new HashSet<>(parts);
		Set<BOMRow> partsWithResult = new HashSet<>();

		parts.stream().forEach(part -> {
			try
			{
				List<String> newComIds = util.getNewComId(part, message.ignoreManufacturer, lookupSolrServer);
				if(newComIds != null && !newComIds.isEmpty())
				{
					partsWithNoResult.remove(part);
					partsWithResult.add(part);
					completePartData(part, newComIds, message.ignoreManufacturer);
				}
			}
			catch(IOException | SolrServerException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		if(!partsWithResult.isEmpty())
		{

			getSender().tell(new ValidationResponseMessage.RespondPartFound(0, partsWithResult, PartValidationMessage.EXACT), getSelf());
		}
		if(!partsWithNoResult.isEmpty())
		{

			getSender().tell(getMessageForNextStep(message, partsWithNoResult), getSelf());

		}
	}

	private Object getMessageForNextStep(ValidationMessage message, Set<BOMRow> partsWithNoResult)
	{
		if(message.ignoreManufacturer)
		{
			return new ValidationResponseMessage.RespondPartNotFound(0, partsWithNoResult, BOMRequestType.Similar, PartValidationMessage.NOT_FOUND,
					false);
		}
		return new ValidationResponseMessage.RespondPartNotFound(0, partsWithNoResult, BOMRequestType.Passive, PartValidationMessage.NOT_FOUND,
				false);
	}

	private void completePartData(BOMRow part, List<String> newComIds, boolean ignoreMan) throws SolrServerException, IOException
	{

		SolrQuery partsQuery = new SolrQuery(util.generateOrQuery("COM_ID", newComIds)).setRows(newComIds.size()).setFields(
				ConstantSolrFields.COM_ID, ConstantSolrFields.PASSIVE_CORE_PART_NUMBER, ConstantSolrFields.MAN_NAME, ConstantSolrFields.MAN_ID,
				ConstantSolrFields.PL_NAME, ConstantSolrFields.PART_SUMMARY_DESCRIPTION, ConstantSolrFields.LIFE_CYCLE_SUMMARY,
				ConstantSolrFields.DATASHEET_URL, ConstantSolrFields.ROHS, ConstantSolrFields.ROHS_VERSION, ConstantSolrFields.IMAGE_URL,
				ConstantSolrFields.NAN_PARTNUM_EXACT, ConstantSolrFields.MAN_ID, ConstantSolrFields.PL_ID);

		QueryResponse response = partsSummarySolrServer.query(partsQuery);
		if(!util.hasNoResults(response))
		{
			if(ignoreMan)
			{
				part.setValidationStatusCode(PartValidationStatuses.SIMILAR_FOUND_Ignoring_Man.getCode());
				part.setMatchStatus(PartValidationStatuses.SIMILAR_FOUND_Ignoring_Man.getMessage());
				part.setSimilarCount((int) response.getResults().getNumFound());
			}
			else
			{

				part.setValidationStatusCode(PartValidationStatuses.LOOKUP.getCode());
				part.setMatchStatus(PartValidationStatuses.LOOKUP.getMessage());
				util.fillFields(part, response.getResults().get(0));
			}

		}

	}

	
}
