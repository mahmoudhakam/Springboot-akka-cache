
package com.se.part.search.dto.export.risk;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Name", "Value" })
public class LifeCycleForeCast implements Serializable
{

	@JsonProperty("Name")
	private String name;
	@JsonProperty("Value")
	private String value;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private final static long serialVersionUID = -8207388315290578870L;

	@JsonProperty("Name")
	public String getName()
	{
		return name;
	}

	@JsonProperty("Name")
	public void setName(String name)
	{
		this.name = name;
	}

	public LifeCycleForeCast withName(String name)
	{
		this.name = name;
		return this;
	}

	@JsonProperty("Value")
	public String getValue()
	{
		return value;
	}

	@JsonProperty("Value")
	public void setValue(String value)
	{
		this.value = value;
	}

	public LifeCycleForeCast withValue(String value)
	{
		this.value = value;
		return this;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties()
	{
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value)
	{
		this.additionalProperties.put(name, value);
	}

	public LifeCycleForeCast withAdditionalProperty(String name, Object value)
	{
		this.additionalProperties.put(name, value);
		return this;
	}

}
