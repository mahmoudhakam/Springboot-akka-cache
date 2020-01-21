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

}
