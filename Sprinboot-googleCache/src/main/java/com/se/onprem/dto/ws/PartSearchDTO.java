package com.se.onprem.dto.ws;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
@JsonPropertyOrder({ "comID", "partNumber", "manufacturer", "manufacturerId", "plName", "description", "lifecycle", "rohs", "rohsVersion",
		"smallImage" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartSearchDTO
{
	@JsonProperty("ComID")
	private String comID;

	@JsonProperty("PartNumber")
	private String partNumber;
	@JsonProperty("NanPartNumber")
	private String nanPartNumber;

	@JsonProperty("Manufacturer")
	private String manufacturer;

	@JsonProperty("ManufacturerId")
	private String manufacturerId;

	@JsonProperty("PlName")
	private String plName;

	@JsonProperty("PlID")
	private String plId;

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

	@JsonProperty("CustomerData")
	private List<CustomerPart> customerDataDTOList;

	public List<CustomerPart> getCustomerDataDTOList()
	{
		return customerDataDTOList;
	}

	public void addCustomerPart(CustomerPart part)
	{
		if(customerDataDTOList == null)
		{
			customerDataDTOList = new ArrayList<>();
		}
		this.customerDataDTOList.add(part);
	}

	public void setCustomerDataDTOList(List<CustomerPart> customerDataDTOList)
	{
		this.customerDataDTOList = customerDataDTOList;
	}

}
