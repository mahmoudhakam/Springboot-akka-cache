package com.se.part.search.dto.keyword;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.part.search.dto.bom.BOMRow;
import com.se.part.search.dto.bom.BOMStatistics;
import com.se.part.search.dto.keyword.parametric.SearchStep;
import com.se.part.search.dto.partSearch.PartSearchResult;

@JsonPropertyOrder({ "status", "totalItems", "serviceTime", "steps", "resultObj", "keywordResults", "resultType", "keywordOperator", "keywordFacetsWrapper" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RestResponseWrapper
{
	@JsonProperty("Status")
	private Status status;

	@JsonProperty("ServiceTime")
	private String serviceTime;

	@JsonProperty("resultObj")
	private ResultObject resultObj;
	@JsonProperty("PartResult")
	private List<PartSearchResult> keywordResults;
	@JsonProperty("BOMResult")
	private List<BOMRow> bomResult;
	@JsonProperty("BOMStatistics")
	private BOMStatistics bomStatistics;
	// @JsonProperty("Result")
	// private List<KeywordResult> keywordResults;
	@JsonProperty("Search Steps")
	private List<SearchStep> steps;
	@JsonProperty("Facets")
	private KeywordFacetsWrapper keywordFacetsWrapper;
	@JsonProperty("FacetsMap")
	private Map<String, Map<String, List<String>>> facetMap;

	@JsonProperty("ResultType")
	private String resultType;

	@JsonProperty("payload")
	private Payload payload;

	@JsonProperty("TotalItems")
	private Long totalItems;

	@JsonProperty("KeywordOperator")
	private String keywordOperator;
	@JsonProperty("AutoCompleteResult")
	private AutoCompleteResult autoCompleteResult;

	public RestResponseWrapper()
	{
	}

	public RestResponseWrapper(Status status)
	{
		this.status = status;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public ResultObject getResultObj()
	{
		return resultObj;
	}

	public void setResultObj(ResultObject resultObj)
	{
		this.resultObj = resultObj;
	}

	public String getServiceTime()
	{
		return serviceTime;
	}

	public void setServiceTime(String serviceTime)
	{
		this.serviceTime = serviceTime;
	}

	public Payload getPayload()
	{
		return payload;
	}

	public void setPayload(Payload payload)
	{
		this.payload = payload;
	}

	public List<PartSearchResult> getKeywordResults()
	{
		return keywordResults;
	}

	public void setKeywordResults(List<PartSearchResult> keywordResults)
	{
		this.keywordResults = keywordResults;
	}

	public KeywordFacetsWrapper getKeywordFacetsWrapper()
	{
		return keywordFacetsWrapper;
	}

	public void setKeywordFacetsWrapper(KeywordFacetsWrapper keywordFacetsWrapper)
	{
		this.keywordFacetsWrapper = keywordFacetsWrapper;
	}

	public String getResultType()
	{
		return resultType;
	}

	public void setResultType(String resultType)
	{
		this.resultType = resultType;
	}

	public Long getTotalItems()
	{
		return totalItems;
	}

	public void setTotalItems(Long totalItems)
	{
		this.totalItems = totalItems;
	}

	public String getKeywordOperator()
	{
		return keywordOperator;
	}

	public void setKeywordOperator(String keywordOperator)
	{
		this.keywordOperator = keywordOperator;
	}

	public List<SearchStep> getSteps()
	{
		return steps;
	}

	public void setSteps(List<SearchStep> steps)
	{
		this.steps = steps;
	}

	public AutoCompleteResult getAutoCompleteResult()
	{
		return autoCompleteResult;
	}

	public void setAutoCompleteResult(AutoCompleteResult autoCompleteResult)
	{
		this.autoCompleteResult = autoCompleteResult;
	}

	public List<BOMRow> getBomResult()
	{
		return bomResult;
	}

	public void setBomResult(List<BOMRow> bomResult)
	{
		this.bomResult = bomResult;
	}

	public Map<String, Map<String, List<String>>> getFacetMap()
	{
		return facetMap;
	}

	public void setFacetMap(Map<String, Map<String, List<String>>> facetMap)
	{
		this.facetMap = facetMap;
	}

	public BOMStatistics getBomStatistics()
	{
		return bomStatistics;
	}

	public void setBomStatistics(BOMStatistics bomStatistics)
	{
		this.bomStatistics = bomStatistics;
	}
}
