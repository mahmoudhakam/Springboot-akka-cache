package com.se.part.search.dto.partSearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "comID", "partNumber", "nanPartNumber", "manufacturer", "manufacturerId", "plName", "plId", "description", "lifecycle", "rohs", "rohsVersion", "smallImage" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartSearchDTO
{
	@JsonProperty("ComID")
	private String comID;

	@JsonProperty("PartNumber")
	private String partNumber;

	@JsonProperty("NanPartNumber")
	private String nanPartNumber;

	@JsonProperty("Manufacturer")
	private String manufacturer;

	@JsonProperty("ManufacturerId")
	private String manufacturerId;

	@JsonProperty("PlName")
	private String plName;

	@JsonProperty("PlID")
	private String plId;

	@JsonProperty("Description")
	private String description;

	@JsonProperty("LifeCycle")
	private String lifecycle;

	@JsonProperty("ROHS")
	private String rohs;

	@JsonProperty("RoHSVersion")
	private String rohsVersion;

	@JsonProperty("SmallImage")
	private String smallImage;

	public String getComID()
	{
		return comID;
	}

	public void setComID(String comID)
	{
		this.comID = comID;
	}

	public String getPartNumber()
	{
		return partNumber;
	}

	public void setPartNumber(String partNumber)
	{
		this.partNumber = partNumber;
	}

	public String getManufacturer()
	{
		return manufacturer;
	}

	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public String getManufacturerId()
	{
		return manufacturerId;
	}

	public void setManufacturerId(String manufacturerId)
	{
		this.manufacturerId = manufacturerId;
	}

	public String getPlName()
	{
		return plName;
	}

	public void setPlName(String plName)
	{
		this.plName = plName;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getLifecycle()
	{
		return lifecycle;
	}

	public void setLifecycle(String lifecycle)
	{
		this.lifecycle = lifecycle;
	}

	public String getRohs()
	{
		return rohs;
	}

	public void setRohs(String rohs)
	{
		this.rohs = rohs;
	}

	public String getRohsVersion()
	{
		return rohsVersion;
	}

	public void setRohsVersion(String rohsVersion)
	{
		this.rohsVersion = rohsVersion;
	}

	public String getSmallImage()
	{
		return smallImage;
	}

	public void setSmallImage(String smallImage)
	{
		this.smallImage = smallImage;
	}

	public String getNanPartNumber()
	{
		return nanPartNumber;
	}

	public void setNanPartNumber(String nanPartNumber)
	{
		this.nanPartNumber = nanPartNumber;
	}

	public String getPlId()
	{
		return plId;
	}

	public void setPlId(String plId)
	{
		this.plId = plId;
	}
}
