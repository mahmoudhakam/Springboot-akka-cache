package com.se.onprem.dto.business.bom;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.onprem.util.annotations.SolrField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonPropertyOrder({ "comID", "partNumber", "nanPartNumber", "manufacturer", "manufacturerId", "plId" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BOMRow
{

	private static Map<String, String> jsonToSolrMap;

	static
	{
		jsonToSolrMap = new HashMap<>();
		jsonToSolrMap.put("ComID", "COM_ID");
		jsonToSolrMap.put("PartNumber", "SE_MPN");
		jsonToSolrMap.put("UploadedPartNumber", "UPLOADED_MPN");
		jsonToSolrMap.put("Manufacturer", "SE_MAN");
		jsonToSolrMap.put("UploadedManufacturer", "UPLOADED_MAN");
		jsonToSolrMap.put("ManufacturerId", "MAN_ID");
		jsonToSolrMap.put("PlID", "PL_ID");
		jsonToSolrMap.put("validationStatusCode", "MATCH_STATUS_CODE");
		jsonToSolrMap.put("MatchStatus", "MATCH_STATUS");
		jsonToSolrMap.put("PartID", "ROW_ID");
		jsonToSolrMap.put("validationStatusCode", "MATCH_STATUS_CODE");
		jsonToSolrMap.put("validationStatusCode", "MATCH_STATUS_CODE");
	}

	public static String getSolrFromJson(String json)
	{
		return jsonToSolrMap.get(json);
	}

	@SolrField("COM_ID")
	@JsonProperty("ComID")
	private long comID;

	@SolrField("SE_MPN")
	@JsonProperty("PartNumber")
	private String partNumber;

	@SolrField("UPLOADED_MPN")
	@JsonProperty("UploadedPartNumber")
	private String uploadedMpn;

	@JsonProperty("NanPartNumber")
	private String nanPartNumber;

	@SolrField("SE_MAN")
	@JsonProperty("Manufacturer")
	private String manufacturer;

	@SolrField("UPLOADED_MAN")
	@JsonProperty("UploadedManufacturer")
	private String uploadedManufacturer;

	@SolrField("MAN_ID")
	@JsonProperty("ManufacturerId")
	private String manufacturerId;

	@JsonProperty("Description")
	private String description;

	@JsonProperty("LifeCycle")
	private String lifecycle;

	@JsonProperty("ROHS")
	private String rohs;

	@JsonProperty("RoHSVersion")
	private String rohsVersion;

	@SolrField("PL_ID")
	@JsonProperty("PlID")
	private String plId;

	@SolrField("MATCH_STATUS_CODE")
	@JsonProperty("validationStatusCode")
	private int validationStatusCode;

	@SolrField("MATCH_STATUS")
	@JsonProperty("MatchStatus")
	private String matchStatus;

	@SolrField("ROW_KEY")
	private String rowKey;

	@JsonProperty("MatchConfidence")
	private float matchConfidence;

	@SolrField("ROW_ID")
	@JsonProperty("PartID")
	private int rowId;

	@SolrField("SIMILAR_COUNT")
	@JsonProperty("SimilarCount")
	private int similarCount;

	@SolrField("BOM_ID")
	private long bomId;
	private String manStatus;

	@SolrField("LEVEL")
	@JsonProperty("LEVEL")
	private int level;

	@SolrField("PARENT_ID")
	@JsonProperty("ParentID")
	private String parentId;

	@SolrField("PATH")
	@JsonProperty("Path")
	private String path;

	@SolrField("ID")
	@JsonProperty("Id")
	private String id;

	@SolrField("HAS_CHILD")
	@JsonProperty("HasChild")
	private int hasChild;

}
