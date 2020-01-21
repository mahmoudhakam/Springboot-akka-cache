package com.se.part.search.dto.export;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.FIELD)
public class PCNListDTO
{
	@JsonProperty("pcnList")
	private List<PCNResponseDTO> pcnList;

	public List<PCNResponseDTO> getPcnList()
	{
		return pcnList;
	}

	public void setPcnList(List<PCNResponseDTO> pcnList)
	{
		this.pcnList = pcnList;
	}

	public PCNListDTO()
	{
	}
}
