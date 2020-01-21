package com.se.part.search.dto.export;

public class ExportParametricFeaturesDTO
{
	private String FeatureName;
	private String FeatureUnit;
	private String FeatureValue;

	public ExportParametricFeaturesDTO()
	{
	}

	public String getFeatureName()
	{
		return FeatureName;
	}

	public void setFeatureName(String featureName)
	{
		FeatureName = featureName;
	}

	public String getFeatureUnit()
	{
		return FeatureUnit;
	}

	public void setFeatureUnit(String featureUnit)
	{
		FeatureUnit = featureUnit;
	}

	public String getFeatureValue()
	{
		return FeatureValue;
	}

	public void setFeatureValue(String featureValue)
	{
		FeatureValue = featureValue;
	}

}
