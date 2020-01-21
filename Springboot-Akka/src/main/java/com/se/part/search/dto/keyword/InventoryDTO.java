package com.se.part.search.dto.keyword;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InventoryDTO
{
	@JsonProperty("Inventory")
	private String quantity;
	@JsonProperty("Distributor")
	private String distributor;
	@JsonProperty("BuyNowLink")
	private String buyNowLink;

	public String getQuantity()
	{
		return quantity;
	}

	public void setQuantity(String quantity)
	{
		this.quantity = quantity;
	}

	public String getDistributor()
	{
		return distributor;
	}

	public void setDistributor(String distributor)
	{
		this.distributor = distributor;
	}

	public String getBuyNowLink()
	{
		return buyNowLink;
	}

	public void setBuyNowLink(String buyNowLink)
	{
		this.buyNowLink = buyNowLink;
	}

}
