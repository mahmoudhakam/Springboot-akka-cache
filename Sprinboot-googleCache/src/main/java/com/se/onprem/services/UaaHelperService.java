package com.se.onprem.services;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.se.onprem.dto.uaa.UaaResponse;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.util.UaaConstants;

@Service
public class UaaHelperService
{
	@Autowired
	HelperService helper;

	private RestTemplate restTemplate = new RestTemplate();

	public UaaResponse sendRequest(String url, String params, String token, HttpMethod httpMethod)
	{
		UaaResponse uaaResponse = new UaaResponse();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if(!StringUtils.isEmpty(token))
		{
			headers.set(UaaConstants.AUTHORIZATION,token);
		}

		HttpEntity<String> entity = new HttpEntity<String>(params, headers);

		try
		{
			ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, entity, String.class);
			uaaResponse.setJsonResponse(response.getBody());
			uaaResponse.setHttpStatus(response.getStatusCode());

		}
		catch(HttpClientErrorException httpClientErrorException)
		{
			uaaResponse.setHttpStatus(httpClientErrorException.getStatusCode());
		}

		return uaaResponse;

	}

//	public RestResponseWrapper sendGatewayRequest(String url, Map<String, String> params, String token)
//	{
//		RestResponseWrapper responseWrapper = new RestResponseWrapper();
//
//		try
//		{
//			LinkedMultiValueMap<String, Object> headers = new LinkedMultiValueMap<>();
//
//			responseWrapper = helper.getResposneFromURL(params, url, headers);
//		}
//		catch(UnsupportedEncodingException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return responseWrapper;
//
//	}

}
