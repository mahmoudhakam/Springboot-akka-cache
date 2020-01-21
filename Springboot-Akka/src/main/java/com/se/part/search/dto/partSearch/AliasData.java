package com.se.part.search.dto.partSearch;

import java.io.Serializable;

public class AliasData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -72843120161285524L;
	String type;
	String note;

	public AliasData()
	{
	}

	public AliasData(String type, String note)
	{
		super();
		this.type = type;
		this.note = note;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getNote()
	{
		return note;
	}

	public void setNote(String note)
	{
		this.note = note;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		AliasData other = (AliasData) obj;
		if(note == null)
		{
			if(other.note != null)
				return false;
		}
		else if(!note.equals(other.note))
			return false;
		if(type == null)
		{
			if(other.type != null)
				return false;
		}
		else if(!type.equals(other.type))
			return false;
		return true;
	}

}
