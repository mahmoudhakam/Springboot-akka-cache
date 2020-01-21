// package com.se.part.search.services.partDetails;
//
// import java.util.ArrayList;
// import java.util.List;
//
// import org.springframework.stereotype.Service;
//
// import com.se.part.search.dto.partDetails.PartDetailsDTO;
// import com.se.part.search.dto.partDetails.PartDetailsResponse;
// import com.se.part.search.messages.PartSearchOperationMessages;
// import com.se.part.search.messages.PartSearchStatus;
// import com.se.part.search.strategies.PartTransformationStrategy;
//
// @Service
// public class PartDetailsTransformation implements PartTransformationStrategy
// {
//
// @Override
// public Object transformResult(Object parts, PartTransformationStrategy transformationStrategy)
// {
// List<PartDetailsDTO> partsSet = (List<PartDetailsDTO>) parts;
// PartDetailsTransformation transformation = (PartDetailsTransformation) transformationStrategy;
//
// PartDetailsResponse response = new PartDetailsResponse(new PartSearchStatus(PartSearchOperationMessages.SUCCESSFULL_OPERATION, true));
// if(partsSet == null || partsSet.isEmpty())
// {
// response = new PartDetailsResponse(new PartSearchStatus(PartSearchOperationMessages.NO_RESULT_FOUND, false));
// return response;
// }
//
// response.setPartsList(new ArrayList<>(partsSet));
//
// return response;
// }
//
// }
