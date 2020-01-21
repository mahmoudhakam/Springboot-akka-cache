package com.se.part.search.dto.bom;

import java.util.Map;

public class BOMStatistics
{

	Map<String, Integer>partMatchStatus;
	Map<String, Integer>manMatchStatus;
	Map<String, Integer>rohsStatus;
	Map<String, Integer>lcStatus;
	public BOMStatistics(Map<String, Integer> partMatchStatus, Map<String, Integer> manMatchStatus, Map<String, Integer> rohsStatus,
			Map<String, Integer> lcStatus)
	{
		super();
		this.partMatchStatus = partMatchStatus;
		this.manMatchStatus = manMatchStatus;
		this.rohsStatus = rohsStatus;
		this.lcStatus = lcStatus;
	}
	public Map<String, Integer> getPartMatchStatus()
	{
		return partMatchStatus;
	}
	public Map<String, Integer> getManMatchStatus()
	{
		return manMatchStatus;
	}
	public Map<String, Integer> getRohsStatus()
	{
		return rohsStatus;
	}
	public Map<String, Integer> getLcStatus()
	{
		return lcStatus;
	}
}
