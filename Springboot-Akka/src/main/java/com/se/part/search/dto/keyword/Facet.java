package com.se.part.search.dto.keyword;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "plName", "features" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Facet
{
	@JsonProperty("Categories")
	private List<FacetCategory> mainCategories;

	@JsonProperty("SubCategories")
	private List<FacetCategory> subCategories;

	public List<FacetCategory> getMainCategories()
	{
		return mainCategories;
	}

	public void setMainCategories(List<FacetCategory> mainCategories)
	{
		this.mainCategories = mainCategories;
	}

	public List<FacetCategory> getSubCategories()
	{
		return subCategories;
	}

	public void setSubCategories(List<FacetCategory> subCategories)
	{
		this.subCategories = subCategories;
	}

}
