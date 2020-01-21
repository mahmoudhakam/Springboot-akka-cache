package com.se.part.search.dto.export.qualification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "aec" })
@XmlAccessorType(XmlAccessType.FIELD)
public class QualificationsDTO
{

	@JsonProperty("aec")
	private Aec aec;

	@JsonProperty("aec")
	public Aec getAec()
	{
		return aec;
	}

	@JsonProperty("aec")
	public void setAec(Aec aec)
	{
		this.aec = aec;
	}

}