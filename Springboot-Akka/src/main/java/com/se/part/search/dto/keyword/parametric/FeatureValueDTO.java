package com.se.part.search.dto.keyword.parametric;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "value", "valueCount" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FeatureValueDTO implements Serializable
{
	@JsonProperty("value")
	private String value;

	@JsonProperty("count")
	private Long valueCount;

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public Long getValueCount()
	{
		return valueCount;
	}

	public void setValueCount(Long valueCount)
	{
		this.valueCount = valueCount;
	}

}
