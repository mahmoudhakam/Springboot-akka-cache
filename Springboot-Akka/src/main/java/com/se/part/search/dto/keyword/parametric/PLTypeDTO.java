package com.se.part.search.dto.keyword.parametric;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "plType", "plTypeId", "mainCategoryList" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PLTypeDTO
{
	@JsonProperty("productLineType")
	private String plType;

	private String plTypeId;

	// @JacksonXmlElementWrapper(localName = "categories")
	// @JacksonXmlProperty(localName = "categories")
	@JsonProperty(value = "categories")
	private List<MainCategoryDTO> mainCategoryList = new ArrayList<MainCategoryDTO>();

	public String getPlType()
	{
		return plType;
	}

	public void setPlType(String plType)
	{
		this.plType = plType;
	}

	public String getPlTypeId()
	{
		return plTypeId;
	}

	public void setPlTypeId(String plTypeId)
	{
		this.plTypeId = plTypeId;
	}

	public List<MainCategoryDTO> getMainCategoryList()
	{
		return mainCategoryList;
	}

	public void setMainCategoryList(List<MainCategoryDTO> mainCategoryList)
	{
		this.mainCategoryList = mainCategoryList;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((plTypeId == null) ? 0 : plTypeId.hashCode());
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
		PLTypeDTO other = (PLTypeDTO) obj;
		if(plTypeId == null)
		{
			if(other.plTypeId != null)
				return false;
		}
		else if(!plTypeId.equals(other.plTypeId))
			return false;
		return true;
	}
}
