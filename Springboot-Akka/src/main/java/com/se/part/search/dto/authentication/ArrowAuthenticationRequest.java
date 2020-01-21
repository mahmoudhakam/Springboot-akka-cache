package com.se.part.search.dto.authentication;

import com.se.part.search.dto.ParentSearchRequest;

public class ArrowAuthenticationRequest extends ParentSearchRequest
{
	private String apiKey;
	private String userName;

	public String getApiKey()
	{
		return apiKey;
	}

	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

}
