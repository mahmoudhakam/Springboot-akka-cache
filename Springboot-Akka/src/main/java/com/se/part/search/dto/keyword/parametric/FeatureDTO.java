package com.se.part.search.dto.keyword.parametric;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "fetName", "fetId", "unit", "sortType", "values", "packageFlag" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureDTO implements Serializable
{
	@JsonProperty("FeatureName")
	private String fetName;

	@JsonProperty("fetID")
	private String fetId;

	@JsonProperty("FeatureUnit")
	private String unit;

	@JsonProperty("FeatureType")
	private String sortType;

	@JsonProperty("FeatureValues")
	private List<FeatureValueDTO> values;

	@JsonProperty("FeatureValue")
	private String featureValue;

	@JsonProperty("PackageFlag")
	private String packageFlag;

	private String hcolName;
	private String mainName;
	private String subName;
	private String featureDefinition;

	public FeatureDTO()
	{
	}

	public FeatureDTO(String fetName)
	{

		this.fetName = fetName;
	}

	public String getFetName()
	{
		return fetName;
	}

	public void setFetName(String fetName)
	{
		this.fetName = fetName;
	}

	public String getHcolName()
	{
		return hcolName;
	}

	public void setHcolName(String hcolName)
	{
		this.hcolName = hcolName;
	}

	public String getUnit()
	{
		return unit;
	}

	public void setUnit(String unit)
	{
		this.unit = unit;
	}

	public void setValues(List<FeatureValueDTO> values)
	{
		this.values = values;
	}

	public String getSortType()
	{
		return sortType;
	}

	public void setSortType(String sortType)
	{
		this.sortType = sortType;
	}

	public String getFetId()
	{
		return fetId;
	}

	public void setFetId(String fetId)
	{
		this.fetId = fetId;
	}

	public String getFeatureDefinition()
	{
		return featureDefinition;
	}

	public void setFeatureDefinition(String featureDefinition)
	{
		this.featureDefinition = featureDefinition;
	}

	public String getMainName()
	{
		return mainName;
	}

	public void setMainName(String mainName)
	{
		this.mainName = mainName;
	}

	public String getSubName()
	{
		return subName;
	}

	public void setSubName(String subName)
	{
		this.subName = subName;
	}

	public List<FeatureValueDTO> getValues()
	{
		return values;
	}

	public String getFeatureValue()
	{
		return featureValue;
	}

	public void setFeatureValue(String featureValue)
	{
		this.featureValue = featureValue;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fetName == null) ? 0 : fetName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		FeatureDTO other = (FeatureDTO) obj;
		if(fetName == null)
		{
			if(other.fetName != null)
				return false;
		}
		else if(!fetName.equalsIgnoreCase(other.fetName))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "FeatureDTO [fetName=" + fetName + ", hcolName=" + hcolName + ", unit=" + unit + ", sortType=" + sortType + ", featureValue=" + featureValue + ", values=" + values;
	}

	public String getPackageFlag()
	{
		return packageFlag;
	}

	public void setPackageFlag(String packageFlag)
	{
		this.packageFlag = packageFlag;
	}
}
