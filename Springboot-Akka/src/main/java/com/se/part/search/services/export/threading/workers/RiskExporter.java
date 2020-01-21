package com.se.part.search.services.export.threading.workers;

import java.net.URI;
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
import com.se.part.search.dto.export.risk.RiskDTO;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.ExportHttpClient;

@Service
public class RiskExporter
{
	@Value("#{environment['export.riskSection.url']}")
	private String url;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PartSearchHelperService helperService;
	@Autowired
	private RestTemplate restTemplate;

	public void export(List<String> actorComIDs, Map<String, RiskDTO> riskConcurrentMap)
	{
		long start = System.currentTimeMillis();
		Collection<String> parts = actorComIDs;
		try
		{
			ExportHttpClient<Map<String, RiskDTO>> httpClient = new ExportHttpClient<>();
			HttpHeaders headers = getRequestHeaders();
			RiskExportRequest requestBody = getRequestBody(parts);
			HttpEntity entity = new HttpEntity<>(requestBody, headers);
			URI uri = helperService.getTargetEndpoint(url, null);
			ResponseEntity<Map<String, RiskDTO>> response = httpClient.callRestEndpoint(HttpMethod.POST, uri, entity, restTemplate, new ParameterizedTypeReference<Map<String, RiskDTO>>() {
			});
			if(response.getStatusCode() == HttpStatus.OK)
			{
				Map<String, RiskDTO> result = response.getBody();
				if(result != null && !result.isEmpty())
				{
					result.entrySet().forEach(e -> {
						String comId = e.getKey();
						RiskDTO dto = e.getValue();
						dto.getLifeCycleForeCast().forEach(x -> {
							dto.getFetNameValue().add(new FeatureNameValueDTO(x.getName(), x.getValue()));
						});
						dto.setLifeCycleForeCast(null);
						riskConcurrentMap.put(comId, dto);
					});
					appendNotFoundParts(riskConcurrentMap, parts);
				}
				else
				{
					returnEmptyMap(parts, riskConcurrentMap);
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Error during exporting Risk", e);
			returnEmptyMap(parts, riskConcurrentMap);
		}
		long end = System.currentTimeMillis() - start;
		logger.info("Risk Export has been finished in:{} ms", end);
	}

	private List<FeatureNameValueDTO> transformFeatures(List<NameValueDTO> returnedNamesAndValues)
	{
		List<FeatureNameValueDTO> features = new LinkedList<>();
		returnedNamesAndValues.forEach(feature -> {
			features.add(new FeatureNameValueDTO(feature.getName(), feature.getValue()));
		});
		return features;
	}

	private void returnEmptyMap(Collection<String> parts, Map<String, RiskDTO> riskConcurrentMap)
	{
		parts.forEach(p -> {
			riskConcurrentMap.put(p, new RiskDTO());
		});
	}

	private void appendNotFoundParts(Map<String, RiskDTO> riskConcurrentMap, Collection<String> parts)
	{
		parts.forEach(p -> {
			riskConcurrentMap.computeIfAbsent(p, k -> new RiskDTO());
		});
	}

	private RiskExportRequest getRequestBody(Collection<String> parts)
	{
		RiskExportRequest requestBody = new RiskExporter.RiskExportRequest();
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

	public class RiskExportRequest
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
