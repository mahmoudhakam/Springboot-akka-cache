package com.se.part.search.dto.partDetails;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.part.search.dto.PartSearchStep;
import com.se.part.search.messages.PartSearchStatus;

@JsonPropertyOrder({ "status", "partsList" })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartDetailsResponse
{

	@JsonProperty("Status")
	private PartSearchStatus status;

	@JsonProperty("PartsList")
	private List<PartDetailsDTO> partsList;

	@JsonProperty("Steps")
	private List<PartSearchStep> steps;

	public PartDetailsResponse(PartSearchStatus status)
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

	public List<PartDetailsDTO> getPartsList()
	{
		return partsList;
	}

	public void setPartsList(List<PartDetailsDTO> partsList)
	{
		this.partsList = partsList;
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
