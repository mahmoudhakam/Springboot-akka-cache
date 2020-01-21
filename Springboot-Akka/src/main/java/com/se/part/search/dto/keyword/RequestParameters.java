package com.se.part.search.dto.keyword;

public class RequestParameters
{
	private String plName;
	private String plId;
	private int level = 3; // Default is 3 (Product line)
	private String filters;
	private String keyword;
	private int pageSize = 25; // Default is 50 part per page
	private String order;
	private int pageNumber = 1; // Default is 1 so that will be 1-1 = 0 the solr page zero is the first page
	private String last_select;
	private String keywordOperator = " AND ";
	private String partDetailsComId;
	private String autocompleteSection;
	boolean debug;
	boolean facetsEnabled;
	boolean boostResults;
	boolean exact;
	boolean bomFacetsRequest;
	private String rerankComIDs;
	private String excludedComIDs;
	private String bomData;

	public String getLast_select()
	{
		return last_select;
	}

	public void setLast_select(String last_select)
	{
		this.last_select = last_select;
	}

	public String getPlName()
	{
		return plName;
	}

	public void setPlName(String plName)
	{
		this.plName = plName;
	}

	public String getPlId()
	{
		return plId;
	}

	public void setPlId(String plId)
	{
		this.plId = plId;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public String getFilters()
	{
		return filters;
	}

	public void setFilters(String filters)
	{
		this.filters = filters;
	}

	public String getKeyword()
	{
		return keyword;
	}

	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public String getOrder()
	{
		return order;
	}

	public void setOrder(String order)
	{
		this.order = order;
	}

	public int getPageNumber()
	{
		return pageNumber;
	}

	public void setPageNumber(int pageNumber)
	{
		this.pageNumber = pageNumber;
	}

	public String getKeywordOperator()
	{
		return keywordOperator;
	}

	public void setKeywordOperator(String keywordOperator)
	{
		this.keywordOperator = keywordOperator;
	}

	public String getPartDetailsComId()
	{
		return partDetailsComId;
	}

	public void setPartDetailsComId(String partDetailsComId)
	{
		this.partDetailsComId = partDetailsComId;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public boolean isFacetsEnabled()
	{
		return facetsEnabled;
	}

	public void setFacetsEnabled(boolean facetsEnabled)
	{
		this.facetsEnabled = facetsEnabled;
	}

	public boolean isBoostResults()
	{
		return boostResults;
	}

	public void setBoostResults(boolean boostResults)
	{
		this.boostResults = boostResults;
	}

	public String getRerankComIDs()
	{
		return rerankComIDs;
	}

	public void setRerankComIDs(String rerankComIDs)
	{
		this.rerankComIDs = rerankComIDs;
	}

	public String getExcludedComIDs()
	{
		return excludedComIDs;
	}

	public void setExcludedComIDs(String excludedComIDs)
	{
		this.excludedComIDs = excludedComIDs;
	}

	public String getAutocompleteSection()
	{
		return autocompleteSection;
	}

	public void setAutocompleteSection(String autocompleteSection)
	{
		this.autocompleteSection = autocompleteSection;
	}

	public String getBomData()
	{
		return bomData;
	}

	public void setBomData(String bomData)
	{
		this.bomData = bomData;
	}

	public boolean isExact()
	{
		return exact;
	}

	public void setExact(boolean exact)
	{
		this.exact = exact;
	}

	public boolean isBomFacetsRequest()
	{
		return bomFacetsRequest;
	}

	public void setBomFacetsRequest(boolean bomFacetsRequest)
	{
		this.bomFacetsRequest = bomFacetsRequest;
	}
}
