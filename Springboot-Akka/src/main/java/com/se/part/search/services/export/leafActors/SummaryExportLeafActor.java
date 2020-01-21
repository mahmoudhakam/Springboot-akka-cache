package com.se.part.search.services.export.leafActors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.PartSearchSolrDelegate;
import com.se.part.search.services.export.BOMExporterActor;

import akka.event.Logging;
import akka.event.LoggingAdapter;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.SUMMARY_STRATEGY)
public class SummaryExportLeafActor extends BOMExporterActor
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PartSearchHelperService helperService;
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	@Autowired
	PartSearchSolrDelegate partSearchSolrDelegate;

	@Override
	public void exportResult(ExportMessage message)
	{
		long start = System.currentTimeMillis();
		Collection<String> parts = message.getReqeustedPart();
		String requestID = message.getRequestID();
		try
		{
			Map<String, String> configuredFeaturesName = message.getConfiguredFeaturesName();
			SolrQuery query = formatePartsSummaryQuery(parts);
			QueryResponse response = helperService.executeSorlQuery(query, partSearchSolrDelegate.getPartsSummarySolrServer());
			SolrDocumentList documents = response.getResults();
			if(!documents.isEmpty())
			{
				Map<String, List<FeatureNameValueDTO>> features = getFeatureNameValueMap(documents, configuredFeaturesName);
				getSender().tell(new SummaryExportLeafActor.SummaryExportResultMessage(Constants.SUMMARY_STRATEGY, features, requestID), getSelf());
			}
			else
			{
				Map<String, List<FeatureNameValueDTO>> featureNameValueMap = new HashMap<>();
				returnEmptyMapV2(parts, featureNameValueMap);
				getSender().tell(new SummaryExportLeafActor.SummaryExportResultMessage(Constants.SUMMARY_STRATEGY, featureNameValueMap, requestID), getSelf());
			}
		}
		catch(Exception e)
		{
			log.error(e, "Error during exporting Summary");
			Map<String, List<FeatureNameValueDTO>> featureNameValueMap = new HashMap<>();
			returnEmptyMapV2(parts, featureNameValueMap);
			getSender().tell(new SummaryExportLeafActor.SummaryExportResultMessage(Constants.SUMMARY_STRATEGY, featureNameValueMap, requestID), getSelf());
		}
		long end = System.currentTimeMillis() - start;
		logger.info("Summary Export has been finished in:{} ms", end);
	}

	private Map<String, List<FeatureNameValueDTO>> getFeatureNameValueMap(SolrDocumentList documents, Map<String, String> configuredFeaturesName)
	{
		Map<String, List<FeatureNameValueDTO>> features = new HashMap<>();
		documents.forEach(d -> {
			String comId = getSafeString((String) d.getFieldValue("COM_ID"));
			features.put(comId, new ArrayList<>());
			d.getFieldNames().forEach(f -> {
				if(!f.equals("COM_ID"))
				{
					String fetName = configuredFeaturesName.get(f);
					if(fetName != null)
					{
						features.computeIfPresent(comId, (k, v) -> {
							v.add(new FeatureNameValueDTO(fetName, d.getFieldValue(f)));
							return v;
						});
					}
				}
			});
		});
		return features;
	}

	private SolrQuery formatePartsSummaryQuery(Collection<String> parts)
	{
		SolrQuery query = new SolrQuery();
		StringBuilder sb = new StringBuilder();
		sb.append("COM_ID:(");
		StringBuilder delimeter = new StringBuilder();
		parts.forEach(part -> {
			sb.append(delimeter);
			sb.append(part);
			delimeter.setLength(0);
			delimeter.append(" ");
		});
		sb.append(")");
		query.set("q", sb.toString());
		query.set("fl", "COM_ID,ROW_COM_DESC,PDF_URL_DS,LC_STATE,ROHS,ROHS_VERSION,CROSS_COUNT,PL_NAME,TAX_PATH,INTRODUCTION_DATE,FAMILY,LARGE_IMAGE,PART_RATING");
		query.setRows(parts.size());
		return query;
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

	public static class SummaryExportResultMessage
	{
		private final String categoryName;
		private final Map<String, List<FeatureNameValueDTO>> response;
		private final String requestId;

		public SummaryExportResultMessage(String categoryName, Map<String, List<FeatureNameValueDTO>> response, String requestId)
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
