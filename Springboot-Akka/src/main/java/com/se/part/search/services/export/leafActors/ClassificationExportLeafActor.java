package com.se.part.search.services.export.leafActors;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.export.ParametricFeatureDTO;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.BOMExporterActor;

import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.CLASSIFICATION_STRATEGY)
public class ClassificationExportLeafActor extends BOMExporterActor
{

	@Value("#{environment['export.classificationSection.url']}")
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
			HttpHeaders headers = getRequestHeaders();
			ClassificationeExportRequest requestBody = getRequestBody(parts);
			HttpEntity entity = new HttpEntity<>(requestBody, headers);
			URI uri = helperService.getTargetEndpoint(url, null);
			ResponseEntity<String> response = httpClient.getStringResponsePOST(HttpMethod.POST, uri, entity, restTemplate);
			if(response.getStatusCode() == HttpStatus.OK)
			{
				String result = response.getBody();
				if(result != null && !result.isEmpty())
				{
					JSONObject jsonResponse = getJSONResponse(result);
					Map<String, List<FeatureNameValueDTO>> featureNameValueMap = getClassificationFeatureNameValue(jsonResponse);
					featureNameValueMap = filterConfiguredClassificationFeatures(featureNameValueMap, configuredFeaturesName);
					appendNotFoundPartsV2(featureNameValueMap, parts);
					sendMessageToCategoryManager(getSender(), new ClassificationExportLeafActor.ClassificationExportResultMessage(Constants.CLASSIFICATION_STRATEGY, featureNameValueMap, requestID), getSelf());
				}
				else
				{
					Map<String, List<FeatureNameValueDTO>> featureNameValueMap = new HashMap<>();
					returnEmptyMapV2(parts, featureNameValueMap);
					sendMessageToCategoryManager(getSender(), new ClassificationExportLeafActor.ClassificationExportResultMessage(Constants.CLASSIFICATION_STRATEGY, featureNameValueMap, requestID), getSelf());
				}
			}
		}
		catch(Exception e)
		{
			log.error(e, "Error during exporting Classification");
			Map<String, List<FeatureNameValueDTO>> featureNameValueMap = new HashMap<>();
			returnEmptyMapV2(parts, featureNameValueMap);
			sendMessageToCategoryManager(getSender(), new ClassificationExportLeafActor.ClassificationExportResultMessage(Constants.CLASSIFICATION_STRATEGY, featureNameValueMap, requestID), getSelf());
		}
		long end = System.currentTimeMillis() - start;
		logger.info("Classification Export has been finished in:{} ms", end);
	}

	private Map<String, List<FeatureNameValueDTO>> filterConfiguredClassificationFeatures(Map<String, List<FeatureNameValueDTO>> featureNameValueMap, Map<String, String> configuredFeaturesName)
	{
		Map<String, List<FeatureNameValueDTO>> filteredResult = new HashMap<>();
		featureNameValueMap.entrySet().forEach(e -> {
			List<FeatureNameValueDTO> filteredFeatures = e.getValue().stream().filter(f -> configuredFeaturesName.get(f.getFeatureName()) != null).collect(Collectors.toList());
			filteredResult.put(e.getKey(), filteredFeatures);
		});
		return filteredResult;
	}

	private Map<String, List<FeatureNameValueDTO>> getClassificationFeatureNameValue(JSONObject jsonResponse) throws JSONException
	{
		Map<String, List<FeatureNameValueDTO>> features = new HashMap<>();
		Iterator keys = jsonResponse.keys();
		while(keys.hasNext())
		{
			Object comId = keys.next();
			features.put(comId.toString(), new LinkedList<>());
			JSONArray valueArray = (JSONArray) jsonResponse.get(comId.toString());
			for(int i = 0; i < valueArray.length(); i++)
			{
				JSONObject element = (JSONObject) valueArray.get(i);
				FeatureNameValueDTO feature = new FeatureNameValueDTO((String) element.get("clasName"), element.get("classValue"));
				features.computeIfPresent(comId.toString(), (k, v) -> {
					v.add(feature);
					return v;
				});
			}
		}
		return features;
	}

	private ClassificationeExportRequest getRequestBody(Collection<String> parts)
	{
		ClassificationeExportRequest requestBody = new ClassificationExportLeafActor.ClassificationeExportRequest();
		requestBody.setComIds(parts);
		return requestBody;
	}

	public ClassificationExportLeafActor()
	{
	}

	public static Props props()
	{
		return Props.create(ClassificationExportLeafActor.class, () -> new ClassificationExportLeafActor());
	}

	@Override
	public Receive createReceive()
	{
		return responseToMessages();
	}

	private Receive responseToMessages()
	{
		return receiveBuilder().match(BOMExporterActor.ExportMessage.class, r -> {
			exportResult(r);
		}).build();
	}

	public class ClassificationeExportRequest
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

	public static class ClassificationExportResultMessage
	{
		private final String categoryName;
		private final Map<String, List<FeatureNameValueDTO>> response;
		private final String requestId;

		public ClassificationExportResultMessage(String categoryName, Map<String, List<FeatureNameValueDTO>> response, String requestId)
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
