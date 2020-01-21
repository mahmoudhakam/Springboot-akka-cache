
package com.se.part.search.dto.export.risk;

import java.io.Serializable;
import java.util.HashMap;
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
@JsonPropertyOrder({ "lcStatus", "obsolescenceLtbDate", "lcStage", "lcCode", "yearsEOL", "rangeEolMin", "rangeEolMax", "riskScore", "lcRisk", "introductinoDate", "y2eolByIntroductinoDate", "formattedYearsEOL", "formattedRiskGrade", "riskTypeCount" })
@XmlAccessorType(XmlAccessType.FIELD)
public class LifeCycleRisk implements Serializable
{

	@JsonProperty("lcStatus")
	private String lcStatus;
	@JsonProperty("obsolescenceLtbDate")
	private String obsolescenceLtbDate;
	@JsonProperty("lcStage")
	private String lcStage;
	@JsonProperty("lcCode")
	private Long lcCode;
	@JsonProperty("yearsEOL")
	private Long yearsEOL;
	@JsonProperty("rangeEolMin")
	private Long rangeEolMin;
	@JsonProperty("rangeEolMax")
	private Long rangeEolMax;
	@JsonProperty("riskScore")
	private Double riskScore;
	@JsonProperty("lcRisk")
	private String lcRisk;
	@JsonProperty("introductinoDate")
	private String introductinoDate;
	@JsonProperty("y2eolByIntroductinoDate")
	private Long y2eolByIntroductinoDate;
	@JsonProperty("formattedYearsEOL")
	private String formattedYearsEOL;
	@JsonProperty("formattedRiskGrade")
	private String formattedRiskGrade;
	@JsonProperty("riskTypeCount")
	private Double riskTypeCount;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private final static long serialVersionUID = 6848337669646037608L;

	@JsonProperty("lcStatus")
	public String getLcStatus()
	{
		return lcStatus;
	}

	@JsonProperty("lcStatus")
	public void setLcStatus(String lcStatus)
	{
		this.lcStatus = lcStatus;
	}

	public LifeCycleRisk withLcStatus(String lcStatus)
	{
		this.lcStatus = lcStatus;
		return this;
	}

	@JsonProperty("obsolescenceLtbDate")
	public String getObsolescenceLtbDate()
	{
		return obsolescenceLtbDate;
	}

	@JsonProperty("obsolescenceLtbDate")
	public void setObsolescenceLtbDate(String obsolescenceLtbDate)
	{
		this.obsolescenceLtbDate = obsolescenceLtbDate;
	}

	public LifeCycleRisk withObsolescenceLtbDate(String obsolescenceLtbDate)
	{
		this.obsolescenceLtbDate = obsolescenceLtbDate;
		return this;
	}

	@JsonProperty("lcStage")
	public String getLcStage()
	{
		return lcStage;
	}

	@JsonProperty("lcStage")
	public void setLcStage(String lcStage)
	{
		this.lcStage = lcStage;
	}

	public LifeCycleRisk withLcStage(String lcStage)
	{
		this.lcStage = lcStage;
		return this;
	}

	@JsonProperty("lcCode")
	public Long getLcCode()
	{
		return lcCode;
	}

	@JsonProperty("lcCode")
	public void setLcCode(Long lcCode)
	{
		this.lcCode = lcCode;
	}

	public LifeCycleRisk withLcCode(Long lcCode)
	{
		this.lcCode = lcCode;
		return this;
	}

	@JsonProperty("yearsEOL")
	public Long getYearsEOL()
	{
		return yearsEOL;
	}

	@JsonProperty("yearsEOL")
	public void setYearsEOL(Long yearsEOL)
	{
		this.yearsEOL = yearsEOL;
	}

	public LifeCycleRisk withYearsEOL(Long yearsEOL)
	{
		this.yearsEOL = yearsEOL;
		return this;
	}

	@JsonProperty("rangeEolMin")
	public Long getRangeEolMin()
	{
		return rangeEolMin;
	}

	@JsonProperty("rangeEolMin")
	public void setRangeEolMin(Long rangeEolMin)
	{
		this.rangeEolMin = rangeEolMin;
	}

	public LifeCycleRisk withRangeEolMin(Long rangeEolMin)
	{
		this.rangeEolMin = rangeEolMin;
		return this;
	}

	@JsonProperty("rangeEolMax")
	public Long getRangeEolMax()
	{
		return rangeEolMax;
	}

	@JsonProperty("rangeEolMax")
	public void setRangeEolMax(Long rangeEolMax)
	{
		this.rangeEolMax = rangeEolMax;
	}

	public LifeCycleRisk withRangeEolMax(Long rangeEolMax)
	{
		this.rangeEolMax = rangeEolMax;
		return this;
	}

	@JsonProperty("riskScore")
	public Double getRiskScore()
	{
		return riskScore;
	}

	@JsonProperty("riskScore")
	public void setRiskScore(Double riskScore)
	{
		this.riskScore = riskScore;
	}

	public LifeCycleRisk withRiskScore(Double riskScore)
	{
		this.riskScore = riskScore;
		return this;
	}

	@JsonProperty("lcRisk")
	public String getLcRisk()
	{
		return lcRisk;
	}

	@JsonProperty("lcRisk")
	public void setLcRisk(String lcRisk)
	{
		this.lcRisk = lcRisk;
	}

	public LifeCycleRisk withLcRisk(String lcRisk)
	{
		this.lcRisk = lcRisk;
		return this;
	}

	@JsonProperty("introductinoDate")
	public String getIntroductinoDate()
	{
		return introductinoDate;
	}

	@JsonProperty("introductinoDate")
	public void setIntroductinoDate(String introductinoDate)
	{
		this.introductinoDate = introductinoDate;
	}

	public LifeCycleRisk withIntroductinoDate(String introductinoDate)
	{
		this.introductinoDate = introductinoDate;
		return this;
	}

	@JsonProperty("y2eolByIntroductinoDate")
	public Long getY2eolByIntroductinoDate()
	{
		return y2eolByIntroductinoDate;
	}

	@JsonProperty("y2eolByIntroductinoDate")
	public void setY2eolByIntroductinoDate(Long y2eolByIntroductinoDate)
	{
		this.y2eolByIntroductinoDate = y2eolByIntroductinoDate;
	}

	public LifeCycleRisk withY2eolByIntroductinoDate(Long y2eolByIntroductinoDate)
	{
		this.y2eolByIntroductinoDate = y2eolByIntroductinoDate;
		return this;
	}

	@JsonProperty("formattedYearsEOL")
	public String getFormattedYearsEOL()
	{
		return formattedYearsEOL;
	}

	@JsonProperty("formattedYearsEOL")
	public void setFormattedYearsEOL(String formattedYearsEOL)
	{
		this.formattedYearsEOL = formattedYearsEOL;
	}

	public LifeCycleRisk withFormattedYearsEOL(String formattedYearsEOL)
	{
		this.formattedYearsEOL = formattedYearsEOL;
		return this;
	}

	@JsonProperty("formattedRiskGrade")
	public String getFormattedRiskGrade()
	{
		return formattedRiskGrade;
	}

	@JsonProperty("formattedRiskGrade")
	public void setFormattedRiskGrade(String formattedRiskGrade)
	{
		this.formattedRiskGrade = formattedRiskGrade;
	}

	public LifeCycleRisk withFormattedRiskGrade(String formattedRiskGrade)
	{
		this.formattedRiskGrade = formattedRiskGrade;
		return this;
	}

	@JsonProperty("riskTypeCount")
	public Double getRiskTypeCount()
	{
		return riskTypeCount;
	}

	@JsonProperty("riskTypeCount")
	public void setRiskTypeCount(Double riskTypeCount)
	{
		this.riskTypeCount = riskTypeCount;
	}

	public LifeCycleRisk withRiskTypeCount(Double riskTypeCount)
	{
		this.riskTypeCount = riskTypeCount;
		return this;
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

	public LifeCycleRisk withAdditionalProperty(String name, Object value)
	{
		this.additionalProperties.put(name, value);
		return this;
	}

}
