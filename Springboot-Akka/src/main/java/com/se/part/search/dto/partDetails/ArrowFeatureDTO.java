package com.se.part.search.dto.partDetails;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "fetName", "fetId", "unit", "sortType", "featureValues" })
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class ArrowFeatureDTO implements Serializable
{

	@JsonProperty("FeatureName")
	private String fetName;
	@JsonProperty("FeatureValue")
	private String featureValue;
	@JsonProperty("FeatureUnit")
	private String unit;
	@JsonIgnore
	@XmlTransient
	private String featureDefinition;
	@JsonIgnore
	@XmlTransient
	private String hcolName;
	@JsonIgnore
	@XmlTransient
	private String fetId;

	@JsonIgnore
	@XmlTransient
	private int expertSheetOrder;

	@JsonIgnore
	@XmlTransient
	private String plID;

	@JsonIgnore
	@XmlTransient
	private Map<String, ArrowFeatureValueDTO> valuesMap = new HashMap<>();

	@JsonIgnore
	@XmlTransient
	private String sortType;

	@JsonProperty("FeatureValues")
	private List<ArrowFeatureValueDTO> featureValues;

	@JsonIgnore
	@XmlTransient
	private String mainName;
	@JsonIgnore
	@XmlTransient
	private String subName;

	@JsonIgnore
	@XmlTransient
	private Boolean basicFeature;
	@JsonIgnore
	@XmlTransient
	private String solrFieldName;

	public int getExpertSheetOrder()
	{
		return expertSheetOrder;
	}

	public void setExpertSheetOrder(int expertSheetOrder)
	{
		this.expertSheetOrder = expertSheetOrder;
	}

	public ArrowFeatureDTO()
	{
	}

	public ArrowFeatureDTO(String fetName)
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

	public List<ArrowFeatureValueDTO> getFeatureValues()
	{
		return featureValues;
	}

	public void setFeatureValues(List<ArrowFeatureValueDTO> featureValue)
	{
		this.featureValues = featureValue;
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
		ArrowFeatureDTO other = (ArrowFeatureDTO) obj;
		if(fetName == null)
		{
			if(other.fetName != null)
				return false;
		}
		else if(!fetName.equalsIgnoreCase(other.fetName))
			return false;
		return true;
	}

	public Boolean isBasicFeature()
	{
		return basicFeature;
	}

	public void setBasicFeature(Boolean basicFeature)
	{
		this.basicFeature = basicFeature;
	}

	public String getFeatureValue()
	{
		return featureValue;
	}

	public void setFeatureValue(String featureValue)
	{
		this.featureValue = featureValue;
	}

	public String getSolrFieldName()
	{
		return solrFieldName;
	}

	public void setSolrFieldName(String solrFieldName)
	{
		this.solrFieldName = solrFieldName;
	}

	public Map<String, ArrowFeatureValueDTO> getValuesMap()
	{
		return valuesMap;
	}

	public void setValuesMap(Map<String, ArrowFeatureValueDTO> valuesMap)
	{
		this.valuesMap = valuesMap;
	}

	public String getPlID()
	{
		return plID;
	}

	public void setPlID(String plID)
	{
		this.plID = plID;
	}

	@Override
	public String toString()
	{
		return "FeatureDTO [fetName=" + fetName + ", fetId=" + fetId + ", unit=" + unit + ", featureValue=" + featureValue + ", hcolName=" + hcolName + "]";
	}

}
