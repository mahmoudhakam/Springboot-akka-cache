package com.se.onprem.dto.ws;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "partResult", "manResult", "descResult", "categoryResult" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutoCompleteResult
{

	@JsonProperty("PartResult")
	private List<String> partResult;

	@JsonProperty("ManufacturerResult")
	private List<String> manResult;

	@JsonProperty("DescriptionResult")
	private List<String> descResult;

	@JsonProperty("CategoryResult")
	private List<String> categoryResult;

	@JsonIgnore
	public boolean isEmpty()
	{
		
		return (manResult == null || manResult.isEmpty()) && (categoryResult == null || categoryResult.isEmpty())
				&& (descResult == null || descResult.isEmpty()) && (partResult == null || partResult.isEmpty());
	}

}
