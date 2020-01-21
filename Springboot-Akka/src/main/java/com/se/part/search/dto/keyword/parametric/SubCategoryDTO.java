package com.se.part.search.dto.keyword.parametric;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "subID", "subName", "searchableAsBase", "partsCount", "productLineList" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SubCategoryDTO
{
	@JsonProperty("name")
	private String subName;

	@JsonProperty("id")
	private String subID;

	// @JacksonXmlElementWrapper(localName = "productLines")
	// @JacksonXmlProperty(localName = "productLines")
	@JsonProperty(value = "productLines")
	private List<ProductLineDTO> productLineList = new ArrayList<ProductLineDTO>();

	@JsonProperty("searchableAsBase")
	private boolean searchableAsBase;

	@JsonProperty("productCount")
	private long partsCount = -1;

	public String getSubName()
	{
		return subName;
	}

	public void setSubName(String subName)
	{
		this.subName = subName;
	}

	public String getSubID()
	{
		return subID;
	}

	public void setSubID(String subID)
	{
		this.subID = subID;
	}

	public List<ProductLineDTO> getProductLineList()
	{
		return productLineList;
	}

	public void setProductLineList(List<ProductLineDTO> productLineList)
	{
		this.productLineList = productLineList;
	}

	public boolean getSearchableAsBase()
	{
		return searchableAsBase;
	}

	public void setSearchableAsBase(boolean searchableAsBase)
	{
		if(this.searchableAsBase)
			return;
		this.searchableAsBase = searchableAsBase;
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
		result = prime * result + ((subID == null) ? 0 : subID.hashCode());
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
		SubCategoryDTO other = (SubCategoryDTO) obj;
		if(subID == null)
		{
			if(other.subID != null)
				return false;
		}
		else if(!subID.equals(other.subID))
			return false;
		return true;
	}

	public long getPartsCount()
	{
		if(partsCount != -1)
		{
			return partsCount;
		}
		partsCount = 0;
		for(ProductLineDTO dto : productLineList)
		{
			partsCount += dto.getPartsCount();
		}
		return partsCount;
	}
}
