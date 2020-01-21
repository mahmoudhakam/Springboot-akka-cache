package com.se.part.search.dto.keyword;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.part.search.dto.keyword.parametric.FeatureDTO;

@JsonPropertyOrder({ "status", "resultObj" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PartDTO
{
	@JsonProperty("Features")
	private List<FeatureDTO> features;

	public List<FeatureDTO> getFeatures()
	{
		return features;
	}

	public void setFeatures(List<FeatureDTO> features)
	{
		this.features = features;
	}
}
