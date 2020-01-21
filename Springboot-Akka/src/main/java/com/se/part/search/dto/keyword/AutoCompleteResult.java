package com.se.part.search.dto.keyword;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "partResult", "manResult", "descResult", "categoryResult" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutoCompleteResult
{

	@JsonProperty("PartResult")
	private List<String> partResult;

	@JsonProperty("ManufacturerResult")
	private List<String> manResult;

	@JsonProperty("DescriptionResult")
	private List<String> descResult;

	@JsonProperty("CategoryResult")
	private List<String> categoryResult;

	public List<String> getPartResult()
	{
		return partResult;
	}

	public void setPartResult(List<String> partResult)
	{
		this.partResult = partResult;
	}

	public List<String> getManResult()
	{
		return manResult;
	}

	public void setManResult(List<String> manResult)
	{
		this.manResult = manResult;
	}

	public List<String> getDescResult()
	{
		return descResult;
	}

	public void setDescResult(List<String> descResult)
	{
		this.descResult = descResult;
	}

	public List<String> getCategoryResult()
	{
		return categoryResult;
	}

	public void setCategoryResult(List<String> categoryResult)
	{
		this.categoryResult = categoryResult;
	}

	public boolean isEmpty()
	{
		// TODO Auto-generated method stub
		return (manResult == null || manResult.isEmpty()) && (categoryResult == null || categoryResult.isEmpty()) && (descResult == null || descResult.isEmpty()) && (partResult == null || partResult.isEmpty());
	}

}
