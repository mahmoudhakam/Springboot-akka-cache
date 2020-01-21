package com.se.part.search.dto;

import org.apache.solr.client.solrj.beans.Field;

public class ArrowACLDTO
{
	private String comID;

	public String getComID()
	{
		return comID;
	}

	@Field("COM_ID")
	public void setComID(String comID)
	{
		this.comID = comID;
	}

}
