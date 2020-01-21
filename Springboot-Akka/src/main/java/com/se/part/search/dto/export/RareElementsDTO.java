package com.se.part.search.dto.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "comId", "rareStatus" })
public class RareElementsDTO
{

	@JsonProperty("comId")
	private Integer comId;
	@JsonProperty("rareStatus")
	private String rareStatus;

	@JsonProperty("comId")
	public Integer getComId()
	{
		return comId;
	}

	@JsonProperty("comId")
	public void setComId(Integer comId)
	{
		this.comId = comId;
	}

	@JsonProperty("rareStatus")
	public String getRareStatus()
	{
		return rareStatus;
	}

	@JsonProperty("rareStatus")
	public void setRareStatus(String rareStatus)
	{
		this.rareStatus = rareStatus;
	}

}
