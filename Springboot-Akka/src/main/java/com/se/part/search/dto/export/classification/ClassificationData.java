package com.se.part.search.dto.export.classification;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class ClassificationData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String classValue;
	private String clasName;
	private String codeDefinition;

	public ClassificationData()
	{
	}

	public ClassificationData(String classValue, String clasName, String codeDefinition)
	{
		super();
		this.classValue = classValue;
		this.clasName = clasName;
		this.codeDefinition = codeDefinition;
	}

	public String getClassValue()
	{
		return classValue;
	}

	public void setClassValue(String classValue)
	{
		this.classValue = classValue;
	}

	public String getClasName()
	{
		return clasName;
	}

	public void setClasName(String clasName)
	{
		this.clasName = clasName;
	}

	public String getCodeDefinition()
	{
		return codeDefinition;
	}

	public void setCodeDefinition(String codeDefinition)
	{
		this.codeDefinition = codeDefinition;
	}

}
