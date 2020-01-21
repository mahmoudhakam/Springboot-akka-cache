package com.se.part.search.bom.messages;

import com.se.part.search.dto.keyword.Constants;

public enum BOMRequestType
{
	Exact(Constants.BOM_VALIDATION_STRATEGY_EXACT), Passive(Constants.BOM_VALIDATION_STRATEGY_PASSIVE), Lookup(Constants.BOM_VALIDATION_STRATEGY_LOOKUP), BeginWith(Constants.BOM_VALIDATION_STRATEGY_BEGINWITH), Similar(
			Constants.BOM_VALIDATION_STRATEGY_SIMILAR);

	private final String stepName;

	private BOMRequestType(String stepName)
	{
		this.stepName = stepName;
	}

	public String stepName()
	{
		// TODO Auto-generated method stub
		return stepName;
	}
}
