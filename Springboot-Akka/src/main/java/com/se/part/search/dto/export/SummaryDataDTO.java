package com.se.part.search.dto.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "comId", "partNumber", "manufacturer", "taxonomyPath", "plName", "description", "manId", "pdfid", "pdfURL", "isPassive" })
public class SummaryDataDTO
{
	@JsonProperty("comId")
	private Integer comId;
	@JsonProperty("partNumber")
	private String partNumber;
	@JsonProperty("manufacturer")
	private String manufacturer;
	@JsonProperty("taxonomyPath")
	private String taxonomyPath;
	@JsonProperty("plName")
	private String plName;
	@JsonProperty("description")
	private String description;
	@JsonProperty("manId")
	private Integer manId;
	@JsonProperty("pdfid")
	private Integer pdfid;
	@JsonProperty("pdfURL")
	private String pdfURL;
	@JsonProperty("isPassive")
	private Boolean isPassive;

	@JsonProperty("comId")
	public Integer getComId()
	{
		return comId;
	}

	@JsonProperty("comId")
	public void setComId(Integer comId)
	{
		this.comId = comId;
	}

	@JsonProperty("partNumber")
	public String getPartNumber()
	{
		return partNumber;
	}

	@JsonProperty("partNumber")
	public void setPartNumber(String partNumber)
	{
		this.partNumber = partNumber;
	}

	@JsonProperty("manufacturer")
	public String getManufacturer()
	{
		return manufacturer;
	}

	@JsonProperty("manufacturer")
	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	@JsonProperty("taxonomyPath")
	public String getTaxonomyPath()
	{
		return taxonomyPath;
	}

	@JsonProperty("taxonomyPath")
	public void setTaxonomyPath(String taxonomyPath)
	{
		this.taxonomyPath = taxonomyPath;
	}

	@JsonProperty("plName")
	public String getPlName()
	{
		return plName;
	}

	@JsonProperty("plName")
	public void setPlName(String plName)
	{
		this.plName = plName;
	}

	@JsonProperty("description")
	public String getDescription()
	{
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description)
	{
		this.description = description;
	}

	@JsonProperty("manId")
	public Integer getManId()
	{
		return manId;
	}

	@JsonProperty("manId")
	public void setManId(Integer manId)
	{
		this.manId = manId;
	}

	@JsonProperty("pdfid")
	public Integer getPdfid()
	{
		return pdfid;
	}

	@JsonProperty("pdfid")
	public void setPdfid(Integer pdfid)
	{
		this.pdfid = pdfid;
	}

	@JsonProperty("pdfURL")
	public String getPdfURL()
	{
		return pdfURL;
	}

	@JsonProperty("pdfURL")
	public void setPdfURL(String pdfURL)
	{
		this.pdfURL = pdfURL;
	}

	@JsonProperty("isPassive")
	public Boolean getIsPassive()
	{
		return isPassive;
	}

	@JsonProperty("isPassive")
	public void setIsPassive(Boolean isPassive)
	{
		this.isPassive = isPassive;
	}

}