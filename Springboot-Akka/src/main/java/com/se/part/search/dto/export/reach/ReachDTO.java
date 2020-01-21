
package com.se.part.search.dto.export.reach;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "comId", "reachId", "containSvhc", "exceedLimit", "svhcListVersion", "reachStatus", "sources", "substances", "isContainTestReport", "flagId" })
@XmlAccessorType(XmlAccessType.FIELD)
public class ReachDTO
{

	@JsonProperty("comId")
	private Long comId;
	@JsonProperty("reachId")
	private Long reachId;
	@JsonProperty("containSvhc")
	private String containSvhc;
	@JsonProperty("exceedLimit")
	private String exceedLimit;
	@JsonProperty("svhcListVersion")
	private String svhcListVersion;
	@JsonProperty("reachStatus")
	private String reachStatus;
	@JsonProperty("sources")
	private List<Source> sources = null;
	@JsonProperty("substances")
	private List<Substance> substances = null;
	@JsonProperty("isContainTestReport")
	private Boolean isContainTestReport;
	@JsonProperty("flagId")
	private Long flagId;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public ReachDTO()
	{
	}

	@JsonProperty("comId")
	public Long getComId()
	{
		return comId;
	}

	@JsonProperty("comId")
	public void setComId(Long comId)
	{
		this.comId = comId;
	}

	@JsonProperty("reachId")
	public Long getReachId()
	{
		return reachId;
	}

	@JsonProperty("reachId")
	public void setReachId(Long reachId)
	{
		this.reachId = reachId;
	}

	@JsonProperty("containSvhc")
	public String getContainSvhc()
	{
		return containSvhc;
	}

	@JsonProperty("containSvhc")
	public void setContainSvhc(String containSvhc)
	{
		this.containSvhc = containSvhc;
	}

	@JsonProperty("exceedLimit")
	public String getExceedLimit()
	{
		return exceedLimit;
	}

	@JsonProperty("exceedLimit")
	public void setExceedLimit(String exceedLimit)
	{
		this.exceedLimit = exceedLimit;
	}

	@JsonProperty("svhcListVersion")
	public String getSvhcListVersion()
	{
		return svhcListVersion;
	}

	@JsonProperty("svhcListVersion")
	public void setSvhcListVersion(String svhcListVersion)
	{
		this.svhcListVersion = svhcListVersion;
	}

	@JsonProperty("reachStatus")
	public String getReachStatus()
	{
		return reachStatus;
	}

	@JsonProperty("reachStatus")
	public void setReachStatus(String reachStatus)
	{
		this.reachStatus = reachStatus;
	}

	@JsonProperty("sources")
	public List<Source> getSources()
	{
		return sources;
	}

	@JsonProperty("sources")
	public void setSources(List<Source> sources)
	{
		this.sources = sources;
	}

	@JsonProperty("substances")
	public List<Substance> getSubstances()
	{
		return substances;
	}

	@JsonProperty("substances")
	public void setSubstances(List<Substance> substances)
	{
		this.substances = substances;
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

	@JsonProperty("flagId")
	public Long getFlagId()
	{
		return flagId;
	}

	@JsonProperty("flagId")
	public void setFlagId(Long flagId)
	{
		this.flagId = flagId;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties()
	{
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value)
	{
		this.additionalProperties.put(name, value);
	}

}
