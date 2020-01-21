package com.se.part.search.dto.partDetails;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "value", "valueCount", "displayOrder", "valueID", "valueIDs" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ArrowFeatureValueDTO implements Serializable
{

	@JsonProperty("Value")
	private String value;

	@JsonIgnore
	@XmlTransient
	private String displayOrder;

	@JsonIgnore
	@XmlTransient
	private String valueID;

	@JsonProperty("ValueIDs")
	private List<String> valueIDs;

	@JsonProperty("PartsCount")
	private Long valueCount;

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public Long getValueCount()
	{
		return valueCount;
	}

	public void setValueCount(Long valueCount)
	{
		this.valueCount = valueCount;
	}

	public String getValueID()
	{
		return valueID;
	}

	public void setValueID(String valueID)
	{
		this.valueID = valueID;
	}

	public String getDisplayOrder()
	{
		return displayOrder;
	}

	public void setDisplayOrder(String displayOrder)
	{
		this.displayOrder = displayOrder;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((valueID == null) ? 0 : valueID.hashCode());
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
		ArrowFeatureValueDTO other = (ArrowFeatureValueDTO) obj;
		if(valueID == null)
		{
			if(other.valueID != null)
				return false;
		}
		else if(!valueID.equals(other.valueID))
			return false;
		return true;
	}

	public List<String> getValueIDs()
	{
		return valueIDs;
	}

	public void setValueIDs(List<String> valueIDs)
	{
		this.valueIDs = valueIDs;
	}

	public void addValueId(String valueIdToAdd)
	{
		if(valueIDs == null)
		{
			valueIDs = new LinkedList<>();
		}
		valueIDs.add(valueIdToAdd);

	}

	public void addValueId(List<String> list)
	{
		if(valueIDs == null)
		{
			valueIDs = new LinkedList<>();
		}
		valueIDs.addAll(list);

	}

}
