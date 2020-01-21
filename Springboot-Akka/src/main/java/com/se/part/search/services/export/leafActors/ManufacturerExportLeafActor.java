package com.se.part.search.services.export.leafActors;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.se.part.search.dto.export.NameValueDTO;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.BOMExporterActor;
import com.se.part.search.services.export.ExportHttpClient;

import akka.event.Logging;
import akka.event.LoggingAdapter;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.MANUFACTURER_STRATEGY)
public class ManufacturerExportLeafActor extends BOMExporterActor
{
	@Value("#{environment['export.manufacturerSection.url']}")
	private String url;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PartSearchHelperService helperService;
	@Autowired
	private RestTemplate restTemplate;
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void exportResult(ExportMessage message)
	{
		long start = System.currentTimeMillis();
		Collection<String> parts = message.getReqeustedPart();
		String requestID = message.getRequestID();
		try
		{
			// Maximum Reflow Temperature (°C) ___ feature name with unit as a key
			Map<String, String> configuredFeaturesName = message.getConfiguredFeaturesName();
			ExportHttpClient<Map<String, List<NameValueDTO>>> httpClient = new ExportHttpClient<>();
			HttpHeaders headers = getRequestHeaders();
			ManufacturereExportRequest requestBody = getRequestBody(parts);
			HttpEntity entity = new HttpEntity<>(requestBody, headers);
			URI uri = helperService.getTargetEndpoint(url, null);
			ResponseEntity<Map<String, List<NameValueDTO>>> response = httpClient.callRestEndpoint(HttpMethod.POST, uri, entity, restTemplate, new ParameterizedTypeReference<Map<String, List<NameValueDTO>>>() {
			});
			if(response.getStatusCode() == HttpStatus.OK)
			{
				Map<String, List<NameValueDTO>> result = response.getBody();
				if(result != null && !result.isEmpty())
				{
					result = filterConfiguredFeatures(result, configuredFeaturesName);
					mergeManufacturerResult(result, parts);
					sendMessageToCategoryManager(getSender(), new ManufacturerExportLeafActor.ManufacturerExportResultMessage(Constants.MANUFACTURER_STRATEGY, result, requestID), getSelf());
				}
				else
				{
					result = getEmptyMap(parts);
					sendMessageToCategoryManager(getSender(), new ManufacturerExportLeafActor.ManufacturerExportResultMessage(Constants.MANUFACTURER_STRATEGY, result, requestID), getSelf());
				}
			}
		}
		catch(Exception e)
		{
			log.error(e, "Error during exporting manufacturer");
			Map<String, List<NameValueDTO>> result = getEmptyMap(parts);
			sendMessageToCategoryManager(getSender(), new ManufacturerExportLeafActor.ManufacturerExportResultMessage(Constants.MANUFACTURER_STRATEGY, result, requestID), getSelf());
		}
		long end = System.currentTimeMillis() - start;
		logger.info("Manufacturer Export has been finished in:{} ms", end);
	}

	private Map<String, List<NameValueDTO>> getEmptyMap(Collection<String> parts)
	{
		Map<String, List<NameValueDTO>> result = new HashMap<>();
		parts.forEach(p -> {
			result.put(p, new ArrayList<>());
		});
		return result;
	}

	private void mergeManufacturerResult(Map<String, List<NameValueDTO>> result, Collection<String> parts)
	{
		parts.forEach(p -> {
			result.computeIfAbsent(p, k -> new ArrayList<>());
		});
	}

	private ManufacturereExportRequest getRequestBody(Collection<String> parts)
	{
		ManufacturereExportRequest requestBody = new ManufacturerExportLeafActor.ManufacturereExportRequest();
		requestBody.setComIds(parts);
		return requestBody;
	}

	public HttpHeaders getRequestHeaders()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	public ManufacturerExportLeafActor()
	{
	}
	//
	// public static Props props()
	// {
	// return Props.create(ManufacturerExportLeafActor.class, () -> new ManufacturerExportLeafActor());
	// }

	@Override
	public Receive createReceive()
	{
		return responseToMessages();
	}

	private Receive responseToMessages()
	{
		return receiveBuilder().match(BOMExporterActor.ExportMessage.class, r -> {
			// logger.info("Received message {ExportComIDs} by [ManufacturerExportLeafActor]");
			exportResult(r);
		}).build();
	}

	public class ManufacturereExportRequest
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

	public static class ManufacturerExportResultMessage
	{
		private final String categoryName;
		private final Map<String, List<NameValueDTO>> response;
		private final String requestId;

		public ManufacturerExportResultMessage(String categoryName, Map<String, List<NameValueDTO>> response, String requestId)
		{
			super();
			this.categoryName = categoryName;
			this.response = response;
			this.requestId = requestId;
		}

		public String getCategoryName()
		{
			return categoryName;
		}

		public Map<String, List<NameValueDTO>> getResponse()
		{
			return response;
		}

		public String getRequestId()
		{
			return requestId;
		}

	}
}
