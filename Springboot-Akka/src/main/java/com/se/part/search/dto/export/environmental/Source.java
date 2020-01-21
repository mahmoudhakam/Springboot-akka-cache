
package com.se.part.search.dto.export.environmental;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "sourceType", "url", "pdfId", "sourceTypeId" })
public class Source
{

	@JsonProperty("sourceType")
	private String sourceType;
	@JsonProperty("url")
	private String url;
	@JsonProperty("pdfId")
	private Long pdfId;
	@JsonProperty("sourceTypeId")
	private Long sourceTypeId;

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

	@JsonProperty("url")
	public String getUrl()
	{
		return url;
	}

	@JsonProperty("url")
	public void setUrl(String url)
	{
		this.url = url;
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

}
