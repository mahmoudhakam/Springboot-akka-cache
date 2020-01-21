package com.se.onprem.dto.ws;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "totalItems", "partsList" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SearchResult
{
	@JsonProperty("PartsList")
	private List<PartResult> partsList;

}
