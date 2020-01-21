package com.se.part.search.services.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.export.NameValueDTO;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.keywordSearch.JsonHandler;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

@Service
public abstract class BOMExporterActor extends AbstractActor
{
	@Autowired
	protected PartSearchHelperService helperService;
	@Autowired
	protected RestTemplate restTemplate;
	protected ExportHttpClient<String> httpClient = new ExportHttpClient<>();
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	protected JsonHandler jsonHandler = new JsonHandler<>();

	public static final class ExportMessage
	{
		private final Collection<String> reqeustedParts;
		private final String requestID;
		private final Map<String, String> configuredFeaturesName;

		public ExportMessage(Collection<String> reqeustedParts, String requestID, Map<String, String> configuredFeaturesName)
		{
			this.reqeustedParts = reqeustedParts;
			this.requestID = requestID;
			this.configuredFeaturesName = configuredFeaturesName;
		}

		public Collection<String> getReqeustedPart()
		{
			return reqeustedParts;
		}

		public String getRequestID()
		{
			return requestID;
		}

		public Map<String, String> getConfiguredFeaturesName()
		{
			return configuredFeaturesName;
		}

	}

	public abstract void exportResult(ExportMessage message);

	public HttpHeaders getRequestHeaders()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	protected void sendMessageToCategoryManager(ActorRef reciever, Object message, ActorRef sender)
	{
		reciever.tell(message, sender);
	}

	protected JSONObject getJSONResponse(String response) throws JSONException
	{
		JSONObject json = new JSONObject(response);
		if(json != null)
		{
			return json;
		}
		return null;
	}

	protected Map<String, List<FeatureNameValueDTO>> getFeatureNameFeatureValueMap(JSONObject json, String categoryName, Map<String, String> configuredFeaturesName) throws JSONException
	{
		long start = System.currentTimeMillis();
		Map<String, List<FeatureNameValueDTO>> featureNameValueMap = new HashMap<>();
		String emptyRoot = "";
		String emptyComId = "";
		boolean byPassFirstElement = true;
		jsonIterate(emptyRoot, json, json.keys(), featureNameValueMap, configuredFeaturesName, byPassFirstElement, emptyComId);
		logger.info("Parsing json :{} takes about:{} found features:{}", categoryName, (System.currentTimeMillis() - start), featureNameValueMap);
		return featureNameValueMap;
	}

	private void jsonIterate(String parentKey, JSONObject result, Iterator<Object> keys, Map<String, List<FeatureNameValueDTO>> featureNameValueMap, Map<String, String> configuredFeaturesName, boolean byPassFirstElement, String comId)
			throws JSONException
	{
		String delimeter = "";
		while(keys.hasNext())
		{
			Object key = keys.next();
			if(byPassFirstElement)
			{
				comId = key.toString();
				featureNameValueMap.put(comId, new ArrayList<>());
			}
			String childParentKey = parentKey;
			if(!byPassFirstElement)
			{
				if(!childParentKey.isEmpty())
				{
					delimeter = ".";
				}
				childParentKey = childParentKey + delimeter + key.toString();
			}
			Object value = result.get(key.toString());
			if(value instanceof JSONObject)
			{
				JSONObject jsonObject = (JSONObject) value;
				jsonIterate(childParentKey, jsonObject, jsonObject.keys(), featureNameValueMap, configuredFeaturesName, false, comId);
			}
			else if(value instanceof JSONArray)
			{

				JSONArray arr = (JSONArray) value;
				if(arr != null && !arr.isNull(0))
				{
					JSONObject jsonObject = (JSONObject) arr.get(0);
					jsonIterate(childParentKey, jsonObject, jsonObject.keys(), featureNameValueMap, configuredFeaturesName, false, comId);
				}
			}
			boolean isKeyConfigured = configuredFeaturesName.get(childParentKey) != null;
			if(isKeyConfigured)
			{
				String featureName = configuredFeaturesName.get(childParentKey);
				String featureValue = value == null ? "" : value.toString();
				FeatureNameValueDTO feature = new FeatureNameValueDTO(featureName, featureValue);
				featureNameValueMap.computeIfPresent(comId, (k, v) -> {
					v.add(feature);
					return v;
				});
			}
		}
	}

	protected void returnEmptyMapV2(Collection<String> parts, Map<String, List<FeatureNameValueDTO>> featureNameValueMap)
	{
		parts.forEach(p -> {
			featureNameValueMap.put(p, new ArrayList<>());
		});
	}

	protected void appendNotFoundPartsV2(Map<String, List<FeatureNameValueDTO>> riskConcurrentMap, Collection<String> parts)
	{
		parts.forEach(p -> {
			riskConcurrentMap.computeIfAbsent(p, k -> new ArrayList<>());
		});
	}
	
	protected Map<String, List<NameValueDTO>> filterConfiguredFeatures(Map<String, List<NameValueDTO>> result, Map<String, String> configuredFeaturesName)
	{
		Map<String, List<NameValueDTO>> filteredResult = new HashMap<>();
		result.entrySet().forEach(e -> {
			List<NameValueDTO> filteredList = e.getValue().stream().filter(f -> configuredFeaturesName.get(f.getName()) != null).collect(Collectors.toList());
			filteredResult.put(e.getKey(), filteredList);
		});
		return filteredResult;
	}
	
	public String getSafeString(String str)
	{
		if(str == null || str.isEmpty())
		{
			return "";
		}
		return str;
	}
}
