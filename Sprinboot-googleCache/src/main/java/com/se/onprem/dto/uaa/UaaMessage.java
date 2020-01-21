package com.se.onprem.dto.uaa;

import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;

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
public class UaaMessage
{

	private User user;
	private String token;
	private String context;
	private String method;
	private LinkedMultiValueMap<String, Object> postPayload;
	private Map<String, String[]> params;
}
