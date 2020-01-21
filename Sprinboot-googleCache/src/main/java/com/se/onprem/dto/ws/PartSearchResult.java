package com.se.onprem.dto.ws;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartSearchResult
{
	@JsonProperty("RequestedCPN")
	private String requestedCpn;
	@JsonProperty("ReqPartNumber")
	private String requestedMPN;
	@JsonProperty("ReqKeyword")
	private String reqKeyword;
	@JsonProperty("ReqManufacturer")
	private String requestedMan;

	@JsonProperty("PartList")
	private List<PartSearchDTO> partResult;

}
