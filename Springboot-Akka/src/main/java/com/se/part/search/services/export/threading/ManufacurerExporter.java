package com.se.part.search.services.export.threading;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
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

import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.export.NameValueDTO;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.ExportHttpClient;

@Service
public class ManufacurerExporter
{
	@Value("#{environment['export.manufacturerSection.url']}")
	private String url;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PartSearchHelperService helperService;
	@Autowired
	private RestTemplate restTemplate;

	public void export(List<String> actorComIDs, Map<String, List<FeatureNameValueDTO>> manufacurerConcurrentMap)
	{
		long start = System.currentTimeMillis();
		Collection<String> parts = actorComIDs;
		try
		{
			ExportHttpClient<Map<String, List<NameValueDTO>>> httpClient = new ExportHttpClient<>();
			HttpHeaders headers = getRequestHeaders();
			ManufacurerExportRequest requestBody = getRequestBody(parts);
			HttpEntity entity = new HttpEntity<>(requestBody, headers);
			URI uri = helperService.getTargetEndpoint(url, null);
			ResponseEntity<Map<String, List<NameValueDTO>>> response = httpClient.callRestEndpoint(HttpMethod.POST, uri, entity, restTemplate, new ParameterizedTypeReference<Map<String, List<NameValueDTO>>>() {
			});
			if(response.getStatusCode() == HttpStatus.OK)
			{
				Map<String, List<NameValueDTO>> result = response.getBody();
				if(result != null && !result.isEmpty())
				{
					result.entrySet().forEach(e -> {
						List<NameValueDTO> returnedNamesAndValues = e.getValue();
						List<FeatureNameValueDTO> features = transformFeatures(returnedNamesAndValues);
						manufacurerConcurrentMap.put(e.getKey(), features);
					});
					appendNotFoundParts(manufacurerConcurrentMap, parts);
				}
				else
				{
					returnEmptyMap(parts, manufacurerConcurrentMap);
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Error during exporting Manufacurer", e);
			returnEmptyMap(parts, manufacurerConcurrentMap);
		}
		long end = System.currentTimeMillis() - start;
		logger.info("Manufacurer Export has been finished in:{} ms", end);
	}

	private List<FeatureNameValueDTO> transformFeatures(List<NameValueDTO> returnedNamesAndValues)
	{
		List<FeatureNameValueDTO> features = new LinkedList<>();
		returnedNamesAndValues.forEach(feature -> {
			features.add(new FeatureNameValueDTO(feature.getName(), feature.getValue()));
		});
		return features;
	}

	private void returnEmptyMap(Collection<String> parts, Map<String, List<FeatureNameValueDTO>> manufacurerConcurrentMap)
	{
		parts.forEach(p -> {
			manufacurerConcurrentMap.put(p, new ArrayList<>());
		});
	}

	private void appendNotFoundParts(Map<String, List<FeatureNameValueDTO>> manufacurerConcurrentMap, Collection<String> parts)
	{
		parts.forEach(p -> {
			manufacurerConcurrentMap.computeIfAbsent(p, k -> new ArrayList<>());
		});
	}

	private ManufacurerExportRequest getRequestBody(Collection<String> parts)
	{
		ManufacurerExportRequest requestBody = new ManufacurerExporter.ManufacurerExportRequest();
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

	public class ManufacurerExportRequest
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

}
