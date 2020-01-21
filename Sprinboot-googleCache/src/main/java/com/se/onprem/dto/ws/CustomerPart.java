package com.se.onprem.dto.ws;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.se.onprem.util.annotations.SolrField;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class CustomerPart
{
	@SolrField("PART_ID")
	private long partID;
	@SolrField("CPN")
	@JsonProperty("cpn")
	private String cpn;
	@SolrField("MPN")
	private String mpn;
	@SolrField("MAN_NAME")
	private String man;
	@SolrField("COM_ID")
	private String comID;
	private List<FeatureDTO> customFeatures;
	@SolrField("CUSTOM_FEATURES")
	private String customFeaturesJson;
	@SolrField("CUSTOM_KEY")
	private String compositeKey;
	@SolrField("MAN_ID")
	private String manId;
	@SolrField("PL_ID")
	private String plId;
	@SolrField("NAN_PARTNUM_EXACT")
	private String nanPart;

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comID == null) ? 0 : comID.hashCode());
		result = prime * result + ((cpn == null) ? 0 : cpn.hashCode());
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
		CustomerPart other = (CustomerPart) obj;
		if(comID == null)
		{
			if(other.comID != null)
				return false;
		}
		else if(!comID.equals(other.comID))
			return false;
		if(cpn == null)
		{
			if(other.cpn != null)
				return false;
		}
		else if(!cpn.equals(other.cpn))
			return false;
		return true;
	}

}
