package com.se.part.search.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "supplier", "manID", "status", "eiccMemmberShip", "eiccTemplate", "hasSmelterDetails", "version", "supplortingDocument", "hasDelerationDetails" })
public class ConflictMiniralsDTO
{
	@JsonProperty("supplier")
	private String supplier;
	@JsonProperty("manID")
	private String manID;
	@JsonProperty("status")
	private String status;
	@JsonProperty("eiccMemmberShip")
	private String eiccMemmberShip;
	@JsonProperty("eiccTemplate")
	private String eiccTemplate;
	@JsonProperty("hasSmelterDetails")
	private Boolean hasSmelterDetails;
	@JsonProperty("version")
	private String version;
	@JsonProperty("supplortingDocument")
	private List<Object> supplortingDocument = null;
	@JsonProperty("hasDelerationDetails")
	private Boolean hasDelerationDetails;

	@JsonProperty("supplier")
	public String getSupplier()
	{
		return supplier;
	}

	@JsonProperty("supplier")
	public void setSupplier(String supplier)
	{
		this.supplier = supplier;
	}

	@JsonProperty("manID")
	public String getManID()
	{
		return manID;
	}

	@JsonProperty("manID")
	public void setManID(String manID)
	{
		this.manID = manID;
	}

	@JsonProperty("status")
	public String getStatus()
	{
		return status;
	}

	@JsonProperty("status")
	public void setStatus(String status)
	{
		this.status = status;
	}

	@JsonProperty("eiccMemmberShip")
	public String getEiccMemmberShip()
	{
		return eiccMemmberShip;
	}

	@JsonProperty("eiccMemmberShip")
	public void setEiccMemmberShip(String eiccMemmberShip)
	{
		this.eiccMemmberShip = eiccMemmberShip;
	}

	@JsonProperty("eiccTemplate")
	public String getEiccTemplate()
	{
		return eiccTemplate;
	}

	@JsonProperty("eiccTemplate")
	public void setEiccTemplate(String eiccTemplate)
	{
		this.eiccTemplate = eiccTemplate;
	}

	@JsonProperty("hasSmelterDetails")
	public Boolean getHasSmelterDetails()
	{
		return hasSmelterDetails;
	}

	@JsonProperty("hasSmelterDetails")
	public void setHasSmelterDetails(Boolean hasSmelterDetails)
	{
		this.hasSmelterDetails = hasSmelterDetails;
	}

	@JsonProperty("version")
	public String getVersion()
	{
		return version;
	}

	@JsonProperty("version")
	public void setVersion(String version)
	{
		this.version = version;
	}

	@JsonProperty("supplortingDocument")
	public List<Object> getSupplortingDocument()
	{
		return supplortingDocument;
	}

	@JsonProperty("supplortingDocument")
	public void setSupplortingDocument(List<Object> supplortingDocument)
	{
		this.supplortingDocument = supplortingDocument;
	}

	@JsonProperty("hasDelerationDetails")
	public Boolean getHasDelerationDetails()
	{
		return hasDelerationDetails;
	}

	@JsonProperty("hasDelerationDetails")
	public void setHasDelerationDetails(Boolean hasDelerationDetails)
	{
		this.hasDelerationDetails = hasDelerationDetails;
	}

}
