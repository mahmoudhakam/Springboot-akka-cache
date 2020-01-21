package com.se.part.search.services.export.threading;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.se.part.search.dto.export.ParametricFeatureDTO;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.ExportHttpClient;

@Service
public class ParametricExporter
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("#{environment['export.parametricSection.url']}")
	private String url;
	@Autowired
	protected PartSearchHelperService helperService;
	@Autowired
	protected RestTemplate restTemplate;

	private ParametricFeaturesExportRequest getRequestBody(Collection<String> parts)
	{
		ParametricFeaturesExportRequest requestBody = new ParametricExporter.ParametricFeaturesExportRequest();
		requestBody.setComIds(parts);
		return requestBody;
	}

	private HttpHeaders getRequestHeaders()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	public class ParametricFeaturesExportRequest
	{
		private Collection<String> comIds;

		public Collection<String> getComIds()
		{
			return comIds;
		}

		public void setComIds(Collection<String> comIds)
		{
			this.comIds = comIds;
		}
	}

	public void export(List<String> comIDs, Map<String, List<ParametricFeatureDTO>> parametricConcurrentMap)
	{
		long start = System.currentTimeMillis();
		try
		{
			ExportHttpClient<Map<String, List<ParametricFeatureDTO>>> httpClient = new ExportHttpClient<>();
			HttpHeaders headers = getRequestHeaders();
			ParametricFeaturesExportRequest requestBody = getRequestBody(comIDs);
			HttpEntity entity = new HttpEntity<>(requestBody, headers);
			URI uri = helperService.getTargetEndpoint(url, null);
			ResponseEntity<Map<String, List<ParametricFeatureDTO>>> response = httpClient.callRestEndpoint(HttpMethod.POST, uri, entity, restTemplate, new ParameterizedTypeReference<Map<String, List<ParametricFeatureDTO>>>() {
			});
			if(response.getStatusCode() == HttpStatus.OK)
			{
				Map<String, List<ParametricFeatureDTO>> result = response.getBody();
				if(result != null && !result.isEmpty())
				{
					result.entrySet().forEach(e -> {
						String comId = e.getKey();
						List<ParametricFeatureDTO> parametricList = e.getValue();
						parametricConcurrentMap.put(comId, parametricList);
					});
				}
				else
				{
					returnEmptyMap(comIDs, parametricConcurrentMap);
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Error during exporting parametric", e);
			returnEmptyMap(comIDs, parametricConcurrentMap);
		}
		long end = System.currentTimeMillis() - start;
		logger.info("Parametric Exporter Thread ends in:{}", end);
	}

	private void returnEmptyMap(Collection<String> parts, Map<String, List<ParametricFeatureDTO>> parametricConcurrentMap)
	{
		parts.forEach(p -> {
			parametricConcurrentMap.put(p, new ArrayList<>());
		});
	}
}
