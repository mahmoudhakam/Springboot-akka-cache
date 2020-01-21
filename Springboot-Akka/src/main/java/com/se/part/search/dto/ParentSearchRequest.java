package com.se.part.search.dto;

import javax.servlet.http.HttpServletRequest;

public class ParentSearchRequest
{
	private String fullURL;
	private String remoteAddress;
	private String dbServer;
	private HttpServletRequest request;
	private String requestID;
	private String seToken;
	private String pageNumber;
	private String pageSize;
	private String debugMode;

	public String getFullURL()
	{
		return fullURL;
	}

	public void setFullURL(String fullURL)
	{
		this.fullURL = fullURL;
	}

	public String getRemoteAddress()
	{
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress)
	{
		this.remoteAddress = remoteAddress;
	}

	public String getDbServer()
	{
		return dbServer;
	}

	public void setDbServer(String dbServer)
	{
		this.dbServer = dbServer;
	}

	public HttpServletRequest getRequest()
	{
		return request;
	}

	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	public String getRequestID()
	{
		return requestID;
	}

	public void setRequestID(String requestID)
	{
		this.requestID = requestID;
	}

	public String getSeToken()
	{
		return seToken;
	}

	public void setSeToken(String seToken)
	{
		this.seToken = seToken;
	}

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

	public String getDebugMode()
	{
		return debugMode;
	}

	public void setDebugMode(String debugMode)
	{
		this.debugMode = debugMode;
	}
}
