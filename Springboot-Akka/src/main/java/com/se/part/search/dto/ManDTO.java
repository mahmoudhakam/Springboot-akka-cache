package com.se.part.search.dto;

public class ManDTO
{
	private String manName;
	private String manCode;
	private Long manId;

	public ManDTO()
	{
		// TODO Auto-generated constructor stub
	}

	public ManDTO(String manName, Long manId, String manCode)
	{
		super();
		this.manName = manName;
		this.manId = manId;
		this.manCode = manCode;
	}

	/**
	 * @return the manName
	 */
	public String getManName()
	{
		return manName;
	}

	/**
	 * @param manName
	 *            the manName to set
	 */
	public void setManName(String manName)
	{
		this.manName = manName;
	}

	/**
	 * @return the mnaId
	 */
	public Long getManId()
	{
		return manId;
	}

	/**
	 * @param mnaId
	 *            the mnaId to set
	 */
	public void setManId(Long mnaId)
	{
		this.manId = mnaId;
	}

	/**
	 * @return the manCode
	 */
	public String getManCode()
	{
		return manCode;
	}

	/**
	 * @param manCode
	 *            the manCode to set
	 */
	public void setManCode(String manCode)
	{
		this.manCode = manCode;
	}
}
