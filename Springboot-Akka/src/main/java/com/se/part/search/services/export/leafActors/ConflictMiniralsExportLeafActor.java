package com.se.part.search.services.export.leafActors;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
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

import com.se.part.search.dto.ConflictMiniralsDTO;
import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.BOMExporterActor;
import com.se.part.search.services.export.ExportHttpClient;

import akka.event.Logging;
import akka.event.LoggingAdapter;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.CONFLICT_MINIRALS_STRATEGY)
public class ConflictMiniralsExportLeafActor extends BOMExporterActor
{
	@Value("#{environment['export.conflictMiniralsSection.url']}")
	private String url;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PartSearchHelperService helperService;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private SolrClient partsSummarySolrServer;
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void exportResult(ExportMessage message)
	{
		long start = System.currentTimeMillis();
		Collection<String> parts = message.getReqeustedPart();
		String requestID = message.getRequestID();
		try
		{
			Map<String, String> manVsPartMap = getManIdsByParts(parts);
			Map<String, String> configuredFeaturesName = message.getConfiguredFeaturesName();
			HttpHeaders headers = getRequestHeaders();
			ConflictMiniralsExportRequest requestBody = getRequestBody(manVsPartMap.keySet());
			HttpEntity entity = new HttpEntity<>(requestBody, headers);
			URI uri = helperService.getTargetEndpoint(url, null);
			ResponseEntity<String> response = httpClient.getStringResponsePOST(HttpMethod.POST, uri, entity, restTemplate);
			if(response.getStatusCode() == HttpStatus.OK)
			{
				String result = response.getBody();
				if(result != null && !result.isEmpty())
				{
					JSONObject jsonResponse = getJSONResponse(result);
					jsonResponse = convertManToPart(jsonResponse, manVsPartMap);
					Map<String, List<FeatureNameValueDTO>> featureNameValueMap = getFeatureNameFeatureValueMap(jsonResponse, Constants.CONFLICT_MINIRALS_STRATEGY, configuredFeaturesName);
					appendNotFoundPartsV2(featureNameValueMap, parts);
					sendMessageToCategoryManager(getSender(), new ConflictMiniralsExportLeafActor.ConflictMiniralsExportResultMessage(Constants.CONFLICT_MINIRALS_STRATEGY, featureNameValueMap, requestID), getSelf());
				}
				else
				{
					Map<String, List<FeatureNameValueDTO>> featureNameValueMap = new HashMap<>();
					returnEmptyMapV2(parts, featureNameValueMap);
					sendMessageToCategoryManager(getSender(), new ConflictMiniralsExportLeafActor.ConflictMiniralsExportResultMessage(Constants.CONFLICT_MINIRALS_STRATEGY, featureNameValueMap, requestID), getSelf());
				}
			}
		}
		catch(Exception e)
		{
			log.error(e, "Error during exporting ConflictMinirals");
			Map<String, List<FeatureNameValueDTO>> featureNameValueMap = new HashMap<>();
			returnEmptyMapV2(parts, featureNameValueMap);
			getSender().tell(new ConflictMiniralsExportLeafActor.ConflictMiniralsExportResultMessage(Constants.CONFLICT_MINIRALS_STRATEGY, featureNameValueMap, requestID), getSelf());
		}
		long end = System.currentTimeMillis() - start;
		logger.info("ConflictMinirals Export has been finished in:{} ms", end);
	}

	private JSONObject convertManToPart(JSONObject manResponse, Map<String, String> manVsPartMap) throws JSONException
	{
		JSONObject json = new JSONObject();
		Iterator keys = manResponse.keys();
		while(keys.hasNext())
		{
			Object manId = keys.next();
			String comId = manVsPartMap.get(manId.toString());
			json.put(comId, manResponse.get(manId.toString()));
		}
		return json;
	}

	private Map<String, String> getManIdsByParts(Collection<String> parts) throws SolrServerException, IOException
	{
		long start = System.currentTimeMillis();
		Map<String, String> partManMap = new HashMap<>();
		SolrQuery query = formateManIdsByComIds(parts);
		QueryResponse response = helperService.executeSorlQuery(query, partsSummarySolrServer);
		if(!response.getResults().isEmpty())
		{
			SolrDocumentList documents = response.getResults();
			documents.forEach(d -> {
				String manId = (String) d.getFieldValue("MAN_ID");
				String comId = (String) d.getFieldValue("COM_ID");
				if(manId != null && comId != null)
				{
					partManMap.put(manId, comId);
				}
			});
		}
		logger.info("Getting man ids for parts conflictminirals actor took :{} with query:{}", (System.currentTimeMillis() - start), query.toString());
		return partManMap;
	}

	private SolrQuery formateManIdsByComIds(Collection<String> parts)
	{
		SolrQuery query = new SolrQuery();
		StringBuilder sb = new StringBuilder();
		String delimeter = "";
		sb.append("COM_ID:(");
		for(String part : parts)
		{
			sb.append(delimeter);
			sb.append(part);
			delimeter = " ";
		}
		sb.append(")");
		query.add("q", sb.toString());
		query.add("fl", "MAN_ID,COM_ID");
		query.setRows(parts.size());
		return query;
	}

	private void returnEmptyMap(Collection<String> parts, Map<String, ConflictMiniralsDTO> ConflictMiniralsConcurrentMap)
	{
		parts.forEach(p -> {
			ConflictMiniralsConcurrentMap.put(p, new ConflictMiniralsDTO());
		});
	}

	private void appendNotFoundParts(Map<String, ConflictMiniralsDTO> ConflictMiniralsConcurrentMap, Collection<String> parts)
	{
		parts.forEach(p -> {
			ConflictMiniralsConcurrentMap.computeIfAbsent(p, k -> new ConflictMiniralsDTO());
		});
	}

	private ConflictMiniralsExportRequest getRequestBody(Collection<String> manIds)
	{
		ConflictMiniralsExportRequest requestBody = new ConflictMiniralsExportLeafActor.ConflictMiniralsExportRequest();
		requestBody.setManIds(manIds);
		return requestBody;
	}

	public class ConflictMiniralsExportRequest
	{
		private Collection<String> manIds;

		public Collection<String> getManIds()
		{
			return manIds;
		}

		public void setManIds(Collection<String> manIds)
		{
			this.manIds = manIds;
		}
	}

	@Override
	public Receive createReceive()
	{
		return responseToMessages();
	}

	private Receive responseToMessages()
	{
		return receiveBuilder().match(BOMExporterActor.ExportMessage.class, r -> {
			// logger.info("Received message {ExportComIDs} by [PCNExportLeafActor]");
			exportResult(r);
		}).build();
	}

	public static class ConflictMiniralsExportResultMessage
	{
		private final String categoryName;
		private final Map<String, List<FeatureNameValueDTO>> response;
		private final String requestId;

		public ConflictMiniralsExportResultMessage(String categoryName, Map<String, List<FeatureNameValueDTO>> response, String requestId)
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
