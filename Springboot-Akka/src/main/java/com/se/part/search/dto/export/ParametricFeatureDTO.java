package com.se.part.search.dto.export;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.FIELD)
public class ParametricFeatureDTO implements BOMResultParent
{
	@JsonProperty("FeatureName")
	private String featureName;
	@JsonProperty("FeatureUnit")
	private String featureUnit;
	@JsonProperty("FeatureValue")
	private String featureValue;

	public ParametricFeatureDTO()
	{
	}

	public String getFeatureName()
	{
		return featureName;
	}

	public void setFeatureName(String featureName)
	{
		this.featureName = featureName;
	}

	public String getFeatureUnit()
	{
		return featureUnit;
	}

	public void setFeatureUnit(String featureUnit)
	{
		this.featureUnit = featureUnit;
	}

	public String getFeatureValue()
	{
		return featureValue;
	}

	public void setFeatureValue(String featureValue)
	{
		this.featureValue = featureValue;
	}

}
