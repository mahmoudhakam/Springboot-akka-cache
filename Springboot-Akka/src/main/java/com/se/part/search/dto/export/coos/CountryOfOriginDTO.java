
package com.se.part.search.dto.export.coos;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "countryOfOriginList" })
public class CountryOfOriginDTO
{

	@JsonProperty("countryOfOriginList")
	private List<CountryOfOriginList> countryOfOriginList = null;

	@JsonProperty("countryOfOriginList")
	public List<CountryOfOriginList> getCountryOfOriginList()
	{
		return countryOfOriginList;
	}

	@JsonProperty("countryOfOriginList")
	public void setCountryOfOriginList(List<CountryOfOriginList> countryOfOriginList)
	{
		this.countryOfOriginList = countryOfOriginList;
	}

}
