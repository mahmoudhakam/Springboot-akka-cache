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
@Component(Constants.PACKAGE_STRATEGY)
public class PackageExportLeafActor extends BOMExporterActor
{
	@Value("#{environment['export.packageSection.url']}")
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
			Map<String, String> configuredFeaturesName = message.getConfiguredFeaturesName();
			ExportHttpClient<Map<String, List<NameValueDTO>>> httpClient = new ExportHttpClient<>();
			HttpHeaders headers = getRequestHeaders();
			PackageExportRequest requestBody = getRequestBody(parts);
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
				}
				else
				{
					result = getEmptyMap(parts);
				}
				sendMessageToCategoryManager(getSender(), new PackageExportLeafActor.PacakageExportResultMessage(Constants.PACKAGE_STRATEGY, result, requestID), getSelf());
			}
		}
		catch(Exception e)
		{
			log.error(e, "Error during exporting package");
			Map<String, List<NameValueDTO>> result = getEmptyMap(parts);
			sendMessageToCategoryManager(getSender(), new PackageExportLeafActor.PacakageExportResultMessage(Constants.PACKAGE_STRATEGY, result, requestID), getSelf());
		}
		long end = System.currentTimeMillis() - start;
		logger.info("Package Export has been finished in:{} ms", end);
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

	private PackageExportRequest getRequestBody(Collection<String> parts)
	{
		PackageExportRequest requestBody = new PackageExportLeafActor.PackageExportRequest();
		requestBody.setComIds(parts);
		return requestBody;
	}

	public PackageExportLeafActor()
	{
	}

	// public static Props props()
	// {
	// return Props.create(PackageExportLeafActor.class, () -> new PackageExportLeafActor());
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

	public class PackageExportRequest
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

	public static class PacakageExportResultMessage
	{
		private final String categoryName;
		private final Map<String, List<NameValueDTO>> response;
		private final String requestId;

		public PacakageExportResultMessage(String categoryName, Map<String, List<NameValueDTO>> response, String requestId)
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
