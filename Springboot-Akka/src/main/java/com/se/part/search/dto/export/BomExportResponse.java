package com.se.part.search.dto.export;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.part.search.dto.PartSearchStep;
import com.se.part.search.dto.keyword.Status;

@JsonPropertyOrder({ "status", "serviceTime", "bomResult", "steps" })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BomExportResponse
{
	@JsonProperty("Status")
	private Status status;
	@JsonProperty("Steps")
	private List<PartSearchStep> steps;
	@JsonProperty("PartList")
	private Object respose;
	@JsonProperty("ServiceTime")
	private String serviceTime;

	public BomExportResponse(Status status)
	{
		this.status = status;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public List<PartSearchStep> getSteps()
	{
		return steps;
	}

	public void setSteps(List<PartSearchStep> steps)
	{
		this.steps = steps;
	}

	public Object getRespose()
	{
		return respose;
	}

	public void setRespose(Object respose)
	{
		this.respose = respose;
	}

	public String getServiceTime()
	{
		return serviceTime;
	}

	public void setServiceTime(String serviceTime)
	{
		this.serviceTime = serviceTime;
	}

}
