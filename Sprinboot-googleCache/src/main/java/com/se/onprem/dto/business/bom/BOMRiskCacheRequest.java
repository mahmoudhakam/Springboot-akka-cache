package com.se.onprem.dto.business.bom;

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BOMRiskCacheRequest
{
	private String bomId;
	@EqualsAndHashCode.Exclude
	private Map<String, Map<String, Set<String>>> cachedBOMFacets;
	@EqualsAndHashCode.Exclude
	private String token;

}
