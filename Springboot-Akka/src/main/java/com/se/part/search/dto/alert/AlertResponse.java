package com.se.part.search.dto.alert;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.se.part.search.dto.PartSearchStep;
import com.se.part.search.messages.PartSearchStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertResponse
{

	@JsonProperty("status")
	private PartSearchStatus status;

	@JsonProperty("alertList")
	private List<AlertDataDTO> alertList;

	@JsonProperty("Steps")
	private List<PartSearchStep> steps;

	public List<PartSearchStep> getSteps()
	{
		return steps;
	}

	public void setSteps(List<PartSearchStep> steps)
	{
		this.steps = steps;
	}

	public AlertResponse(PartSearchStatus status)
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

	public List<AlertDataDTO> getAlertList()
	{
		return alertList;
	}

	public void setAlertList(List<AlertDataDTO> alertList)
	{
		this.alertList = alertList;
	}

}
