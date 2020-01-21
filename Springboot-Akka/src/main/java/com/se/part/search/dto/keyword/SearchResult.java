package com.se.part.search.dto.keyword;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "totalItems", "partsList" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SearchResult
{
	@JsonProperty("PartsList")
	private List<PartDTO> partsList;

	/*
	 * public long getTotalItems() { return totalItems; }
	 * 
	 * public void setTotalItems(long totalItems) { this.totalItems = totalItems; }
	 */

	public List<PartDTO> getPartsList()
	{
		return partsList;
	}

	public void setPartsList(List<PartDTO> partsList)
	{
		this.partsList = partsList;
	}

}
