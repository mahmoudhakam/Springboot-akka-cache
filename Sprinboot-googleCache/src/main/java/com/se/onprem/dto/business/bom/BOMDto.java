package com.se.onprem.dto.business.bom;

import java.time.Instant;
import java.util.Date;

import com.se.onprem.util.annotations.SolrField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BOMDto {
	@SolrField("BOM_ID")
	long bomId;

	@SolrField("BOM_NAME")
	String bomName;

	@SolrField("CREATION_DATE")
	private Instant creationdate;

	@SolrField("MODIFICATION_DATE")
	private Instant modifiactionDate;

	@SolrField("IS_INDENTED")
	private int indentedBOM;

	@SolrField("MAX_LEVEL")
	private Integer maxLevel;
	
	@SolrField("MIN_LEVEL")
	private Integer minLevel;

	private Long partsCount;
	
}
