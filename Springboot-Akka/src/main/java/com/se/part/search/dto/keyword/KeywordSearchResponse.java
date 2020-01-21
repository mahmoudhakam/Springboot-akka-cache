package com.se.part.search.dto.keyword;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.part.search.dto.PartSearchStep;
import com.se.part.search.dto.partSearch.PartSearchDTO;
import com.se.part.search.messages.PartSearchStatus;

@JsonPropertyOrder({ "status", "keywordResults", "steps" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KeywordSearchResponse
{

	@JsonProperty("Status")
	private PartSearchStatus status;

	@JsonProperty("Result")
	private List<PartSearchDTO> keywordResults;

	@JsonProperty("Steps")
	private List<PartSearchStep> steps;

	public KeywordSearchResponse()
	{
	}

	public KeywordSearchResponse(PartSearchStatus status)
	{
		super();
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

	public List<PartSearchDTO> getKeywordResults()
	{
		return keywordResults;
	}

	public void setKeywordResults(List<PartSearchDTO> keywordResults)
	{
		this.keywordResults = keywordResults;
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
