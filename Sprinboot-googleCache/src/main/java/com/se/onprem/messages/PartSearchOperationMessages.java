package com.se.onprem.messages;

import com.se.onprem.util.PartSearchServiceConstants;

public enum PartSearchOperationMessages
{

	//@formatter:off
    
	SUCCESSFULL_OPERATION(200, "Successfull Operation"), 
	FAILED_OPERATION(1, "Failed Operation"),
    NO_RESULT_FOUND(2, "No Result Found"),
    INTERNAL_ERROR(3, "Internal Error"),
    MISSING_COMID(4, "Missing comIDs"),
    INVALID_COMID_FOMART(5, "Invalid ComID format, Only numbers allowed"),
    MISSING_VALUE_IDS(6, "Missing value IDs"),
    INVALID_JSON(7, "Invalid JSON Format"),
    PART_NUMBER_EXCEED(8, "Partnumbers exceed limit "+PartSearchServiceConstants.MAX_PAGE_SIZE),
    COM_ID_EXCEED(8, "ComIds exceed limit "+PartSearchServiceConstants.MAX_PAGE_SIZE),
    PAGESIZE_EXCEED(9, "PageSize exceed limit " + PartSearchServiceConstants.MAX_PAGE_SIZE), 
    WRONG_PAGE_NUMBER_FORMAT(10,"pageNumber parameter is wrong, only numbers allowed"),
    WRONG_PAGE_SIZE_FORMAT(11,"pageSize parameter is wrong, only numbers allowed"),
    WRONG_PAGE_NUMBER(12,"Wrong page number"),
    WRONG_PAGE_SIZE(13,"Wrong page size"), 
    MISSING_USERNAME_OR_APIKEY(14,"Missing userName or apiKey"), 
    EXPIRED_TOKEN(403,"Expired or wrong token, please get new token"), 
    TOKEN_MANDATORY(405,"seToken is mandatory parameter"),
    MISSING_FROM(9, "Missing From date range"),
    INVALID_FROM_FOMART(10, "Invalid From format, Only date allowed formate is MM-dd-yyyy"),   
    MISSING_TO(11, "Missing To date range"),
    INVALID_TO_FOMART(12, "Invalid to format, Only date allowed formate is MM-dd-yyyy"),
    INVALID_DATE_RANGE(13, "Invalid date range, from must be less than to"),
    INVALID_DATE_PARSING(13, "Invalid date parsing, Only date allowed formate is MM-dd-yyyy"),
    WRONG_USENAME_OR_PASSWORD(401,"Wrong username or password");
	
	//@formatter:on

	private Integer code;
	private String msg;

	private PartSearchOperationMessages(int code, String msg)
	{
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode()
	{
		return code;
	}

	public String getMsg()
	{
		return msg;
	}

}
