package com.se.part.search.services.keywordSearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.ParentSearchRequest;
import com.se.part.search.dto.keyword.KeywordSearchRequest;
import com.se.part.search.messages.PartSearchOperationMessages;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.authentication.TokenBasedAuthentication;
import com.se.part.search.strategies.PartValidationStrategy;

@Service
public class KeywordSearchValidation implements PartValidationStrategy
{

	private PartSearchHelperService helperService;

	@Autowired
	public KeywordSearchValidation(PartSearchHelperService helperService)
	{
		this.helperService = helperService;
	}

	@Override
	public PartSearchOperationMessages validateArrowRequest(ParentSearchRequest request, TokenBasedAuthentication tokenAuthService)
	{
		KeywordSearchRequest keywordSearchRequest = (KeywordSearchRequest) request;
		// if(keywordSearchRequest.getSeToken().isEmpty())
		// {
		// return PartSearchOperationMessages.TOKEN_MANDATORY;
		// }
		// boolean isValid = true;
		// isValid = validateToken(tokenAuthService, keywordSearchRequest.getSeToken());
		// if(!isValid)
		// {
		// return PartSearchOperationMessages.EXPIRED_TOKEN;
		// }
		return validateGeneral(keywordSearchRequest, helperService);
	}

}
