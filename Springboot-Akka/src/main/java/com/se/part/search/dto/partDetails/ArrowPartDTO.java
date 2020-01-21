package com.se.part.search.dto.partDetails;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ArrowPartDTO
{

	@JsonProperty("Features")
	private List<ArrowFeatureDTO> features;

	public List<ArrowFeatureDTO> getFeatures()
	{
		return features;
	}

	public void setFeatures(List<ArrowFeatureDTO> features)
	{
		this.features = features;
	}
}
