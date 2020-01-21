package com.se.part.search.dto.keyword;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "comID", "partNumber", "manufacturer", "manufacturerId", "plName", "description", "lifecycle", "rohs", "rohsVersion", "smallImage", "pdfURL", "mfrHomePage", "inventoryList" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeywordResult
{
	@JsonProperty("ComID")
	private String comID;

	@JsonProperty("PartNumber")
	private String partNumber;

	@JsonProperty("Manufacturer")
	private String manufacturer;

	@JsonProperty("ManufacturerId")
	private String manufacturerId;

	@JsonProperty("PlName")
	private String plName;

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
	@JsonProperty("DatasheetURL")
	private String pdfURL;
	@JsonProperty("MFRHomePageURL")
	private String mfrHomePage;
	@JsonProperty("InventoryData")
	private List<InventoryDTO> inventoryList;

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

	public String getPdfURL()
	{
		return pdfURL;
	}

	public void setPdfURL(String pdfURL)
	{
		this.pdfURL = pdfURL;
	}

	public String getMfrHomePage()
	{
		return mfrHomePage;
	}

	public void setMfrHomePage(String mfrHomePage)
	{
		this.mfrHomePage = mfrHomePage;
	}

	public List<InventoryDTO> getInventoryList()
	{
		return inventoryList;
	}

	public void setInventoryList(List<InventoryDTO> inventoryList)
	{
		this.inventoryList = inventoryList;
	}

	public void addInventoryEntry(InventoryDTO inventoryDTO)
	{
		if(inventoryList == null)
		{
			inventoryList = new ArrayList<>();
		}
		inventoryList.add(inventoryDTO);
	}
}
