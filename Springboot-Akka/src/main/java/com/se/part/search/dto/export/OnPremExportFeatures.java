package com.se.part.search.dto.export;

public class OnPremExportFeatures
{
	private int id;
	private String featureKey;
	private int parentFeatureId;
	private String jsonKey;
	private boolean isActive;

	public OnPremExportFeatures()
	{
	}

	public OnPremExportFeatures(int id, String featureKey, int parentFeatureId, String jsonKey, boolean isActive)
	{
		super();
		this.id = id;
		this.featureKey = featureKey;
		this.parentFeatureId = parentFeatureId;
		this.jsonKey = jsonKey;
		this.isActive = isActive;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getFeatureKey()
	{
		return featureKey;
	}

	public void setFeatureKey(String featureKey)
	{
		this.featureKey = featureKey;
	}

	public int getParentFeatureId()
	{
		return parentFeatureId;
	}

	public void setParentFeatureId(int parentFeatureId)
	{
		this.parentFeatureId = parentFeatureId;
	}

	public String getJsonKey()
	{
		return jsonKey;
	}

	public void setJsonKey(String jsonKey)
	{
		this.jsonKey = jsonKey;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}

}
