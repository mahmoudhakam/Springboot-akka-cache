
package com.se.part.search.dto.export.reach;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "sourceType", "pdfUrl", "onlineUrl", "pdfId", "sourceTypeId" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Source
{

	@JsonProperty("sourceType")
	private String sourceType;
	@JsonProperty("pdfUrl")
	private String pdfUrl;
	@JsonProperty("onlineUrl")
	private String onlineUrl;
	@JsonProperty("pdfId")
	private Long pdfId;
	@JsonProperty("sourceTypeId")
	private Long sourceTypeId;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public Source()
	{
	}

	@JsonProperty("sourceType")
	public String getSourceType()
	{
		return sourceType;
	}

	@JsonProperty("sourceType")
	public void setSourceType(String sourceType)
	{
		this.sourceType = sourceType;
	}

	@JsonProperty("pdfUrl")
	public String getPdfUrl()
	{
		return pdfUrl;
	}

	@JsonProperty("pdfUrl")
	public void setPdfUrl(String pdfUrl)
	{
		this.pdfUrl = pdfUrl;
	}

	@JsonProperty("onlineUrl")
	public String getOnlineUrl()
	{
		return onlineUrl;
	}

	@JsonProperty("onlineUrl")
	public void setOnlineUrl(String onlineUrl)
	{
		this.onlineUrl = onlineUrl;
	}

	@JsonProperty("pdfId")
	public Long getPdfId()
	{
		return pdfId;
	}

	@JsonProperty("pdfId")
	public void setPdfId(Long pdfId)
	{
		this.pdfId = pdfId;
	}

	@JsonProperty("sourceTypeId")
	public Long getSourceTypeId()
	{
		return sourceTypeId;
	}

	@JsonProperty("sourceTypeId")
	public void setSourceTypeId(Long sourceTypeId)
	{
		this.sourceTypeId = sourceTypeId;
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

}
