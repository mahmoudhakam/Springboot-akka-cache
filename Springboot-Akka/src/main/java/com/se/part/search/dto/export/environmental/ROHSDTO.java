
package com.se.part.search.dto.export.environmental;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "elv", "green", "rohsVersionName", "rohsVersionStatus", "rohsStatus", "rohsExemption", "rohsData", "exemptionData", "testReportDTOs", "lfStatusId", "isContainTestReport" })
public class ROHSDTO
{

	@JsonProperty("elv")
	private String elv;
	@JsonProperty("green")
	private String green;
	@JsonProperty("rohsVersionName")
	private String rohsVersionName;
	@JsonProperty("rohsVersionStatus")
	private String rohsVersionStatus;
	@JsonProperty("rohsStatus")
	private String rohsStatus;
	@JsonProperty("rohsExemption")
	private RohsExemption rohsExemption;
	@JsonProperty("rohsData")
	private RohsData rohsData;
	@JsonProperty("exemptionData")
	private ExemptionData exemptionData;
	@JsonProperty("testReportDTOs")
	private List<Object> testReportDTOs = null;
	@JsonProperty("lfStatusId")
	private Integer lfStatusId;
	@JsonProperty("isContainTestReport")
	private Boolean isContainTestReport;

	@JsonProperty("elv")
	public String getElv()
	{
		return elv;
	}

	@JsonProperty("elv")
	public void setElv(String elv)
	{
		this.elv = elv;
	}

	@JsonProperty("green")
	public String getGreen()
	{
		return green;
	}

	@JsonProperty("green")
	public void setGreen(String green)
	{
		this.green = green;
	}

	@JsonProperty("rohsVersionName")
	public String getRohsVersionName()
	{
		return rohsVersionName;
	}

	@JsonProperty("rohsVersionName")
	public void setRohsVersionName(String rohsVersionName)
	{
		this.rohsVersionName = rohsVersionName;
	}

	@JsonProperty("rohsVersionStatus")
	public String getRohsVersionStatus()
	{
		return rohsVersionStatus;
	}

	@JsonProperty("rohsVersionStatus")
	public void setRohsVersionStatus(String rohsVersionStatus)
	{
		this.rohsVersionStatus = rohsVersionStatus;
	}

	@JsonProperty("rohsStatus")
	public String getRohsStatus()
	{
		return rohsStatus;
	}

	@JsonProperty("rohsStatus")
	public void setRohsStatus(String rohsStatus)
	{
		this.rohsStatus = rohsStatus;
	}

	@JsonProperty("rohsExemption")
	public RohsExemption getRohsExemption()
	{
		return rohsExemption;
	}

	@JsonProperty("rohsExemption")
	public void setRohsExemption(RohsExemption rohsExemption)
	{
		this.rohsExemption = rohsExemption;
	}

	@JsonProperty("rohsData")
	public RohsData getRohsData()
	{
		return rohsData;
	}

	@JsonProperty("rohsData")
	public void setRohsData(RohsData rohsData)
	{
		this.rohsData = rohsData;
	}

	@JsonProperty("exemptionData")
	public ExemptionData getExemptionData()
	{
		return exemptionData;
	}

	@JsonProperty("exemptionData")
	public void setExemptionData(ExemptionData exemptionData)
	{
		this.exemptionData = exemptionData;
	}

	@JsonProperty("testReportDTOs")
	public List<Object> getTestReportDTOs()
	{
		return testReportDTOs;
	}

	@JsonProperty("testReportDTOs")
	public void setTestReportDTOs(List<Object> testReportDTOs)
	{
		this.testReportDTOs = testReportDTOs;
	}

	@JsonProperty("lfStatusId")
	public Integer getLfStatusId()
	{
		return lfStatusId;
	}

	@JsonProperty("lfStatusId")
	public void setLfStatusId(Integer lfStatusId)
	{
		this.lfStatusId = lfStatusId;
	}

	@JsonProperty("isContainTestReport")
	public Boolean getIsContainTestReport()
	{
		return isContainTestReport;
	}

	@JsonProperty("isContainTestReport")
	public void setIsContainTestReport(Boolean isContainTestReport)
	{
		this.isContainTestReport = isContainTestReport;
	}

}
