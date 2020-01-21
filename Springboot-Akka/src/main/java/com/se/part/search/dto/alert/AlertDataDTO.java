package com.se.part.search.dto.alert;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "comId", "seqId", "plId", "categoryIdDml", "oldVal", "newVal", "dmlFetName", "ddDate", "switchId", "categoryIdDml" })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertDataDTO
{

	@JsonProperty("COM_ID")
	private String comId;
	@JsonProperty("SEQ_ID")
	private Long seqId;
	@JsonProperty("PL_ID")
	private Long plId;
	@JsonProperty("MAN_ID")
	private Long manId;
	@JsonProperty("CATEGORY_ID_DML")
	private Long categoryIdDml;
	@JsonProperty("OLD_VAL")
	private String oldVal;
	@JsonProperty("NEW_VAL")
	private String newVal;
	@JsonProperty("DML_FET_NAME")
	private String dmlFetName;
	@JsonProperty("DD_DATE")
	private String ddDate;
	@JsonProperty("SWITCH_ID")
	private String switchId;

	public String getComId()
	{
		return comId;
	}

	public void setComId(String comId)
	{
		this.comId = comId;
	}

	public Long getSeqId()
	{
		return seqId;
	}

	public void setSeqId(Long seqId)
	{
		this.seqId = seqId;
	}

	public Long getPlId()
	{
		return plId;
	}

	public void setPlId(Long plId)
	{
		this.plId = plId;
	}

	public Long getManId()
	{
		return manId;
	}

	public void setManId(Long manId)
	{
		this.manId = manId;
	}

	public Long getCategoryIdDml()
	{
		return categoryIdDml;
	}

	public void setCategoryIdDml(Long categoryIdDml)
	{
		this.categoryIdDml = categoryIdDml;
	}

	public String getOldVal()
	{
		return oldVal;
	}

	public void setOldVal(String oldVal)
	{
		this.oldVal = oldVal;
	}

	public String getNewVal()
	{
		return newVal;
	}

	public void setNewVal(String newVal)
	{
		this.newVal = newVal;
	}

	public String getDmlFetName()
	{
		return dmlFetName;
	}

	public void setDmlFetName(String dmlFetName)
	{
		this.dmlFetName = dmlFetName;
	}

	public String getDdDate()
	{
		return ddDate;
	}

	public void setDdDate(String ddDate)
	{
		this.ddDate = ddDate;
	}

	public String getSwitchId()
	{
		return switchId;
	}

	public void setSwitchId(String switchId)
	{
		this.switchId = switchId;
	}

}
