package com.se.part.search.dto.export.qualification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "ppap", "qualifiedNo", "qualified", "automotive" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Aec
{

	@JsonProperty("ppap")
	private String ppap;
	@JsonProperty("qualifiedNo")
	private String qualifiedNo;
	@JsonProperty("qualified")
	private String qualified;
	@JsonProperty("automotive")
	private String automotive;

	@JsonProperty("ppap")
	public String getPpap()
	{
		return ppap;
	}

	@JsonProperty("ppap")
	public void setPpap(String ppap)
	{
		this.ppap = ppap;
	}

	@JsonProperty("qualifiedNo")
	public String getQualifiedNo()
	{
		return qualifiedNo;
	}

	@JsonProperty("qualifiedNo")
	public void setQualifiedNo(String qualifiedNo)
	{
		this.qualifiedNo = qualifiedNo;
	}

	@JsonProperty("qualified")
	public String getQualified()
	{
		return qualified;
	}

	@JsonProperty("qualified")
	public void setQualified(String qualified)
	{
		this.qualified = qualified;
	}

	@JsonProperty("automotive")
	public String getAutomotive()
	{
		return automotive;
	}

	@JsonProperty("automotive")
	public void setAutomotive(String automotive)
	{
		this.automotive = automotive;
	}

}
