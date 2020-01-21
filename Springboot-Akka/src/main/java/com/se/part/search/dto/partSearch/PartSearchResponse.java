package com.se.part.search.dto.partSearch;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.part.search.dto.PartSearchStep;
import com.se.part.search.messages.PartSearchStatus;

@JsonPropertyOrder({ "status", "partList" })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartSearchResponse
{
	@JsonProperty("Status")
	private PartSearchStatus status;
	@JsonProperty("PartResult")
	private List<PartSearchResult> partList;
	@JsonProperty("Steps")
	private List<PartSearchStep> steps;

	public PartSearchResponse(PartSearchStatus status)
	{
		this.status = status;
	}

	public PartSearchStatus getStatus()
	{
		return status;
	}

	public void setStatus(PartSearchStatus status)
	{
		this.status = status;
	}

	public List<PartSearchResult> getPartList()
	{
		return partList;
	}

	public void setPartList(List<PartSearchResult> partList)
	{
		this.partList = partList;
	}

	public List<PartSearchStep> getSteps()
	{
		return steps;
	}

	public void setSteps(List<PartSearchStep> steps)
	{
		this.steps = steps;
	}
}
