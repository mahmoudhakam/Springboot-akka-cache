package com.se.part.search.services.export.leafActors;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.export.qualification.QualificationsDTO;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.BOMExporterActor;
import com.se.part.search.services.export.ExportHttpClient;

import akka.event.Logging;
import akka.event.LoggingAdapter;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.QUALIFICATIONS_STRATEGY)
public class QualificationsExportLeafActor extends BOMExporterActor
{
	@Value("#{environment['export.qualificationsSection.url']}")
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
			QualificationsExportRequest requestBody = getRequestBody(parts);
			HttpEntity entity = new HttpEntity<>(requestBody, headers);
			URI uri = helperService.getTargetEndpoint(url, null);
			ResponseEntity<String> response = httpClient.getStringResponsePOST(HttpMethod.POST, uri, entity, restTemplate);
			if(response.getStatusCode() == HttpStatus.OK)
			{
				String result = response.getBody();
				if(result != null && !result.isEmpty())
				{
					JSONObject jsonResponse = getJSONResponse(result);
					Map<String, List<FeatureNameValueDTO>> featureNameValueMap = getFeatureNameFeatureValueMap(jsonResponse, Constants.QUALIFICATIONS_STRATEGY, configuredFeaturesName);
					appendNotFoundPartsV2(featureNameValueMap, parts);
					sendMessageToCategoryManager(getSender(), new QualificationsExportLeafActor.QualificationsExportResultMessage(Constants.QUALIFICATIONS_STRATEGY, featureNameValueMap, requestID), getSelf());
				}
				else
				{
					Map<String, List<FeatureNameValueDTO>> featureNameValueMap = new HashMap<>();
					returnEmptyMapV2(parts, featureNameValueMap);
					sendMessageToCategoryManager(getSender(), new QualificationsExportLeafActor.QualificationsExportResultMessage(Constants.QUALIFICATIONS_STRATEGY, featureNameValueMap, requestID), getSelf());
				}
			}
		}
		catch(Exception e)
		{
			log.error(e, "Error during exporting Qualifications");
			Map<String, List<FeatureNameValueDTO>> featureNameValueMap = new HashMap<>();
			returnEmptyMapV2(parts, featureNameValueMap);
			getSender().tell(new QualificationsExportLeafActor.QualificationsExportResultMessage(Constants.QUALIFICATIONS_STRATEGY, featureNameValueMap, requestID), getSelf());
		}
		long end = System.currentTimeMillis() - start;
		logger.info("Qualifications Export has been finished in:{} ms", end);
	}

	private QualificationsExportRequest getRequestBody(Collection<String> parts)
	{
		QualificationsExportRequest requestBody = new QualificationsExportLeafActor.QualificationsExportRequest();
		requestBody.setComIds(parts);
		return requestBody;
	}

	public class QualificationsExportRequest
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

	public static class QualificationsExportResultMessage
	{
		private final String categoryName;
		private final Map<String, List<FeatureNameValueDTO>> response;
		private final String requestId;

		public QualificationsExportResultMessage(String categoryName, Map<String, List<FeatureNameValueDTO>> response, String requestId)
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
