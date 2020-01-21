package com.se.part.search.dto.keyword;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.part.search.dto.keyword.parametric.PLTypeDTO;

@JsonPropertyOrder({ "productLineTypes" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Payload
{
	@JsonProperty("productLineTypes")
	private List<PLTypeDTO> productLineTypes;

	public List<PLTypeDTO> getProductLineTypes()
	{
		return productLineTypes;
	}

	public void setProductLineTypes(List<PLTypeDTO> productLineTypes)
	{
		this.productLineTypes = productLineTypes;
	}
}
