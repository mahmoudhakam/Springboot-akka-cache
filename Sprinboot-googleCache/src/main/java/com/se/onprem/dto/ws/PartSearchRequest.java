package com.se.onprem.dto.ws;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartSearchRequest
{

	private String manName;
	private String debugMode;
	private HttpServletRequest request;
	private String fullURL;
	private String remoteAddress;
	@Builder.Default
	private String timeTaken = "1";
	@Builder.Default
	private int pageNumber = 1;
	private String start;
	@Builder.Default
	private int pageSize = 25;
	private String partNumber;
	private String mode;
	private boolean boostResults;
	private String autocompleteSection;
	private String wildcardSingle;
	private String wildCardMulti;
	private String excludedComIds;
	private String boostedComIDs;
	private String filters;
	private String order;
	private boolean exact;
	private String token;

}
