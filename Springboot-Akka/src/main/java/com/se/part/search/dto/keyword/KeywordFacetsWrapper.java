package com.se.part.search.dto.keyword;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "mainCategories", "subCategories", "manufacturer", "lifeCycle", "rohs" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KeywordFacetsWrapper
{
	@JsonProperty("Categories")
	private List<KeywordFacet> mainCategories;

	@JsonProperty("SubCategories")
	private List<KeywordFacet> subCategories;

	@JsonProperty("Manufacturer")
	private List<KeywordFacet> manufacturer;

	@JsonProperty("LifeCycle")
	private List<KeywordFacet> lifeCycle;

	@JsonProperty("ROHS")
	private List<KeywordFacet> rohs;

	public List<KeywordFacet> getMainCategories()
	{
		return mainCategories;
	}

	public void setMainCategories(List<KeywordFacet> mainCategories)
	{
		this.mainCategories = mainCategories;
	}

	public List<KeywordFacet> getSubCategories()
	{
		return subCategories;
	}

	public void setSubCategories(List<KeywordFacet> subCategories)
	{
		this.subCategories = subCategories;
	}

	public List<KeywordFacet> getManufacturer()
	{
		return manufacturer;
	}

	public void setManufacturer(List<KeywordFacet> manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public List<KeywordFacet> getLifeCycle()
	{
		return lifeCycle;
	}

	public void setLifeCycle(List<KeywordFacet> lifeCycle)
	{
		this.lifeCycle = lifeCycle;
	}

	public List<KeywordFacet> getRohs()
	{
		return rohs;
	}

	public void setRohs(List<KeywordFacet> rohs)
	{
		this.rohs = rohs;
	}

}
