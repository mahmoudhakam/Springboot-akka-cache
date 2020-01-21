package com.se.part.search.dto.export;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParametricResponseWrapper
{
	@JsonProperty("ParametricFeatures")
	private List<ParametricFeatureDTO> features;

	public List<ParametricFeatureDTO> getFeatures()
	{
		return features;
	}

	public void setFeatures(List<ParametricFeatureDTO> features)
	{
		this.features = features;
	}

}
