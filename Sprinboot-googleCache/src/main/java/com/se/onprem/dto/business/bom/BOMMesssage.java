package com.se.onprem.dto.business.bom;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.se.onprem.dto.ws.KeywordFacet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BOMMesssage
{
	private String bomId;
	private String bomName;
	private List<BOMRow> bomParts;
	private BOMSaveResult bomSaveResult;
	private List<BOMDto> savedBoms;
	private BOMDto saveResult;
	private int pageNumber;
	private int indented;
	private long resultCount;
	private String levels;
	private String paths;
	private String ignoredPaths;
	private Map<String, Map<String, Set<String>>> bomFacets;
	private Map<String, Map<String, Set<String>>> bomRiskFacets;
	private int pageSize;
	private Map<String, List<KeywordFacet>> bomMatchFacets;
	private boolean bomDeleted;
	private Integer maxLevel;
	private Integer minLevel;
	private int rowId;
	private Map<String, List<String>> filtersRequest;
	private Map<String, List<String>> matchFiltersRequest;
	private boolean exact;
	private String lastFilter;
	private String bomExportQuery;
	private List<String> matchStatusExportRequest;
	private String token;
	private String sortField;
	private String sortType;

}
