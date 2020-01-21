package com.se.part.search.dto.keyword;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "plFeatures" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultObject
{
	@JsonProperty("PlFeatures")
	private PlFeatures plFeatures;

	@JsonProperty("TotalItems")
	private Long totalItems;

	@JsonProperty("PartsList")
	private List<PartDTO> partsList;

	public PlFeatures getPlFeatures()
	{
		return plFeatures;
	}

	public void setPlFeatures(PlFeatures plFeatures)
	{
		this.plFeatures = plFeatures;
	}

	public Long getTotalItems()
	{
		return totalItems;
	}

	public void setTotalItems(Long totalItems)
	{
		this.totalItems = totalItems;
	}

	public List<PartDTO> getPartsList()
	{
		return partsList;
	}

	public void setPartsList(List<PartDTO> partsList)
	{
		this.partsList = partsList;
	}
}
