package com.se.onprem.dto.ws;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.se.onprem.dto.business.bom.BOMDto;
import com.se.onprem.dto.business.bom.BOMRow;
import com.se.onprem.dto.business.bom.BOMSaveResult;
import com.se.onprem.dto.business.bom.BomExportFeature;
import com.se.onprem.dto.uaa.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
public class RestResponseWrapper
{

	@JsonProperty("Status")
	private Status status;

	private String serviceTime;

	@JsonProperty("TotalItems")
	private Long totalItems;

	@JsonProperty("PartResult")
	private List<PartSearchResult> keywordResults;

	@JsonProperty("BOMResult")
	private List<BOMRow> bomResult;

	@JsonProperty("BOMList")
	private List<BOMDto> bomList;

	@JsonProperty("AddedParts")
	private List<CustomerPart> addpartsResult;

	@JsonProperty("Facets")
	private KeywordFacetsWrapper keywordFacetsWrapper;

	@JsonProperty("BOMFacets")
	private Map<String, List<KeywordFacet>> bomFacets;

	@JsonProperty("FacetsMap")
	private Map<String, Map<String, Set<String>>> facetMap;

	private String resultType;

	@JsonProperty("BOMSaveResult")
	private BOMSaveResult bomSaveResult;

	@JsonProperty("BOMDeleteResult")
	private Boolean bomDeleted;

	@JsonProperty("KeywordOperator")
	private String keywordOperator;

	@JsonProperty("AutoCompleteResult")
	private AutoCompleteResult autoCompleteResult;

	@JsonProperty("Search Steps")
	private List<DebugStep> steps;

	@JsonProperty("BOMData")
	private BOMDto bomData;

	@Builder.Default
	private HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

	@JsonProperty("id_token")
	private String token;

	private Workbook excelFile;

	@JsonProperty("user")
	private User user;

	@JsonProperty("PartList")
	private Map<String, Map<String, List<BomExportFeature>>> partList;

	public RestResponseWrapper()
	{
	}

	public RestResponseWrapper(Status status)
	{
		this.status = status;
	}

	public RestResponseWrapper(HttpStatus httpStatus)
	{
		this.httpStatus = httpStatus;
	}

	public boolean isSuccessufull()
	{

		return status != null && status.isSuccess() != null && status.isSuccess();
	}

}
