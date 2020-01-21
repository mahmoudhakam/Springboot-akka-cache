package com.se.part.search.services.keywordSearch;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.se.part.search.dto.partSearch.PartSearchResponse;
import com.se.part.search.dto.partSearch.PartSearchResult;
import com.se.part.search.messages.PartSearchOperationMessages;
import com.se.part.search.messages.PartSearchStatus;
import com.se.part.search.strategies.PartTransformationStrategy;

@Service
public class KeywordSearchTransformation implements PartTransformationStrategy
{

	@Override
	public Object transformResult(Object parts, PartTransformationStrategy transformationStrategy)
	{
		PartSearchResponse response = new PartSearchResponse(new PartSearchStatus(PartSearchOperationMessages.SUCCESSFULL_OPERATION, true));
		PartSearchResult result = (PartSearchResult) parts;
		if(result.getPartResult().isEmpty())
		{
			response = new PartSearchResponse(new PartSearchStatus(PartSearchOperationMessages.NO_RESULT_FOUND, false));
			return response;
		}
		response.setPartList(new ArrayList<>());
		response.getPartList().add(result);
		return response;
	}

}
