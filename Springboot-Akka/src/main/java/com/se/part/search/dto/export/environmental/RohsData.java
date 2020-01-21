
package com.se.part.search.dto.export.environmental;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "2011/65/EU", "2002/95/EC", "2011/65/EU, 2015/863" })
public class RohsData
{

	@JsonProperty("2011/65/EU")
	private List<com.se.part.search.dto.export.environmental._201165EU> _201165EU = null;
	@JsonProperty("2002/95/EC")
	private List<com.se.part.search.dto.export.environmental._200295EC> _200295EC = null;
	@JsonProperty("2011/65/EU, 2015/863")
	private List<com.se.part.search.dto.export.environmental._201165EU2015863> _201165EU2015863 = null;

	@JsonProperty("2011/65/EU")
	public List<com.se.part.search.dto.export.environmental._201165EU> get201165EU()
	{
		return _201165EU;
	}

	@JsonProperty("2011/65/EU")
	public void set201165EU(List<com.se.part.search.dto.export.environmental._201165EU> _201165EU)
	{
		this._201165EU = _201165EU;
	}

	@JsonProperty("2002/95/EC")
	public List<com.se.part.search.dto.export.environmental._200295EC> get200295EC()
	{
		return _200295EC;
	}

	@JsonProperty("2002/95/EC")
	public void set200295EC(List<com.se.part.search.dto.export.environmental._200295EC> _200295EC)
	{
		this._200295EC = _200295EC;
	}

	@JsonProperty("2011/65/EU, 2015/863")
	public List<com.se.part.search.dto.export.environmental._201165EU2015863> get_201165EU2015863()
	{
		return _201165EU2015863;
	}

	@JsonProperty("2011/65/EU, 2015/863")
	public void set_201165EU2015863(List<com.se.part.search.dto.export.environmental._201165EU2015863> _201165eu2015863)
	{
		_201165EU2015863 = _201165eu2015863;
	}

}
