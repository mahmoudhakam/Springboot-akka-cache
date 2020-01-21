package com.se.part.search.dto.partDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "rowComDesc", "comPartNumber", "manName", "pdfURL", "onlineSupplierDS", "plName", "taxPath", "eCCN", "hTSUSA", "scheduleB", "uNSPSC" })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryDataDTO
{

	@JsonIgnore
	private String comID;

	@JsonProperty("RowComDescription")
	private String rowComDesc;
	@JsonProperty("ComPartNumber")
	private String comPartNumber;
	@JsonProperty("ECCN")
	private String eCCN;
	@JsonProperty("HTSUSA")
	private String hTSUSA;
	@JsonProperty("ManufacturerName")
	private String manName;
	@JsonProperty("PDF_URL")
	private String pdfURL;
	@JsonProperty("PL_NAME")
	private String plName;
	@JsonProperty("ScheduleB")
	private String scheduleB;
	@JsonProperty("TaxonomyPath")
	private String taxPath;
	@JsonProperty("UNSPSC")
	private String uNSPSC;
	@JsonProperty("OnlineSupplierDatasheet")
	private String onlineSupplierDS;

	public SummaryDataDTO()
	{
	}

	public String getComID()
	{
		return comID;
	}

	public void setComID(String comID)
	{
		this.comID = comID;
	}

	public String getRowComDesc()
	{
		return rowComDesc;
	}

	public void setRowComDesc(String rowComDesc)
	{
		this.rowComDesc = rowComDesc;
	}

	public String getComPartNumber()
	{
		return comPartNumber;
	}

	public void setComPartNumber(String comPartNumber)
	{
		this.comPartNumber = comPartNumber;
	}

	public String geteCCN()
	{
		return eCCN;
	}

	public void seteCCN(String eCCN)
	{
		this.eCCN = eCCN;
	}

	public String gethTSUSA()
	{
		return hTSUSA;
	}

	public void sethTSUSA(String hTSUSA)
	{
		this.hTSUSA = hTSUSA;
	}

	public String getManName()
	{
		return manName;
	}

	public void setManName(String manName)
	{
		this.manName = manName;
	}

	public String getPdfURL()
	{
		return pdfURL;
	}

	public void setPdfURL(String pdfURL)
	{
		this.pdfURL = pdfURL;
	}

	public String getPlName()
	{
		return plName;
	}

	public void setPlName(String plName)
	{
		this.plName = plName;
	}

	public String getScheduleB()
	{
		return scheduleB;
	}

	public void setScheduleB(String scheduleB)
	{
		this.scheduleB = scheduleB;
	}

	public String getTaxPath()
	{
		return taxPath;
	}

	public void setTaxPath(String taxPath)
	{
		this.taxPath = taxPath;
	}

	public String getuNSPSC()
	{
		return uNSPSC;
	}

	public void setuNSPSC(String uNSPSC)
	{
		this.uNSPSC = uNSPSC;
	}

	public String getOnlineSupplierDS()
	{
		return onlineSupplierDS;
	}

	public void setOnlineSupplierDS(String onlineSupplierDS)
	{
		this.onlineSupplierDS = onlineSupplierDS;
	}

}
