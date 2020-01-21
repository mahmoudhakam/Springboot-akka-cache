
package com.se.part.search.dto.export.environmental;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "rohsStatus", "sources", "rohsVersionStatus", "rohsVersionName" })
public class _200295EC
{

	@JsonProperty("rohsStatus")
	private String rohsStatus;
	@JsonProperty("sources")
	private List<Source> sources = null;
	@JsonProperty("rohsVersionStatus")
	private String rohsVersionStatus;
	@JsonProperty("rohsVersionName")
	private String rohsVersionName;

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

}
