package com.se.part.search.dto.export.price;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Max Lead Time", "Min Lead Time", "Min Price", "AVG Price", "Last Updated Date" })
public class PriceDTO
{
	@JsonProperty("Max Lead Time")
	private String maxLeadTime;
	@JsonProperty("Min Lead Time")
	private String minLeadTime;
	@JsonProperty("Min Price")
	private String minPrice;
	@JsonProperty("AVG Price")
	private String aVGPrice;
	@JsonProperty("Last Updated Date")
	private String lastUpdatedDate;

	@JsonProperty("Max Lead Time")
	public String getMaxLeadTime()
	{
		return maxLeadTime;
	}

	@JsonProperty("Max Lead Time")
	public void setMaxLeadTime(String maxLeadTime)
	{
		this.maxLeadTime = maxLeadTime;
	}

	@JsonProperty("Min Lead Time")
	public String getMinLeadTime()
	{
		return minLeadTime;
	}

	@JsonProperty("Min Lead Time")
	public void setMinLeadTime(String minLeadTime)
	{
		this.minLeadTime = minLeadTime;
	}

	@JsonProperty("Min Price")
	public String getMinPrice()
	{
		return minPrice;
	}

	@JsonProperty("Min Price")
	public void setMinPrice(String minPrice)
	{
		this.minPrice = minPrice;
	}

	@JsonProperty("AVG Price")
	public String getAVGPrice()
	{
		return aVGPrice;
	}

	@JsonProperty("AVG Price")
	public void setAVGPrice(String aVGPrice)
	{
		this.aVGPrice = aVGPrice;
	}

	@JsonProperty("Last Updated Date")
	public String getLastUpdatedDate()
	{
		return lastUpdatedDate;
	}

	@JsonProperty("Last Updated Date")
	public void setLastUpdatedDate(String lastUpdatedDate)
	{
		this.lastUpdatedDate = lastUpdatedDate;
	}

}
