package com.se.part.search.services.partSearch;

import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.ParentSearchRequest;
import com.se.part.search.dto.partSearch.PartInput;
import com.se.part.search.dto.partSearch.PartSearchRequest;
import com.se.part.search.messages.PartSearchOperationMessages;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.authentication.TokenBasedAuthentication;
import com.se.part.search.strategies.PartValidationStrategy;
import com.se.part.search.util.PartSearchServiceConstants;

@Service
public class PartSearchValidation implements PartValidationStrategy
{

	private PartSearchHelperService helperService;

	@Autowired
	public PartSearchValidation(PartSearchHelperService helperService)
	{
		this.helperService = helperService;
	}

	@Override
	public PartSearchOperationMessages validateArrowRequest(ParentSearchRequest request, TokenBasedAuthentication tokenAuthService)
	{
		PartSearchRequest partSearchRequest = (PartSearchRequest) request;
		boolean isValid = true;
		// if(partSearchRequest.getSeToken().isEmpty())
		// {
		// return PartSearchOperationMessages.TOKEN_MANDATORY;
		// }
		// isValid = validateToken(tokenAuthService, partSearchRequest.getSeToken());
		// if(!isValid)
		// {
		// return PartSearchOperationMessages.EXPIRED_TOKEN;
		// }
		try
		{
			List<PartInput> parts = helperService.convertJsonPartsMans(partSearchRequest.getPartNumber());
			isValid = helperService.validateSizeRange((int) parts.size(), Integer.parseInt(PartSearchServiceConstants.MAX_PAGE_SIZE));
			if(!isValid)
			{
				return PartSearchOperationMessages.PART_NUMBER_EXCEED;
			}
		}
		catch(JSONException e)
		{
			return PartSearchOperationMessages.INVALID_JSON;
		}

		return validateGeneral(partSearchRequest, helperService);
	}
}
