package com.se.onprem.dto.ws;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartInput
{

	private String partNumber = "";
	private String man = "";
	private String requestedMan = "";
	private String comID = "";

	public PartInput()
	{
		super();
	}

	public PartInput(String partNumber, String requestedMan, String comID)
	{
		super();
		this.partNumber = partNumber;
		this.man = this.requestedMan = requestedMan;
		this.comID = comID;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comID == null) ? 0 : comID.hashCode());
		result = prime * result + ((man == null) ? 0 : man.hashCode());
		result = prime * result + ((partNumber == null) ? 0 : partNumber.hashCode());
		result = prime * result + ((requestedMan == null) ? 0 : requestedMan.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		PartInput other = (PartInput) obj;
		if(comID == null)
		{
			if(other.comID != null)
				return false;
		}
		else if(!comID.equals(other.comID))
			return false;
		if(man == null)
		{
			if(other.man != null)
				return false;
		}
		else if(!man.equals(other.man))
			return false;
		if(partNumber == null)
		{
			if(other.partNumber != null)
				return false;
		}
		else if(!partNumber.equals(other.partNumber))
			return false;
		if(requestedMan == null)
		{
			if(other.requestedMan != null)
				return false;
		}
		else if(!requestedMan.equals(other.requestedMan))
			return false;
		return true;
	}

}
