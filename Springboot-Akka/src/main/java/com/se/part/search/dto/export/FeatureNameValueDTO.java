package com.se.part.search.dto.export;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureNameValueDTO
{
	@JsonProperty("FeatureName")
	private String featureName;
	@JsonProperty("FeatureValue")
	private Object featureValue;
	@JsonProperty("FeatureUnit")
	private String featureUnit;

	public FeatureNameValueDTO()
	{
	}

	public FeatureNameValueDTO(String featureName, Object featureValue)
	{
		super();
		this.featureName = featureName;
		this.featureValue = featureValue;
	}

	public String getFeatureName()
	{
		return featureName;
	}

	public void setFeatureName(String featureName)
	{
		this.featureName = featureName;
	}

	public Object getFeatureValue()
	{
		return featureValue;
	}

	public void setFeatureValue(String featureValue)
	{
		this.featureValue = featureValue;
	}

	public String getFeatureUnit()
	{
		return featureUnit;
	}

	public void setFeatureUnit(String featureUnit)
	{
		this.featureUnit = featureUnit;
	}

	@Override
	public String toString()
	{
		return "FeatureNameValueDTO [featureName=" + featureName + ", featureValue=" + featureValue + ", featureUnit=" + featureUnit + "]";
	}


}
