package com.se.part.search.dto.keyword;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "name", "count" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FacetCategory
{
	@JsonProperty("name")
	private String name;

	@JsonProperty("count")
	private long count;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getCount()
	{
		return count;
	}

	public void setCount(long count)
	{
		this.count = count;
	}
}
