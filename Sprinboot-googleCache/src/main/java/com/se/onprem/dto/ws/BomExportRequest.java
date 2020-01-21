package com.se.onprem.dto.ws;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BomExportRequest
{
	private List<String> comIDs;
	private Map<String, List<String>> categories;
	private int batchSize;

}
