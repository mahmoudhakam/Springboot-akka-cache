package com.se.part.search.dto.partSearch;

import com.se.part.search.dto.ParentSearchRequest;

public class PartSearchRequest extends ParentSearchRequest
{
	private String partNumber;
	private String mode;
	private String wildcardSingle;
	private String wildCardMulti;
	private String start;
	private String excludedParts;

	public String getPartNumber()
	{
		return partNumber;
	}

	public void setPartNumber(String partNumber)
	{
		this.partNumber = partNumber;
	}

	public String getMode()
	{
		return mode;
	}

	public void setMode(String mode)
	{
		this.mode = mode;
	}

	public String getWildcardSingle()
	{
		return wildcardSingle;
	}

	public void setWildcardSingle(String wildcardSingle)
	{
		this.wildcardSingle = wildcardSingle;
	}

	public String getWildCardMulti()
	{
		return wildCardMulti;
	}

	public void setWildCardMulti(String wildCardMulti)
	{
		this.wildCardMulti = wildCardMulti;
	}

	public String getStart()
	{
		return start;
	}

	public void setStart(String start)
	{
		this.start = start;
	}

	public String getExcludedParts()
	{
		return excludedParts;
	}

	public void setExcludedParts(String excludedParts)
	{
		this.excludedParts = excludedParts;
	}

}
