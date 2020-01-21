package com.se.part.search.dto.export;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManufacturerResponseWrapper
{
	@JsonProperty("Manufacturer")
	private List<NameValueDTO> manufacturerExportResult;

	public List<NameValueDTO> getManufacturerExportResult()
	{
		return manufacturerExportResult;
	}

	public void setManufacturerExportResult(List<NameValueDTO> manufacturerExportResult)
	{
		this.manufacturerExportResult = manufacturerExportResult;
	}

}
