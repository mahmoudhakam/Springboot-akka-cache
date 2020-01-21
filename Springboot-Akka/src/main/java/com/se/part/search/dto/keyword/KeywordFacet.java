package com.se.part.search.dto.keyword;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "name", "count" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KeywordFacet
{
	private String name;
	private Long count;

	public KeywordFacet(String name, Long count)
	{
		super();
		this.name = name;
		this.count = count;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Long getCount()
	{
		return count;
	}

	public void setCount(Long count)
	{
		this.count = count;
	}

}
