package com.se.part.search.dto.alert;

import com.se.part.search.dto.ParentSearchRequest;

public class AlertRequest extends ParentSearchRequest
{

	private String pageNumber;
	private String pageSize;
	private String from;
	private String to;

	public String getPageNumber()
	{
		return pageNumber;
	}

	public void setPageNumber(String pageNumber)
	{
		this.pageNumber = pageNumber;
	}

	public String getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(String pageSize)
	{
		this.pageSize = pageSize;
	}

	public String getFrom()
	{
		return from;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getTo()
	{
		return to;
	}

	public void setTo(String to)
	{
		this.to = to;
	}

}
