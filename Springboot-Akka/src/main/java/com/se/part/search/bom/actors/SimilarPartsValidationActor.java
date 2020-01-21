package com.se.part.search.bom.actors;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.se.part.search.bom.messages.PartValidationMessage;
import com.se.part.search.bom.messages.PartValidationStatuses;
import com.se.part.search.bom.messages.ValidationResponseMessage;
import com.se.part.search.dto.bom.BOMRow;
import com.se.part.search.dto.keyword.Constants;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.BOM_VALIDATION_STRATEGY_SIMILAR)
public class SimilarPartsValidationActor extends BOMValidator
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

		Collection<BOMRow> requestedParts = message.getReqeustedPart();
		// validateInputMans(requestedParts);
		for(BOMRow part : requestedParts)
		{
			List<Suggestion> suggestions = getSuggestions(util.removeSpecialCharacters(part.getUploadedMpn(), true));
			if(suggestions == null || suggestions.isEmpty())
			{
				part.setMatchStatus(PartValidationStatuses.INVALID.getMessage());
				part.setValidationStatusCode(PartValidationStatuses.INVALID.getCode());
				continue;
			}

			String query = util.getSimilarPartManQuery(suggestions.get(0).getAlternatives(), part.getManufacturerId(), false).trim();
			SolrDocumentList documents;
			try
			{
				documents = util.getDocumentsFromQuery(query, partsSummarySolrServer, 1, null);
				if(documents != null && documents.size() > 0)
				{
					part.setMatchStatus(PartValidationStatuses.SIMILAR_FOUND.getMessage());
					part.setValidationStatusCode(PartValidationStatuses.SIMILAR_FOUND.getCode());
					part.setSimilarCount((int) documents.getNumFound());
					// getSender().tell(new ValidationResponseMessage.RespondPartFound(0, requestedParts, PartValidationMessage.SIMILAR), getSelf());
					continue;

				}
				if(StringUtils.isNotEmpty(part.getManufacturerId()))
				{
					query = util.getSimilarPartManQuery(suggestions.get(0).getAlternatives(), part.getManufacturerId(), true).trim();
					documents = util.getDocumentsFromQuery(query, partsSummarySolrServer, 1, null);
					if(documents != null && documents.size() > 0)
					{
						part.setMatchStatus(PartValidationStatuses.SIMILAR_FOUND.getMessage());
						part.setValidationStatusCode(PartValidationStatuses.SIMILAR_FOUND.getCode());
						part.setSimilarCount((int) documents.getNumFound());
						continue;

					}
					part.setMatchStatus(PartValidationStatuses.INVALID.getMessage());
					part.setValidationStatusCode(PartValidationStatuses.INVALID.getCode());
				}

			}
			catch(SolrServerException | IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		getSender().tell(new ValidationResponseMessage.RespondPartFound(0, requestedParts, PartValidationMessage.SIMILAR), getSelf());
	}

	private List<Suggestion> getSuggestions(String nanPart)
	{
		SolrQuery params = new SolrQuery();
		params.set("qt", "/spell");
		params.set("spellcheck.q", nanPart);
		params.set("spellcheck.count", 50);
		params.set("spellcheck.maxCollations", 0);
		params.set("spellcheck.extendedResults", false);

		QueryResponse response;
		try
		{
			response = partsSummarySolrServer.query(params);
			List<Suggestion> result = response.getSpellCheckResponse().getSuggestions();
			return result;
		}
		catch(SolrServerException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
