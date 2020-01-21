
package com.se.part.search.dto.export.reach;

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
@JsonPropertyOrder({ "substanceIdentification", "requiredAuthorization", "substanceConcentration", "substanceLocation" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Substance
{

	@JsonProperty("substanceIdentification")
	private String substanceIdentification;
	@JsonProperty("requiredAuthorization")
	private String requiredAuthorization;
	@JsonProperty("substanceConcentration")
	private String substanceConcentration;
	@JsonProperty("substanceLocation")
	private String substanceLocation;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public Substance()
	{
	}

	@JsonProperty("substanceIdentification")
	public String getSubstanceIdentification()
	{
		return substanceIdentification;
	}

	@JsonProperty("substanceIdentification")
	public void setSubstanceIdentification(String substanceIdentification)
	{
		this.substanceIdentification = substanceIdentification;
	}

	@JsonProperty("requiredAuthorization")
	public String getRequiredAuthorization()
	{
		return requiredAuthorization;
	}

	@JsonProperty("requiredAuthorization")
	public void setRequiredAuthorization(String requiredAuthorization)
	{
		this.requiredAuthorization = requiredAuthorization;
	}

	@JsonProperty("substanceConcentration")
	public String getSubstanceConcentration()
	{
		return substanceConcentration;
	}

	@JsonProperty("substanceConcentration")
	public void setSubstanceConcentration(String substanceConcentration)
	{
		this.substanceConcentration = substanceConcentration;
	}

	@JsonProperty("substanceLocation")
	public String getSubstanceLocation()
	{
		return substanceLocation;
	}

	@JsonProperty("substanceLocation")
	public void setSubstanceLocation(String substanceLocation)
	{
		this.substanceLocation = substanceLocation;
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

}
