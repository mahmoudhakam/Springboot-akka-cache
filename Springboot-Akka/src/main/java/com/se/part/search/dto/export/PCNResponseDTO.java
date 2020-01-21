package com.se.part.search.dto.export;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.FIELD)
public class PCNResponseDTO
{
	@JsonProperty("Id")
	private String Id;
	@JsonProperty("Pcn PDF")
	private String pcnPDF;
	@JsonProperty("Description Of Change")
	private String desOfChange;
	@JsonProperty("Man Id")
	private String manId;
	@JsonProperty("Com Id")
	private String comId;
	@JsonProperty("Man")
	private String man;
	@JsonProperty("Number of Affected Product Name")
	private String numberOfAffectedProductName;
	@JsonProperty("Affected Product Name")
	private String affectedProductName;
	@JsonProperty("Type Of Change")
	private String typeOfChange;
	@JsonProperty("PCN Number")
	private String pcnNumber;
	@JsonProperty("Inserted Date")
	private String insertionDate;
	@JsonProperty("Effected Date")
	private String effectiveDate;
	@JsonProperty("Notification Date")
	private String notificationDate;

	public PCNResponseDTO()
	{
	}

	public String getId()
	{
		return Id;
	}

	public void setId(String id)
	{
		Id = id;
	}

	public String getPcnPDF()
	{
		return pcnPDF;
	}

	public void setPcnPDF(String pcnPDF)
	{
		this.pcnPDF = pcnPDF;
	}

	public String getDesOfChange()
	{
		return desOfChange;
	}

	public void setDesOfChange(String desOfChange)
	{
		this.desOfChange = desOfChange;
	}

	public String getManId()
	{
		return manId;
	}

	public void setManId(String manId)
	{
		this.manId = manId;
	}

	public String getComId()
	{
		return comId;
	}

	public void setComId(String comId)
	{
		this.comId = comId;
	}

	public String getMan()
	{
		return man;
	}

	public void setMan(String man)
	{
		this.man = man;
	}

	public String getNumberOfAffectedProductName()
	{
		return numberOfAffectedProductName;
	}

	public void setNumberOfAffectedProductName(String numberOfAffectedProductName)
	{
		this.numberOfAffectedProductName = numberOfAffectedProductName;
	}

	public String getAffectedProductName()
	{
		return affectedProductName;
	}

	public void setAffectedProductName(String affectedProductName)
	{
		this.affectedProductName = affectedProductName;
	}

	public String getTypeOfChange()
	{
		return typeOfChange;
	}

	public void setTypeOfChange(String typeOfChange)
	{
		this.typeOfChange = typeOfChange;
	}

	public String getPcnNumber()
	{
		return pcnNumber;
	}

	public void setPcnNumber(String pcnNumber)
	{
		this.pcnNumber = pcnNumber;
	}

	public String getInsertionDate()
	{
		return insertionDate;
	}

	public void setInsertionDate(String insertionDate)
	{
		this.insertionDate = insertionDate;
	}

	public String getEffectiveDate()
	{
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate)
	{
		this.effectiveDate = effectiveDate;
	}

	public String getNotificationDate()
	{
		return notificationDate;
	}

	public void setNotificationDate(String notificationDate)
	{
		this.notificationDate = notificationDate;
	}

}
