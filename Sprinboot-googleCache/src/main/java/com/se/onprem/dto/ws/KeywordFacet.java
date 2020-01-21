package com.se.onprem.dto.ws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonPropertyOrder({ "name", "count" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KeywordFacet
{
	private String name;
	private Long count;
}
