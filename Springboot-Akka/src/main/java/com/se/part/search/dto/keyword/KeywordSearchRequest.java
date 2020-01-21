package com.se.part.search.dto.keyword;

import com.se.part.search.dto.ParentSearchRequest;

public class KeywordSearchRequest extends ParentSearchRequest
{
	private String partNumber;
	private String keyword;
	private String wildcardSingle;
	private String wildCardMulti;

	public String getPartNumber()
	{
		return partNumber;
	}

	public void setPartNumber(String partNumber)
	{
		this.partNumber = partNumber;
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

	public String getKeyword()
	{
		return keyword;
	}

	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

}
