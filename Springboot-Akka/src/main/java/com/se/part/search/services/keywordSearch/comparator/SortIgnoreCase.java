package com.se.part.search.services.keywordSearch.comparator;

import java.util.Comparator;

import com.se.part.search.dto.keyword.parametric.FeatureValueDTO;

public class SortIgnoreCase implements Comparator<FeatureValueDTO>
{
	@Override
	public int compare(FeatureValueDTO s1, FeatureValueDTO s2)
	{
		return s1.getValue().toLowerCase().compareTo(s2.getValue().toLowerCase());
	}

}
