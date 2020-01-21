package com.se.part.search.dto.keyword.parametric;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "comId", "fullPart", "description", "datasheetDTO" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ParametricSearchResultDTO
{

	private String comId;
	private String fullPart;
	private String description;
	private String manId;
	private String manName;
	private String plId;
	private String plName;
	private String rohs;
	private String rohsVersion;
	private String lifeCycle;
	private String starRatig;
	private String smallImage;
	private String largeImage;
	private List<FeatureDTO> searchResultParametricFeatures;

	public String getComId()
	{
		return comId;
	}

	public void setComId(String comId)
	{
		this.comId = comId;
	}

	public String getFullPart()
	{
		return fullPart;
	}

	public void setFullPart(String fullPart)
	{
		this.fullPart = fullPart;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getManId()
	{
		return manId;
	}

	public void setManId(String manId)
	{
		this.manId = manId;
	}

	public String getManName()
	{
		return manName;
	}

	public void setManName(String manName)
	{
		this.manName = manName;
	}

	public String getPlId()
	{
		return plId;
	}

	public void setPlId(String plId)
	{
		this.plId = plId;
	}

	public String getPlName()
	{
		return plName;
	}

	public void setPlName(String plName)
	{
		this.plName = plName;
	}

	public String getRohs()
	{
		return rohs;
	}

	public void setRohs(String rohs)
	{
		this.rohs = rohs;
	}

	public String getRohsVersion()
	{
		return rohsVersion;
	}

	public void setRohsVersion(String rohsVersion)
	{
		this.rohsVersion = rohsVersion;
	}

	public String getLifeCycle()
	{
		return lifeCycle;
	}

	public void setLifeCycle(String lifeCycle)
	{
		this.lifeCycle = lifeCycle;
	}

	public String getStarRatig()
	{
		return starRatig;
	}

	public void setStarRatig(String starRatig)
	{
		this.starRatig = starRatig;
	}

	public String getSmallImage()
	{
		return smallImage;
	}

	public void setSmallImage(String smallImage)
	{
		this.smallImage = smallImage;
	}

	public String getLargeImage()
	{
		return largeImage;
	}

	public void setLargeImage(String largeImage)
	{
		this.largeImage = largeImage;
	}

	public List<FeatureDTO> getSearchResultParametricFeatures()
	{
		return searchResultParametricFeatures;
	}

	public void setSearchResultParametricFeatures(List<FeatureDTO> searchResultParametricFeatures)
	{
		this.searchResultParametricFeatures = searchResultParametricFeatures;
	}

}
