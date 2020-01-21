package com.se.part.search.bom.actors;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.se.part.search.bom.messages.BOMRequestType;
import com.se.part.search.bom.messages.PartValidationMessage;
import com.se.part.search.bom.messages.ValidationResponseMessage;
import com.se.part.search.dto.bom.BOMRow;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.util.ConstantSolrFields;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.BOM_VALIDATION_STRATEGY_BEGINWITH)
public class BeginWithPartValidationActor extends BOMValidator
{

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
		// validateInputMans(parts);
		Set<BOMRow> partsWithNoResult = new HashSet<>(parts);
		Set<BOMRow> partsWithResult = new HashSet<>();

		List<BOMRow> foundParts = util.getPartsWithResultsFromParameters(parts, false, false, partsSummarySolrServer, 1,ConstantSolrFields.NAN_PARTNUM_EXACT,false);
		partsWithResult.addAll(foundParts);
		partsWithNoResult.removeAll(foundParts);
		
		if(!partsWithResult.isEmpty())
		{

			getSender().tell(new ValidationResponseMessage.RespondPartFound(0, partsWithResult, PartValidationMessage.SIMILAR), getSelf());
		}

		if(!partsWithNoResult.isEmpty()){
			
			 getSender().tell(new ValidationResponseMessage.RespondPartNotFound(0, partsWithNoResult,BOMRequestType.Lookup, PartValidationMessage.NOT_FOUND,true), getSelf());
//			getSender().tell(new ValidationResponseMessage.RespondPartFound(0, partsWithNoResult, PartValidationMessage.NOT_FOUND), getSelf());

		}
	}

}
