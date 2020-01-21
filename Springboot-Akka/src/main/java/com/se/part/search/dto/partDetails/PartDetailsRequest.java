package com.se.part.search.dto.partDetails;

import com.se.part.search.dto.ParentSearchRequest;

public class PartDetailsRequest extends ParentSearchRequest
{

	private String comIDs;

	public String getComIDs()
	{
		return comIDs;
	}

	public void setComIDs(String comIDs)
	{
		this.comIDs = comIDs;
	}

}
