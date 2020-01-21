package com.se.part.search.services.keywordSearch.comparator;

import java.math.BigDecimal;
import java.util.Comparator;

import com.se.part.search.dto.keyword.parametric.FeatureValueDTO;
import com.se.part.search.services.keywordSearch.Util;

public class FeatureComparator implements Comparator<FeatureValueDTO>
{
	private Util<FeatureValueDTO> util = new Util<FeatureValueDTO>();

	@Override
	public int compare(FeatureValueDTO o1, FeatureValueDTO o2)
	{
		String v1 = o1.getValue();
		String v2 = o2.getValue();

		if(!util.isNullSpacesOrEmptyOrNULL(v1) && !util.isNullSpacesOrEmptyOrNULL(v2))
		{
			if(v1.indexOf("!") > -1 && v1.substring(0, v1.indexOf("!")) != null && !v1.substring(0, v1.indexOf("!")).isEmpty())
			{
				v1 = v1.substring(0, v1.indexOf("!"));
			}
			else
			{
				// sort values with no display order last
				v1 = "9999999999";
			}

			if(v2.indexOf("!") > -1 && v2.substring(0, v2.indexOf("!")) != null && !v2.substring(0, v2.indexOf("!")).isEmpty())
			{
				v2 = v2.substring(0, v2.indexOf("!"));
			}
			else
			{
				v2 = "9999999999";
			}

			if(v1.length() == 0)
			{
				v1 = "9999999999";
			}

			if(v2.length() == 0)
			{
				v2 = "9999999999";
			}
			if(v1.equalsIgnoreCase(v2))
			{
				return v1.toLowerCase().compareTo(v2.toLowerCase());
			}
			else
			{
				BigDecimal bigDecimal1 = new BigDecimal(v1);
				BigDecimal bigDecimal2 = new BigDecimal(v2);

				return bigDecimal1.compareTo(bigDecimal2);
			}

		}
		return 0;
	}

}
