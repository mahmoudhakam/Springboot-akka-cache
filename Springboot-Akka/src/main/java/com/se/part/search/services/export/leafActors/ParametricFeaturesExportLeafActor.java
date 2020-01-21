package com.se.part.search.services.export.leafActors;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.export.NameValueDTO;
import com.se.part.search.dto.export.ParametricFeatureDTO;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.export.BOMExporterActor;
import com.se.part.search.services.export.ExportHttpClient;

import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.collection.mutable.LinkedList;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.PARAMETRIC_STRATEGY)
public class ParametricFeaturesExportLeafActor extends BOMExporterActor
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("#{environment['export.parametricSection.url']}")
	private String url;
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void exportResult(ExportMessage message)
	{
		long start = System.currentTimeMillis();
		Collection<String> parts = message.getReqeustedPart();
		String requestID = message.getRequestID();
		try
		{
			ExportHttpClient<Map<String, List<ParametricFeatureDTO>>> httpClient = new ExportHttpClient<>();
			Map<String, String> configuredFeaturesName = message.getConfiguredFeaturesName();
			HttpHeaders headers = getRequestHeaders();
			ParametricFeaturesExportRequest requestBody = getRequestBody(parts);
			HttpEntity entity = new HttpEntity<>(requestBody, headers);
			URI uri = helperService.getTargetEndpoint(url, null);
			ResponseEntity<Map<String, List<ParametricFeatureDTO>>> response = httpClient.callRestEndpoint(HttpMethod.POST, uri, entity, restTemplate, new ParameterizedTypeReference<Map<String, List<ParametricFeatureDTO>>>() {
			});
			if(response.getStatusCode() == HttpStatus.OK)
			{
				Map<String, List<ParametricFeatureDTO>> result = response.getBody();
				if(result != null && !result.isEmpty())
				{
					Map<String, List<FeatureNameValueDTO>> featureNameValueMap = filterConfiguredParametricFeatures(result, configuredFeaturesName);
					sendMessageToCategoryManager(getSender(), new ParametricFeaturesExportLeafActor.ParametricExportResultMessage(Constants.PARAMETRIC_STRATEGY, featureNameValueMap, requestID), getSelf());
				}
				else
				{
					Map<String, List<FeatureNameValueDTO>> featureNameValueMap = new HashMap<>();
					returnEmptyMapV2(parts, featureNameValueMap);
					sendMessageToCategoryManager(getSender(), new ParametricFeaturesExportLeafActor.ParametricExportResultMessage(Constants.PARAMETRIC_STRATEGY, featureNameValueMap, requestID), getSelf());
				}
			}
		}
		catch(Exception e)
		{
			log.error(e, "Error during getting parametric features ");
			Map<String, List<FeatureNameValueDTO>> featureNameValueMap = new HashMap<>();
			returnEmptyMapV2(parts, featureNameValueMap);
			sendMessageToCategoryManager(getSender(), new ParametricFeaturesExportLeafActor.ParametricExportResultMessage(Constants.PARAMETRIC_STRATEGY, featureNameValueMap, requestID), getSelf());
		}
		long end = System.currentTimeMillis() - start;
		logger.info("Parametric export service finished in about:{} ms", end);
	}

	private Map<String, List<FeatureNameValueDTO>> filterConfiguredParametricFeatures(Map<String, List<ParametricFeatureDTO>> result, Map<String, String> configuredFeaturesName)
	{
		Map<String, List<FeatureNameValueDTO>> filteredResult = new HashMap<>();
		result.entrySet().forEach(e -> {
			List<ParametricFeatureDTO> filteredFeatures = e.getValue().stream().filter(f -> configuredFeaturesName.get(f.getFeatureName()) != null).collect(Collectors.toList());
			List<FeatureNameValueDTO> convertedFeatures = new ArrayList<>();
			filteredFeatures.forEach(f -> {
				convertedFeatures.add(new FeatureNameValueDTO(f.getFeatureName(), f.getFeatureValue() + " (" + f.getFeatureUnit() + ")"));
			});
			filteredResult.put(e.getKey(), convertedFeatures);
		});
		return filteredResult;
	}

	private Map<String, List<ParametricFeatureDTO>> getEmptyMap(Collection<String> parts)
	{
		Map<String, List<ParametricFeatureDTO>> result = new HashMap<>();
		parts.forEach(p -> {
			result.put(p, new ArrayList<>());
		});
		return result;
	}

	public static Props props()
	{
		return Props.create(ParametricFeaturesExportLeafActor.class, () -> new ParametricFeaturesExportLeafActor());
	}

	@Override
	public Receive createReceive()
	{
		return receiveBuilder().match(BOMExporterActor.ExportMessage.class, r -> exportResult(r)).build();
	}

	private ParametricFeaturesExportRequest getRequestBody(Collection<String> parts)
	{
		ParametricFeaturesExportRequest requestBody = new ParametricFeaturesExportLeafActor.ParametricFeaturesExportRequest();
		requestBody.setComIds(parts);
		return requestBody;
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

	public static class ParametricExportResultMessage
	{
		private final String categoryName;
		private final Map<String, List<FeatureNameValueDTO>> response;
		private final String requestId;

		public ParametricExportResultMessage(String categoryName, Map<String, List<FeatureNameValueDTO>> response, String requestId)
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

		public Map<String, List<FeatureNameValueDTO>> getResponse()
		{
			return response;
		}

		public String getRequestId()
		{
			return requestId;
		}

	}
}
