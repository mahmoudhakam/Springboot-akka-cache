package com.se.part.search.dto.partSearch;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartSearchResult
{
	@JsonProperty("ReqPartNumber")
	private String requestedMPN;
	@JsonProperty("ReqManufacturer")
	private String requestedMan;
	@JsonProperty("ReqKeyword")
	private String requestedKeyword;

	@JsonProperty("PartList")
	private List<PartSearchDTO> partResult = new ArrayList<>();

	public String getRequestedMPN()
	{
		return requestedMPN;
	}

	public void setRequestedMPN(String requestedMPN)
	{
		this.requestedMPN = requestedMPN;
	}

	public String getRequestedMan()
	{
		return requestedMan;
	}

	public void setRequestedMan(String requestedMan)
	{
		this.requestedMan = requestedMan;
	}

	public List<PartSearchDTO> getPartResult()
	{
		return partResult;
	}

	public void setPartResult(List<PartSearchDTO> partResult)
	{
		this.partResult = partResult;
	}

	public String getRequestedKeyword()
	{
		return requestedKeyword;
	}

	public void setRequestedKeyword(String requestedKeyword)
	{
		this.requestedKeyword = requestedKeyword;
	}

}
