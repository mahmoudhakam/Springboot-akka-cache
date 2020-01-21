package com.se.part.search.dto.keyword.parametric;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "plID", "plName", "partsCount" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductLineDTO
{
	@JsonProperty("name")
	private String plName;

	@JsonProperty("id")
	private String plID;

	@JsonProperty("productCount")
	private Long partsCount;

	public String getPlName()
	{
		return plName;
	}

	public void setPlName(String plName)
	{
		this.plName = plName;
	}

	public String getPlID()
	{
		return plID;
	}

	public void setPlID(String plID)
	{
		this.plID = plID;
	}

	public Long getPartsCount()
	{
		return partsCount;
	}

	public void setPartsCount(Long partsCount)
	{
		this.partsCount = partsCount;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((plID == null) ? 0 : plID.hashCode());
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
		ProductLineDTO other = (ProductLineDTO) obj;
		if(plID == null)
		{
			if(other.plID != null)
				return false;
		}
		else if(!plID.equals(other.plID))
			return false;
		return true;
	}
}
