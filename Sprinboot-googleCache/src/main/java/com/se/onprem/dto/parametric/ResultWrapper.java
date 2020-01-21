package com.se.onprem.dto.parametric;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.se.onprem.dto.ws.Status;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonPropertyOrder({ "status", "resultCount", "facets", "parts", "taxonomyTree", "taxonomyCountsMap" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResultWrapper
{
	private Long resultCount;
	private Status status;
	private Map<String, Map<String, Long>> taxonomyCountsMap;

}
