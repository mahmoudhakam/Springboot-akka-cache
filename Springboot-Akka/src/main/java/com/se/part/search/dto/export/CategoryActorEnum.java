package com.se.part.search.dto.export;

import com.se.part.search.dto.keyword.Constants;

public enum CategoryActorEnum
{
	PARAMETRIC(Constants.PARAMETRIC_STRATEGY), MANUFACTURER(Constants.MANUFACTURER_STRATEGY), PACKAGE(Constants.PACKAGE_STRATEGY), PCN(Constants.PCN_STRATEGY), PACKAGING(Constants.PACKAGING_STRATEGY), RISK(Constants.RISK_STRATEGY), CLASSIFICATION(
			Constants.CLASSIFICATION_STRATEGY), REACH(Constants.REACH_STRATEGY), QUALIFICATION(Constants.QUALIFICATIONS_STRATEGY), RAREELEMENTS(Constants.RAREELEMENTS_STRATEGY), CHINAROHS(Constants.CHINAROHS_STRATEGY), WEEE(
					Constants.WEEE_STRATEGY), COOS(Constants.COOS_STRATEGY), PRICE(
							Constants.PRICE_STRATEGY), ROHS(Constants.ROHS_STRATEGY), SUMMARY(Constants.SUMMARY_STRATEGY), CONFLICT_MINIRALS(Constants.CONFLICT_MINIRALS_STRATEGY), GENERAL_PASSIVE(Constants.GENERAL_PASSIVE_STRATEGY);
	private final String categoryName;

	CategoryActorEnum(String categoryName)
	{
		this.categoryName = categoryName;
	}

	public String getCategoryName()
	{
		return categoryName;
	}
}
