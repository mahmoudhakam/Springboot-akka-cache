package com.se.onprem.dto.business;

import java.util.List;

import com.se.onprem.dto.ws.PartSearchDTO;

public class ACLQueryResult
{

	private List<String>foundComIds;
	private List<PartSearchDTO> partsWithNoComid;
	public List<String> getFoundComIds()
	{
		return foundComIds;
	}
	public void setFoundComIds(List<String> foundComIds)
	{
		this.foundComIds = foundComIds;
	}
	public List<PartSearchDTO> getPartsWithNoComid()
	{
		return partsWithNoComid;
	}
	public void setPartsWithNoComid(List<PartSearchDTO> partsWithNoComid)
	{
		this.partsWithNoComid = partsWithNoComid;
	}
	public boolean isEmpty()
	{
		
		return (partsWithNoComid==null||partsWithNoComid.isEmpty())&&(foundComIds==null||foundComIds.isEmpty());
	}
	public boolean hasPartsWithComids()
	{
		
		return (partsWithNoComid!=null &&!partsWithNoComid.isEmpty());
	}
}
