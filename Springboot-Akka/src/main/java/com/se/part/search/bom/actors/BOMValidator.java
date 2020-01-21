package com.se.part.search.bom.actors;

import static com.se.part.search.configuration.SpringExtension.SPRING_EXTENSION_PROVIDER;
import static com.se.part.search.services.keywordSearch.Util.INVALID;
import static com.se.part.search.services.keywordSearch.Util.MISSING;
import static com.se.part.search.services.keywordSearch.Util.UNKNOWN;
import static com.se.part.search.services.keywordSearch.Util.VALID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.bom.BOMRow;
import com.se.part.search.dto.keyword.ManDTO;
import com.se.part.search.services.keywordSearch.ManValidator;
import com.se.part.search.services.keywordSearch.Util;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

@Service
public abstract class BOMValidator extends AbstractActor
{
	@Autowired
	SolrClient partsSummarySolrServer;
	@Autowired
	Util<BOMRow> util;
	@Autowired
	ManValidator manValidator;
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public abstract void validatePart(ValidationMessage part) throws SolrServerException, IOException;

	public static final class ValidationMessage
	{
		final Collection<BOMRow> reqeustedParts;
		final boolean ignoreManufacturer;

		public ValidationMessage(Collection<BOMRow> reqeustedParts, boolean ignoreManufacturer)
		{
			this.reqeustedParts = reqeustedParts;
			this.ignoreManufacturer = ignoreManufacturer;
		}

		public Collection<BOMRow> getReqeustedPart()
		{
			return reqeustedParts;
		}

	}

	protected ActorRef actorForValidationStep(String validationStep)
	{
		return getContext().getSystem().actorOf(SPRING_EXTENSION_PROVIDER.get(getContext().getSystem()).props(validationStep));
	}

	protected void fillManStatus(BOMRow part)
	{
		String requestedMan = part.getUploadedManufacturer();
		if(StringUtils.isEmpty(requestedMan))
		{
			part.setManStatus(MISSING);
			return;
		}
		ManDTO validatedManDto = manValidator.getValidatedMan(requestedMan);
		if(validatedManDto == null)
		{
			part.setManStatus(INVALID);
			return;
		}
		part.setManStatus(VALID);
		part.setManufacturerId("" + validatedManDto.getManId());
	}

	protected void validateInputMans(Collection<BOMRow> parts)
	{
		Map<String, String> validatedManNames = new HashMap<>();
		Set<String> distinctManNames = new HashSet<>();
		for(BOMRow part : parts)
		{
			if(StringUtils.isBlank(part.getUploadedManufacturer()))
			{
				part.setManStatus(MISSING);
				continue;
			}
			distinctManNames.add(part.getUploadedManufacturer());

		}
		List<String> distinctManList = new ArrayList<>(distinctManNames);
		validatedManNames = manValidator.validateManNames(distinctManList);
		for(BOMRow part : parts)
		{

			String uploadedManufacturer = part.getUploadedManufacturer();
			String validmanName = validatedManNames.get(uploadedManufacturer);
			if(StringUtils.isNotBlank(validmanName))
			{
				part.setManufacturerId(validmanName.split("::")[1]);
				part.setManufacturer(validmanName.split("::")[0]);
				part.setManStatus(VALID);
			}
			else if(StringUtils.isNotBlank(uploadedManufacturer))
			{

				if(manValidator.isUnknownMan(uploadedManufacturer))
				{
					part.setManStatus(UNKNOWN);
					Integer unknownMnaId = manValidator.getunknownMnaId(uploadedManufacturer);
					if(unknownMnaId!=null&&unknownMnaId>0)
					part.setManufacturerId(String.valueOf(unknownMnaId));
				}
				else
				{

					part.setManStatus(INVALID);
				}

			}

		}

	}

}
