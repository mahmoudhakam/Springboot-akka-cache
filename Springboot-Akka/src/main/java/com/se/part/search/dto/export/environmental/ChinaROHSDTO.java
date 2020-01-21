
package com.se.part.search.dto.export.environmental;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "comId", "chinaId", "cadmiumConc", "cadmiumFlag", "chromiumConc", "chromiumFlag", "leadConc", "leadFlag", "mercuryConc", "mercuryFlag", "pbbConc", "pbbFlag", "pbdeConc", "pbdeFlag", "epup", "sources", "leadFree" })
public class ChinaROHSDTO
{

	@JsonProperty("comId")
	private Integer comId;
	@JsonProperty("chinaId")
	private Integer chinaId;
	@JsonProperty("cadmiumConc")
	private String cadmiumConc;
	@JsonProperty("cadmiumFlag")
	private String cadmiumFlag;
	@JsonProperty("chromiumConc")
	private String chromiumConc;
	@JsonProperty("chromiumFlag")
	private String chromiumFlag;
	@JsonProperty("leadConc")
	private String leadConc;
	@JsonProperty("leadFlag")
	private String leadFlag;
	@JsonProperty("mercuryConc")
	private String mercuryConc;
	@JsonProperty("mercuryFlag")
	private String mercuryFlag;
	@JsonProperty("pbbConc")
	private String pbbConc;
	@JsonProperty("pbbFlag")
	private String pbbFlag;
	@JsonProperty("pbdeConc")
	private String pbdeConc;
	@JsonProperty("pbdeFlag")
	private String pbdeFlag;
	@JsonProperty("epup")
	private String epup;
	@JsonProperty("sources")
	private List<Source> sources = null;
	@JsonProperty("leadFree")
	private String leadFree;

	@JsonProperty("comId")
	public Integer getComId()
	{
		return comId;
	}

	@JsonProperty("comId")
	public void setComId(Integer comId)
	{
		this.comId = comId;
	}

	@JsonProperty("chinaId")
	public Integer getChinaId()
	{
		return chinaId;
	}

	@JsonProperty("chinaId")
	public void setChinaId(Integer chinaId)
	{
		this.chinaId = chinaId;
	}

	@JsonProperty("cadmiumConc")
	public String getCadmiumConc()
	{
		return cadmiumConc;
	}

	@JsonProperty("cadmiumConc")
	public void setCadmiumConc(String cadmiumConc)
	{
		this.cadmiumConc = cadmiumConc;
	}

	@JsonProperty("cadmiumFlag")
	public String getCadmiumFlag()
	{
		return cadmiumFlag;
	}

	@JsonProperty("cadmiumFlag")
	public void setCadmiumFlag(String cadmiumFlag)
	{
		this.cadmiumFlag = cadmiumFlag;
	}

	@JsonProperty("chromiumConc")
	public String getChromiumConc()
	{
		return chromiumConc;
	}

	@JsonProperty("chromiumConc")
	public void setChromiumConc(String chromiumConc)
	{
		this.chromiumConc = chromiumConc;
	}

	@JsonProperty("chromiumFlag")
	public String getChromiumFlag()
	{
		return chromiumFlag;
	}

	@JsonProperty("chromiumFlag")
	public void setChromiumFlag(String chromiumFlag)
	{
		this.chromiumFlag = chromiumFlag;
	}

	@JsonProperty("leadConc")
	public String getLeadConc()
	{
		return leadConc;
	}

	@JsonProperty("leadConc")
	public void setLeadConc(String leadConc)
	{
		this.leadConc = leadConc;
	}

	@JsonProperty("leadFlag")
	public String getLeadFlag()
	{
		return leadFlag;
	}

	@JsonProperty("leadFlag")
	public void setLeadFlag(String leadFlag)
	{
		this.leadFlag = leadFlag;
	}

	@JsonProperty("mercuryConc")
	public String getMercuryConc()
	{
		return mercuryConc;
	}

	@JsonProperty("mercuryConc")
	public void setMercuryConc(String mercuryConc)
	{
		this.mercuryConc = mercuryConc;
	}

	@JsonProperty("mercuryFlag")
	public String getMercuryFlag()
	{
		return mercuryFlag;
	}

	@JsonProperty("mercuryFlag")
	public void setMercuryFlag(String mercuryFlag)
	{
		this.mercuryFlag = mercuryFlag;
	}

	@JsonProperty("pbbConc")
	public String getPbbConc()
	{
		return pbbConc;
	}

	@JsonProperty("pbbConc")
	public void setPbbConc(String pbbConc)
	{
		this.pbbConc = pbbConc;
	}

	@JsonProperty("pbbFlag")
	public String getPbbFlag()
	{
		return pbbFlag;
	}

	@JsonProperty("pbbFlag")
	public void setPbbFlag(String pbbFlag)
	{
		this.pbbFlag = pbbFlag;
	}

	@JsonProperty("pbdeConc")
	public String getPbdeConc()
	{
		return pbdeConc;
	}

	@JsonProperty("pbdeConc")
	public void setPbdeConc(String pbdeConc)
	{
		this.pbdeConc = pbdeConc;
	}

	@JsonProperty("pbdeFlag")
	public String getPbdeFlag()
	{
		return pbdeFlag;
	}

	@JsonProperty("pbdeFlag")
	public void setPbdeFlag(String pbdeFlag)
	{
		this.pbdeFlag = pbdeFlag;
	}

	@JsonProperty("epup")
	public String getEpup()
	{
		return epup;
	}

	@JsonProperty("epup")
	public void setEpup(String epup)
	{
		this.epup = epup;
	}

	@JsonProperty("sources")
	public List<Source> getSources()
	{
		return sources;
	}

	@JsonProperty("sources")
	public void setSources(List<Source> sources)
	{
		this.sources = sources;
	}

	@JsonProperty("leadFree")
	public String getLeadFree()
	{
		return leadFree;
	}

	@JsonProperty("leadFree")
	public void setLeadFree(String leadFree)
	{
		this.leadFree = leadFree;
	}

}
