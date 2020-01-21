package com.se.part.search.dto.bom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "comID", "partNumber", "nanPartNumber", "manufacturer", "manufacturerId", "plId" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BOMRow
{
	@JsonProperty("ComID")

	private String comID;

	@JsonProperty("PartNumber")
	private String partNumber;

	@JsonProperty("UploadedPartNumber")
	private String uploadedMpn;

	@JsonProperty("NanPartNumber")
	private String nanPartNumber;

	@JsonProperty("Manufacturer")
	private String manufacturer;
	@JsonProperty("UploadedManufacturer")
	private String uploadedManufacturer;

	@JsonProperty("ManufacturerId")
	private String manufacturerId;

	@JsonProperty("Description")
	private String description;

	@JsonProperty("LifeCycle")
	private String lifecycle;

	@JsonProperty("ROHS")
	private String rohs;

	@JsonProperty("RoHSVersion")
	private String rohsVersion;
	@JsonProperty("PlID")
	private String plId;

	@JsonProperty("validationStatusCode")
	private int validationStatusCode;

	@JsonProperty("MatchStatus")
	private String matchStatus;

	private String rowKey;

	@JsonProperty("MatchConfidence")
	private float matchConfidence;

	@JsonProperty("PartID")
	private int rowId;

	@JsonProperty("SimilarCount")
	private int similarCount;

	private long bomId;
	private String manStatus;

	@JsonProperty("LEVEL")
	private int level;

	@JsonProperty("ParentID")
	private String parentId;

	@JsonProperty("Path")
	private String path;

	@JsonProperty("Id")
	private String id;
	@JsonProperty("HasChild")
	private int hasChild;

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

	public String getUploadedMpn()
	{
		return uploadedMpn;
	}

	public void setUploadedMpn(String uploadedMpn)
	{
		this.uploadedMpn = uploadedMpn;
	}

	public String getNanPartNumber()
	{
		return nanPartNumber;
	}

	public void setNanPartNumber(String nanPartNumber)
	{
		this.nanPartNumber = nanPartNumber;
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

	public String getPlId()
	{
		return plId;
	}

	public void setPlId(String plId)
	{
		this.plId = plId;
	}

	public String getMatchStatus()
	{
		return matchStatus;
	}

	public void setMatchStatus(String matchStatus)
	{
		this.matchStatus = matchStatus;
	}

	public int getRowId()
	{
		return rowId;
	}

	public void setRowId(int rowId)
	{
		this.rowId = rowId;
	}

	public int getSimilarCount()
	{
		return similarCount;
	}

	public void setSimilarCount(int similarCount)
	{
		this.similarCount = similarCount;
	}

	public String getManStatus()
	{
		return manStatus;
	}

	public void setManStatus(String manStatus)
	{
		this.manStatus = manStatus;
	}

	public int getValidationStatusCode()
	{
		return validationStatusCode;
	}

	public void setValidationStatusCode(int validationStatusCode)
	{
		this.validationStatusCode = validationStatusCode;
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

	public String getUploadedManufacturer()
	{
		return uploadedManufacturer;
	}

	public void setUploadedManufacturer(String uploadedManufacturer)
	{
		this.uploadedManufacturer = uploadedManufacturer;
	}

	public float getMatchConfidence()
	{
		return matchConfidence;
	}

	public void setMatchConfidence(float matchConfidence)
	{
		this.matchConfidence = matchConfidence;
	}

	public String getRowKey()
	{
		return rowKey;
	}

	public void setRowKey(String rowKey)
	{
		this.rowKey = rowKey;
	}

	public long getBomId()
	{
		return bomId;
	}

	public void setBomId(long bomId)
	{
		this.bomId = bomId;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	@Override
	public String toString()
	{
		return "BOMRow [comID=" + comID + ", partNumber=" + partNumber + ", uploadedMpn=" + uploadedMpn + ", nanPartNumber=" + nanPartNumber + ", manufacturer=" + manufacturer + ", uploadedManufacturer=" + uploadedManufacturer + ", manufacturerId="
				+ manufacturerId + ", description=" + description + ", lifecycle=" + lifecycle + ", rohs=" + rohs + ", rohsVersion=" + rohsVersion + ", plId=" + plId + ", validationStatusCode=" + validationStatusCode + ", matchStatus=" + matchStatus
				+ ", rowKey=" + rowKey + ", matchConfidence=" + matchConfidence + ", rowId=" + rowId + ", similarCount=" + similarCount + ", bomId=" + bomId + ", manStatus=" + manStatus + ", level=" + level + ", parentId=" + parentId + ", path="
				+ path + ", id=" + id + "]";
	}

	public int getHasChild()
	{
		return hasChild;
	}

	public void setHasChild(int hasChild)
	{
		this.hasChild = hasChild;
	}

}
