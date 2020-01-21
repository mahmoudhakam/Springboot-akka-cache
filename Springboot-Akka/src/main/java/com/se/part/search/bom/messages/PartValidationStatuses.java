package com.se.part.search.bom.messages;

public enum PartValidationStatuses
{
	// @formatter:off
    ERROR("Erroe",0),
    VALID("Exact",1),
    INVALID("No Match",2),
    LOOKUP("Autotmatic",3),
    MISSING("Ignored.",4),
    OTHER_MATCHES("Similar Match Check Manufacture.",5),
    SELECT_MANUFACTURER("Similar Match Check Manufacture.",5),
    SIMILAR_FOUND("Similar Matches available.",7),
    BEGINWITH_SIMILAR_FOUND("Similar Matches available.",9),
    SIMILAR_FOUND_Ignoring_Man("Similar Matches available.",8);
	// @formatter:on
	private String message;
	private int code;

	private PartValidationStatuses(String message, int code)
	{
		this.message = message;
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	public int getCode()
	{
		return code;
	}

}
