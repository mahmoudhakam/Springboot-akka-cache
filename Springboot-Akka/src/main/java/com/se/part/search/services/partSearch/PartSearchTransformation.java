package com.se.part.search.services.partSearch;

import java.util.List;

import org.springframework.stereotype.Service;

import com.se.part.search.dto.partSearch.PartSearchResponse;
import com.se.part.search.dto.partSearch.PartSearchResult;
import com.se.part.search.messages.PartSearchOperationMessages;
import com.se.part.search.messages.PartSearchStatus;
import com.se.part.search.strategies.PartTransformationStrategy;

@Service
public class PartSearchTransformation implements PartTransformationStrategy
{

	@Override
	public Object transformResult(Object parts, PartTransformationStrategy transformationStrategy)
	{
		PartSearchResponse response = new PartSearchResponse(new PartSearchStatus(PartSearchOperationMessages.SUCCESSFULL_OPERATION, true));
		List<PartSearchResult> result = (List<PartSearchResult>) parts;
		if(result.isEmpty())
		{
			response = new PartSearchResponse(new PartSearchStatus(PartSearchOperationMessages.NO_RESULT_FOUND, false));
			return response;
		}
		response.setPartList(result);
		return response;
	}

}
