
package com.se.part.search.dto.export.coos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "country", "source" })
public class CountryOfOriginList
{

	@JsonProperty("country")
	private String country;
	@JsonProperty("source")
	private String source;

	@JsonProperty("country")
	public String getCountry()
	{
		return country;
	}

	@JsonProperty("country")
	public void setCountry(String country)
	{
		this.country = country;
	}

	@JsonProperty("source")
	public String getSource()
	{
		return source;
	}

	@JsonProperty("source")
	public void setSource(String source)
	{
		this.source = source;
	}

}
