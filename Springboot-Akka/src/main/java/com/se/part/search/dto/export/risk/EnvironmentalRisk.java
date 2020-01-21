
package com.se.part.search.dto.export.risk;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "riskGrade", "riskScore", "rohsStatus", "formattedRiskGrade" })
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentalRisk implements Serializable
{

	@JsonProperty("riskGrade")
	private String riskGrade;
	@JsonProperty("riskScore")
	private Long riskScore;
	@JsonProperty("rohsStatus")
	private String rohsStatus;
	@JsonProperty("formattedRiskGrade")
	private String formattedRiskGrade;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private final static long serialVersionUID = -6222595878797442420L;

	@JsonProperty("riskGrade")
	public String getRiskGrade()
	{
		return riskGrade;
	}

	@JsonProperty("riskGrade")
	public void setRiskGrade(String riskGrade)
	{
		this.riskGrade = riskGrade;
	}

	public EnvironmentalRisk withRiskGrade(String riskGrade)
	{
		this.riskGrade = riskGrade;
		return this;
	}

	@JsonProperty("riskScore")
	public Long getRiskScore()
	{
		return riskScore;
	}

	@JsonProperty("riskScore")
	public void setRiskScore(Long riskScore)
	{
		this.riskScore = riskScore;
	}

	public EnvironmentalRisk withRiskScore(Long riskScore)
	{
		this.riskScore = riskScore;
		return this;
	}

	@JsonProperty("rohsStatus")
	public String getRohsStatus()
	{
		return rohsStatus;
	}

	@JsonProperty("rohsStatus")
	public void setRohsStatus(String rohsStatus)
	{
		this.rohsStatus = rohsStatus;
	}

	public EnvironmentalRisk withRohsStatus(String rohsStatus)
	{
		this.rohsStatus = rohsStatus;
		return this;
	}

	@JsonProperty("formattedRiskGrade")
	public String getFormattedRiskGrade()
	{
		return formattedRiskGrade;
	}

	@JsonProperty("formattedRiskGrade")
	public void setFormattedRiskGrade(String formattedRiskGrade)
	{
		this.formattedRiskGrade = formattedRiskGrade;
	}

	public EnvironmentalRisk withFormattedRiskGrade(String formattedRiskGrade)
	{
		this.formattedRiskGrade = formattedRiskGrade;
		return this;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties()
	{
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value)
	{
		this.additionalProperties.put(name, value);
	}

	public EnvironmentalRisk withAdditionalProperty(String name, Object value)
	{
		this.additionalProperties.put(name, value);
		return this;
	}

}
