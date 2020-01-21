package com.se.part.search.strategies;

import org.springframework.stereotype.Service;

import com.se.part.search.dto.ParentSearchRequest;
import com.se.part.search.messages.PartSearchOperationMessages;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.authentication.TokenBasedAuthentication;
import com.se.part.search.util.PartSearchServiceConstants;

@Service
public interface PartValidationStrategy
{

	PartSearchOperationMessages validateArrowRequest(ParentSearchRequest request, TokenBasedAuthentication tokenAuthService);

	default boolean validateToken(TokenBasedAuthentication tokenAuthService, String token)
	{
		String issuer = "";
		try
		{
			issuer = tokenAuthService.verifyToken(token);
		}
		catch(Exception e)
		{
			return false;
		}
		return !issuer.isEmpty();
	}

	default PartSearchOperationMessages validateGeneral(ParentSearchRequest request, PartSearchHelperService helperService)
	{
		// validate page number and size as a number input
		if(!helperService.validateAgainistRegex(request.getPageNumber(), PartSearchServiceConstants.REGEX.REGEX_NUMBER_ONLY))
		{
			return PartSearchOperationMessages.WRONG_PAGE_NUMBER_FORMAT;
		}
		if(!helperService.validateAgainistRegex(request.getPageSize(), PartSearchServiceConstants.REGEX.REGEX_NUMBER_ONLY))
		{
			return PartSearchOperationMessages.WRONG_PAGE_SIZE_FORMAT;
		}
		if(!helperService.validateSizeRange(Integer.parseInt(request.getPageSize()), Integer.parseInt(PartSearchServiceConstants.MAX_PAGE_SIZE)))
		{
			return PartSearchOperationMessages.PAGESIZE_EXCEED;
		}
		if(!helperService.validateWrongPageNumber(Integer.parseInt(request.getPageNumber())))
		{
			return PartSearchOperationMessages.WRONG_PAGE_NUMBER;
		}
		if(!helperService.validateWrongPageSize(Integer.parseInt(request.getPageSize())))
		{
			return PartSearchOperationMessages.WRONG_PAGE_SIZE;
		}
		return PartSearchOperationMessages.SUCCESSFULL_OPERATION;
	}
}
