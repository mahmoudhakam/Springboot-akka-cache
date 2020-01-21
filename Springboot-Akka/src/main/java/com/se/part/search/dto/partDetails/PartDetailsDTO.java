package com.se.part.search.dto.partDetails;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "comID", "summaryData", "features" })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartDetailsDTO
{

	@JsonProperty("COM_ID")
	private String comID;

	@JsonProperty("SummaryData")
	private SummaryDataDTO summaryData;

	@JsonProperty("ParametricFeatures")
	private List<ArrowFeatureDTO> features;

	public PartDetailsDTO()
	{
	}

	public List<ArrowFeatureDTO> getFeatures()
	{
		return features;
	}

	public void setFeatures(List<ArrowFeatureDTO> features)
	{
		this.features = features;
	}

	public SummaryDataDTO getSummaryData()
	{
		return summaryData;
	}

	public void setSummaryData(SummaryDataDTO summaryData)
	{
		this.summaryData = summaryData;
	}

	public String getComID()
	{
		return comID;
	}

	public void setComID(String comID)
	{
		this.comID = comID;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comID == null) ? 0 : comID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		PartDetailsDTO other = (PartDetailsDTO) obj;
		if(comID == null)
		{
			if(other.comID != null)
				return false;
		}
		else if(!comID.equals(other.comID))
			return false;
		return true;
	}

}
