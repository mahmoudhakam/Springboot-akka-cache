package com.se.part.search.services.export;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ExportHttpClient<T>
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public ResponseEntity<T> callRestEndpoint(HttpMethod httpMethod, URI endPoint, HttpEntity<?> entity, RestTemplate restTemplate, Class<T> theClass)
	{
		ResponseEntity<T> responseEntity = null;
		try
		{
			responseEntity = restTemplate.exchange(endPoint, httpMethod, entity, theClass);
		}
		catch(HttpClientErrorException e)
		{
			logger.error("Error calling api", e);
			if(e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
			{
				responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
			{
				responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			return responseEntity;
		}
		if(responseEntity != null)
		{
			// String body = responseEntity.getBody();
			// logger.info("Getting body for url:{} body:{}", endPoint, body);
			if(responseEntity.getStatusCode() == HttpStatus.OK)
			{
				return responseEntity;
			}
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<T> callRestEndpoint(HttpMethod httpMethod, URI endPoint, HttpEntity<?> entity, RestTemplate restTemplate, ParameterizedTypeReference<T> parameterizedTypeReference)
	{
		ResponseEntity<T> responseEntity = null;
		try
		{
			responseEntity = restTemplate.exchange(endPoint, httpMethod, entity, parameterizedTypeReference);
		}
		catch(HttpClientErrorException e)
		{
			logger.error("Error calling api", e);
			if(e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
			{
				responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
			{
				responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			return responseEntity;
		}
		if(responseEntity != null)
		{
			if(responseEntity.getStatusCode() == HttpStatus.OK)
			{
				// logger.info("Getting body for url:{} ", endPoint);
				return responseEntity;
			}
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<String> getStringResponsePOST(HttpMethod httpMethod, URI endPoint, HttpEntity<?> entity, RestTemplate restTemplate)
	{
		ResponseEntity<String> responseEntity = null;
		try
		{
			responseEntity = restTemplate.exchange(endPoint, httpMethod, entity, String.class);
		}
		catch(HttpClientErrorException e)
		{
			logger.error("Error calling api", e);
			if(e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
			{
				responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
			{
				responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			return responseEntity;
		}
		if(responseEntity != null)
		{
			if(responseEntity.getStatusCode() == HttpStatus.OK)
			{
				// logger.info("Getting body for url:{} ", endPoint);
				return responseEntity;
			}
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
