package com.se.onprem.dto.ws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "name", "count" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FacetCategory
{
	@JsonProperty("name")
	private String name;

	@JsonProperty("count")
	private long count;

}
