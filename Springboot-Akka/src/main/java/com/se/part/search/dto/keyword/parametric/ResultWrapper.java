package com.se.part.search.dto.keyword.parametric;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.part.search.dto.keyword.Status;

@JsonPropertyOrder({ "status", "resultCount", "steps", "facets", "parts", "taxonomyTree", "taxonomyCountsMap" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResultWrapper
{
	private Long resultCount;
	private List<FeatureDTO> facets;
	private List<ParametricSearchResultDTO> parts;
	private Status status;
	private List<PLTypeDTO> taxonomyTree;
	private Map<String, Map<String, Long>> taxonomyCountsMap;
	private List<SearchStep> steps;

	public ResultWrapper(Status status)
	{
		this.status = status;
	}

	public ResultWrapper()
	{
	}

	public Long getResultCount()
	{
		return resultCount;
	}

	public void setResultCount(Long resultCount)
	{
		this.resultCount = resultCount;
	}

	public List<FeatureDTO> getFacets()
	{
		return facets;
	}

	public void setFacets(List<FeatureDTO> facets)
	{
		this.facets = facets;
	}

	public List<ParametricSearchResultDTO> getParts()
	{
		return parts;
	}

	public void setParts(List<ParametricSearchResultDTO> parts)
	{
		this.parts = parts;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public List<PLTypeDTO> getTaxonomyTree()
	{
		return taxonomyTree;
	}

	public void setTaxonomyTree(List<PLTypeDTO> taxonomyTree)
	{
		this.taxonomyTree = taxonomyTree;
	}

	public Map<String, Map<String, Long>> getTaxonomyCountsMap()
	{
		return taxonomyCountsMap;
	}

	public void setTaxonomyCountsMap(Map<String, Map<String, Long>> taxonomyCountsMap)
	{
		this.taxonomyCountsMap = taxonomyCountsMap;
	}

	public List<SearchStep> getSteps()
	{
		return steps;
	}

	public void setSteps(List<SearchStep> steps)
	{
		this.steps = steps;
	}
}
