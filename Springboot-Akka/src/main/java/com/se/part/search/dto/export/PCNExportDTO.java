package com.se.part.search.dto.export;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.se.part.search.dto.keyword.Status;

@XmlAccessorType(XmlAccessType.FIELD)
public class PCNExportDTO implements Serializable
{
	@JsonProperty("Status")
	private Status status;
	@JsonProperty("pcnMap")
	private Map<String, PCNListDTO> pcnMap;

	public Map<String, PCNListDTO> getPcnMap()
	{
		return pcnMap;
	}

	public void setPcnMap(Map<String, PCNListDTO> pcnMap)
	{
		this.pcnMap = pcnMap;
	}

	public PCNExportDTO()
	{
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

}
