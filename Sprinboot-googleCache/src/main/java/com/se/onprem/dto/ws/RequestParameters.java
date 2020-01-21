package com.se.onprem.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestParameters
{
	private String plName;
	private String plId;
	private int level = 3; // Default is 3 (Product line)
	private String filters;
	private String keyword;
	private int pageSize = 50; // Default is 50 part per page
	private String order;
	private int pageNumber = 1; // Default is 1 so that will be 1-1 = 0 the solr page zero is the first page
	private String last_select;
	private String keywordOperator;
	private String partDetailsComId;

	private String fullURL;
	private String remoteAddress;

}
