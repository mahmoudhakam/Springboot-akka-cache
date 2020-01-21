package com.se.part.search.dto.keyword;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.part.search.dto.keyword.parametric.FeatureDTO;

@JsonPropertyOrder({ "plName", "features" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PlFeatures
{
	@JsonProperty("plName")
	private String plName;

	@JsonProperty("Features")
	private List<FeatureDTO> features;

	@JsonProperty("Facets")
	private Facet facet;

	public String getPlName()
	{
		return plName;
	}

	public void setPlName(String plName)
	{
		this.plName = plName;
	}

	public List<FeatureDTO> getFeatures()
	{
		return features;
	}

	public void setFeatures(List<FeatureDTO> features)
	{
		this.features = features;
	}

	public Facet getFacet()
	{
		return facet;
	}

	public void setFacet(Facet facet)
	{
		this.facet = facet;
	}
}
