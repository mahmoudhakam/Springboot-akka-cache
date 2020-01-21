// package com.se.part.search.services.partDetails;
//
// import java.util.List;
// import java.util.concurrent.atomic.AtomicBoolean;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
//
// import com.se.part.search.dto.ParentSearchRequest;
// import com.se.part.search.dto.partDetails.PartDetailsRequest;
// import com.se.part.search.messages.PartSearchOperationMessages;
// import com.se.part.search.services.PartSearchHelperService;
// import com.se.part.search.services.authentication.TokenBasedAuthentication;
// import com.se.part.search.strategies.PartValidationStrategy;
// import com.se.part.search.util.PartSearchServiceConstants;
//
// @Service
// public class PartDetailsValidation implements PartValidationStrategy
// {
//
// private PartSearchHelperService helperService;
//
// @Autowired
// public PartDetailsValidation(PartSearchHelperService helperService)
// {
// this.helperService = helperService;
// }
//
// @Override
// public PartSearchOperationMessages validateArrowRequest(ParentSearchRequest request, TokenBasedAuthentication tokenAuthService)
// {
//
// PartDetailsRequest partDetailsRequest = (PartDetailsRequest) request;
//
// if(partDetailsRequest.getSeToken().isEmpty())
// {
// return PartSearchOperationMessages.TOKEN_MANDATORY;
// }
//
// boolean isValid = true;
// isValid = validateToken(tokenAuthService, partDetailsRequest.getSeToken());
// if(!isValid)
// {
// return PartSearchOperationMessages.EXPIRED_TOKEN;
// }
// if(partDetailsRequest.getComIDs().isEmpty())
// {
// return PartSearchOperationMessages.MISSING_COMID;
// }
// List<String> comIDs = helperService.splitByDelimeter(partDetailsRequest.getComIDs(), ",");
// if(!helperService.validateSizeRange((int) comIDs.size(), Integer.parseInt(PartSearchServiceConstants.MAX_PAGE_SIZE)))
// {
// return PartSearchOperationMessages.COM_ID_EXCEED;
// }
// AtomicBoolean valid = new AtomicBoolean(true);
// comIDs.forEach(c -> {
// if(!helperService.validateAgainistRegex(c, PartSearchServiceConstants.REGEX.REGEX_NUMBER_ONLY))
// {
// valid.getAndSet(false);
// }
// });
// if(!valid.get())
// {
// return PartSearchOperationMessages.INVALID_COMID_FOMART;
// }
// return validateGeneral(partDetailsRequest, helperService);
// }
//
// }
