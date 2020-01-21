package com.se.part.search.dto.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.part.search.messages.PartSearchStatus;

@JsonPropertyOrder({ "status", "token" })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartSearchAuthenticationResponse
{
	@JsonProperty("Status")
	private PartSearchStatus status;
	@JsonProperty("seToken")
	private String token;

	public PartSearchStatus getStatus()
	{
		return status;
	}

	public void setStatus(PartSearchStatus status)
	{
		this.status = status;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public PartSearchAuthenticationResponse(PartSearchStatus status)
	{
		super();
		this.status = status;
	}

}
