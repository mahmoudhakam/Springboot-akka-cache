package com.se.part.search.dto.keyword.parametric;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "mainId", "mainCategory", "partsCount", "searchableAsBase", "subCategoryDTOs" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MainCategoryDTO
{
	// @JacksonXmlProperty(localName = "id")
	@JsonProperty(value = "id")
	private String mainId;

	// @JacksonXmlProperty(localName = "name")
	@JsonProperty(value = "name")
	private String mainCategory;

	// @JacksonXmlProperty(localName = "productCount")
	@JsonProperty(value = "productCount")
	private long partsCount = -1;

	// @JacksonXmlElementWrapper(localName = "subCategories")
	// @JacksonXmlProperty(localName = "subCategories")
	@JsonProperty(value = "subCategories")
	private List<SubCategoryDTO> subCategoryDTOs = new ArrayList<SubCategoryDTO>();

	// @JacksonXmlProperty(localName = "searchableAsBase")
	@JsonProperty("searchableAsBase")
	private boolean searchableAsBase;

	public String getMainId()
	{
		return mainId;
	}

	public void setMainId(String mainId)
	{
		this.mainId = mainId;
	}

	public String getMainCategory()
	{
		return mainCategory;
	}

	public void setMainCategory(String mainCategory)
	{
		this.mainCategory = mainCategory;
	}

	public void setPartsCount(Long partsCount)
	{
		this.partsCount = partsCount;
	}

	public List<SubCategoryDTO> getSubCategoryDTOs()
	{
		return subCategoryDTOs;
	}

	public void setSubCategoryDTOs(List<SubCategoryDTO> subCategoryDTOs)
	{
		this.subCategoryDTOs = subCategoryDTOs;
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

	public long getPartsCount()
	{
		if(partsCount != -1)
		{
			return partsCount;
		}
		partsCount = 0;
		for(SubCategoryDTO sub : subCategoryDTOs)
		{
			partsCount += sub.getPartsCount();
		}
		return partsCount;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mainId == null) ? 0 : mainId.hashCode());
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
		MainCategoryDTO other = (MainCategoryDTO) obj;
		if(mainId == null)
		{
			if(other.mainId != null)
				return false;
		}
		else if(!mainId.equals(other.mainId))
			return false;
		return true;
	}
}
