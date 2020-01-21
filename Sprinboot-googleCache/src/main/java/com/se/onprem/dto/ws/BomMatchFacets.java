package com.se.onprem.dto.ws;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BomMatchFacets
{

	private Map<String, List<KeywordFacet>> bomMatchFacets;
	private boolean exact;
	private boolean exactOnly;

}
