
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
@JsonPropertyOrder({ "riskScore", "riskGrade", "crossPartCategory", "formattedRiskGrade" })
@XmlAccessorType(XmlAccessType.FIELD)
public class MultisourceRisk implements Serializable
{

	@JsonProperty("riskScore")
	private Long riskScore;
	@JsonProperty("riskGrade")
	private String riskGrade;
	@JsonProperty("crossPartCategory")
	private String crossPartCategory;
	@JsonProperty("formattedRiskGrade")
	private String formattedRiskGrade;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private final static long serialVersionUID = -1320461057114227284L;

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

	public MultisourceRisk withRiskScore(Long riskScore)
	{
		this.riskScore = riskScore;
		return this;
	}

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

	public MultisourceRisk withRiskGrade(String riskGrade)
	{
		this.riskGrade = riskGrade;
		return this;
	}

	@JsonProperty("crossPartCategory")
	public String getCrossPartCategory()
	{
		return crossPartCategory;
	}

	@JsonProperty("crossPartCategory")
	public void setCrossPartCategory(String crossPartCategory)
	{
		this.crossPartCategory = crossPartCategory;
	}

	public MultisourceRisk withCrossPartCategory(String crossPartCategory)
	{
		this.crossPartCategory = crossPartCategory;
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

	public MultisourceRisk withFormattedRiskGrade(String formattedRiskGrade)
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

	public MultisourceRisk withAdditionalProperty(String name, Object value)
	{
		this.additionalProperties.put(name, value);
		return this;
	}

}
