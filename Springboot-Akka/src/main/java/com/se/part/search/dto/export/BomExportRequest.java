package com.se.part.search.dto.export;

import java.util.List;
import java.util.Map;

import com.se.part.search.dto.ParentSearchRequest;

public class BomExportRequest extends ParentSearchRequest
{
	private List<String> comIDs;
	private Map<String, List<String>> categories;
	private int batchSize;

	public BomExportRequest()
	{
	}

	public List<String> getComIDs()
	{
		return comIDs;
	}

	public void setComIDs(List<String> comIDs)
	{
		this.comIDs = comIDs;
	}

	public Map<String, List<String>> getCategories()
	{
		return categories;
	}

	public void setCategories(Map<String, List<String>> categories)
	{
		this.categories = categories;
	}

	public int getBatchSize()
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

}
