package com.se.part.search.dto.export.risk;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.se.part.search.dto.export.FeatureNameValueDTO;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiskDTO
{
	@JsonProperty("overallRiskGrade")
	private String overallRiskGrade;
	@JsonProperty("formattedRiskGrade")
	private String formattedRiskGrade;
	@JsonProperty("overallRiskScore")
	private Double overallRiskScore;
	@JsonProperty("overallRiskScorePercentage")
	private String overallRiskScorePercentage;
	@JsonProperty("lifeCycleRisk")
	private LifeCycleRisk lifeCycleRiskObject;
	@JsonProperty("multisourceRisk")
	private MultisourceRisk multisourceRiskObject;
	@JsonProperty("inventoryRisk")
	private InventoryRisk inventoryRiskObject;
	@JsonProperty("environmentalRisk")
	private EnvironmentalRisk environmentalRiskObject;
	@JsonProperty("lifeCycleForeCast") // for binding
	private List<LifeCycleForeCast> lifeCycleForeCast = new ArrayList<>();
	@JsonProperty("lcForeCast") // for response
	private List<FeatureNameValueDTO> fetNameValue = new ArrayList<>();

	public RiskDTO()
	{
	}

	public String getOverallRiskGrade()
	{
		return overallRiskGrade;
	}

	public void setOverallRiskGrade(String overallRiskGrade)
	{
		this.overallRiskGrade = overallRiskGrade;
	}

	public String getFormattedRiskGrade()
	{
		return formattedRiskGrade;
	}

	public void setFormattedRiskGrade(String formattedRiskGrade)
	{
		this.formattedRiskGrade = formattedRiskGrade;
	}

	public Double getOverallRiskScore()
	{
		return overallRiskScore;
	}

	public void setOverallRiskScore(Double overallRiskScore)
	{
		this.overallRiskScore = overallRiskScore;
	}

	public String getOverallRiskScorePercentage()
	{
		return overallRiskScorePercentage;
	}

	public void setOverallRiskScorePercentage(String overallRiskScorePercentage)
	{
		this.overallRiskScorePercentage = overallRiskScorePercentage;
	}

	public LifeCycleRisk getLifeCycleRiskObject()
	{
		return lifeCycleRiskObject;
	}

	public void setLifeCycleRiskObject(LifeCycleRisk lifeCycleRiskObject)
	{
		this.lifeCycleRiskObject = lifeCycleRiskObject;
	}

	public MultisourceRisk getMultisourceRiskObject()
	{
		return multisourceRiskObject;
	}

	public void setMultisourceRiskObject(MultisourceRisk multisourceRiskObject)
	{
		this.multisourceRiskObject = multisourceRiskObject;
	}

	public InventoryRisk getInventoryRiskObject()
	{
		return inventoryRiskObject;
	}

	public void setInventoryRiskObject(InventoryRisk inventoryRiskObject)
	{
		this.inventoryRiskObject = inventoryRiskObject;
	}

	public EnvironmentalRisk getEnvironmentalRiskObject()
	{
		return environmentalRiskObject;
	}

	public void setEnvironmentalRiskObject(EnvironmentalRisk environmentalRiskObject)
	{
		this.environmentalRiskObject = environmentalRiskObject;
	}

	public List<LifeCycleForeCast> getLifeCycleForeCast()
	{
		return lifeCycleForeCast;
	}

	public void setLifeCycleForeCast(List<LifeCycleForeCast> lifeCycleForeCast)
	{
		this.lifeCycleForeCast = lifeCycleForeCast;
	}

	public List<FeatureNameValueDTO> getFetNameValue()
	{
		return fetNameValue;
	}

	public void setFetNameValue(List<FeatureNameValueDTO> fetNameValue)
	{
		this.fetNameValue = fetNameValue;
	}

}