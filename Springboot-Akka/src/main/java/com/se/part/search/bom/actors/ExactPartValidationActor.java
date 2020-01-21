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

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.BOM_VALIDATION_STRATEGY_EXACT)
public class ExactPartValidationActor extends BOMValidator
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
		validateInputMans(parts);
		Set<BOMRow>partsWithNoResult=new HashSet<>(parts);
		Set<BOMRow>partsWithResult=new HashSet<>();
		
		List<BOMRow> foundParts = util.getPartsWithResultsFromParameters(parts,  message.ignoreManufacturer, true,partsSummarySolrServer,1);
		partsWithResult.addAll(foundParts);
		partsWithNoResult.removeAll(foundParts);
//		if(!partsWithNoResult.isEmpty()){
//			
//			foundParts = util.getPartsWithResultsFromParameters(partsWithNoResult,  true, true,partsSummarySolrServer,1);
//			partsWithResult.addAll(foundParts);
//			partsWithNoResult.removeAll(foundParts);
//		}
		
		if(!partsWithResult.isEmpty()){
			
			getSender().tell(new ValidationResponseMessage.RespondPartFound(0, partsWithResult, PartValidationMessage.EXACT), getSelf());
		}
		if(!partsWithNoResult.isEmpty()){
			
			 getSender().tell(getMessageForNextStep(message,partsWithNoResult), getSelf());
		}
	}

	private Object getMessageForNextStep(ValidationMessage message, Collection<BOMRow> partsWithNoResult)
	{
		if(message.ignoreManufacturer){
			
			return new ValidationResponseMessage.RespondPartNotFound(0, partsWithNoResult,BOMRequestType.BeginWith, PartValidationMessage.NOT_FOUND,true);

		}
		return new ValidationResponseMessage.RespondPartNotFound(0, partsWithNoResult,BOMRequestType.Lookup, PartValidationMessage.NOT_FOUND,false);
	}

}
