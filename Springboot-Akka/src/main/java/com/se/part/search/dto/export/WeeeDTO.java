package com.se.part.search.dto.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "comId", "weeeStatus", "sourcePdf", "sourceType" })
public class WeeeDTO
{

	@JsonProperty("comId")
	private Integer comId;
	@JsonProperty("weeeStatus")
	private String weeeStatus;
	@JsonProperty("sourcePdf")
	private String sourcePdf;
	@JsonProperty("sourceType")
	private String sourceType;

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

	@JsonProperty("weeeStatus")
	public String getWeeeStatus()
	{
		return weeeStatus;
	}

	@JsonProperty("weeeStatus")
	public void setWeeeStatus(String weeeStatus)
	{
		this.weeeStatus = weeeStatus;
	}

	@JsonProperty("sourcePdf")
	public String getSourcePdf()
	{
		return sourcePdf;
	}

	@JsonProperty("sourcePdf")
	public void setSourcePdf(String sourcePdf)
	{
		this.sourcePdf = sourcePdf;
	}

	@JsonProperty("sourceType")
	public String getSourceType()
	{
		return sourceType;
	}

	@JsonProperty("sourceType")
	public void setSourceType(String sourceType)
	{
		this.sourceType = sourceType;
	}

}
