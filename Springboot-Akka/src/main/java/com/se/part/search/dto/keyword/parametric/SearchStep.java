package com.se.part.search.dto.keyword.parametric;

public class SearchStep
{
	String stepName;
	String query;
	long queryTime;
	long totalTime;

	public SearchStep(String stepName, String query, long queryTime, long totalTime)
	{
		super();
		this.stepName = stepName;
		this.query = query;
		this.queryTime = queryTime;
		this.totalTime = totalTime;
	}

	public String getStepName()
	{
		return stepName;
	}

	public void setStepName(String stepName)
	{
		this.stepName = stepName;
	}

	public String getQuery()
	{
		return query;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}

	public long getQueryTime()
	{
		return queryTime;
	}

	public void setQueryTime(long queryTime)
	{
		this.queryTime = queryTime;
	}

	public long getTotalTime()
	{
		return totalTime;
	}

	public void setTotalTime(long totalTime)
	{
		this.totalTime = totalTime;
	}

}
