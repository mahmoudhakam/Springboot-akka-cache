package com.se.onprem.dto.ws;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.onprem.messages.PartSearchStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({ "status", "totalItems", "serviceTime", "partList" })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartSearchResponse
{
	@JsonProperty("Status")
	private PartSearchStatus status;
	/*
	 * @JsonProperty("TotalItems") private Long totalItems;
	 */
	// @JsonProperty("ServiceTime")
	@JsonIgnore
	private String serviceTime;
	@JsonProperty("PartResult")
	private List<PartSearchResult> partList;
	@JsonProperty("Steps")
	private List<PartSearchStep> steps;

	public PartSearchResponse(PartSearchStatus status)
	{
		this.status = status;
	}

}
