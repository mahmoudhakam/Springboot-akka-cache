package com.se.part.search.dto.export;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.FIELD)
public class NameValueDTO implements BOMResultParent
{
	@JsonProperty("Name")
	private String name;
	@JsonProperty("Value")
	private String value;

	public NameValueDTO()
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public NameValueDTO(String name, String value)
	{
		super();
		this.name = name;
		this.value = value;
	}

}
