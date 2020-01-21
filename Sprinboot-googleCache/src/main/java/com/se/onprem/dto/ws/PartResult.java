package com.se.onprem.dto.ws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "comID", "partNumber", "manufacturer", "manufacturerId", "plName", "description", "lifecycle", "rohs", "rohsVersion",
		"smallImage" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartResult
{
	@JsonProperty("comID")
	private String comID;

	// changed from "PartNumber" to "partNumber" for VM1 partSearch request
	@JsonProperty("partNumber")
	private String partNumber;

	// changed from "Manufacturer" to "manufacturer" for VM1 partSearch request
	@JsonProperty("manufacturer")
	private String manufacturer;

	@JsonProperty("ManufacturerId")
	private String manufacturerId;

	@JsonProperty("PlName")
	private String plName;

	@JsonProperty("Description")
	private String description;

	@JsonProperty("LifeCycle")
	private String lifecycle;

	@JsonProperty("ROHS")
	private String rohs;

	@JsonProperty("RoHSVersion")
	private String rohsVersion;

	@JsonProperty("SmallImage")
	private String smallImage;

	private CustomerPart customerDataDTO;
}
