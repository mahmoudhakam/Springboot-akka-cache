package com.se.part.search.bom.similar;

import java.util.List;

import com.se.part.search.dto.bom.BOMRow;

public interface SimilarPartsResolver
{
	List<BOMRow> similarParts(String partNumber, String manId);
}
