package com.se.part.search.messages;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "code", "success", "message" })
public class PartSearchStatus
{
	private int code;
	private String message;
	private Boolean success;

	public PartSearchStatus(PartSearchOperationMessages message, Boolean success)
	{
		super();
		this.message = message.getMsg();
		this.code = message.getCode();
		this.success = success;
	}

	public PartSearchStatus(String message, Boolean success)
	{
		super();
		this.message = message;
		this.success = success;
	}

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Boolean isSuccess()
	{
		return success;
	}

	public void setSuccess(Boolean success)
	{
		this.success = success;
	}

}
