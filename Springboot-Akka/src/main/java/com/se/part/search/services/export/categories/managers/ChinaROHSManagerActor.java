package com.se.part.search.services.export.categories.managers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.se.part.search.dto.export.ExportResultMessage;
import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.export.environmental.ChinaROHSDTO;
import com.se.part.search.dto.export.environmental.Source;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.BOMExporterActor;
import com.se.part.search.services.export.leafActors.ChinaROHSExportLeafActor;
import com.se.part.search.services.export.leafActors.ChinaROHSExportLeafActor.ChinaROHSExportResultMessage;
import com.se.part.search.services.keywordSearch.JsonHandler;

import akka.actor.ActorRef;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.CHINAROHS_STRATEGY + Constants.ACTOR_MANAGER_POSTFIX)
public class ChinaROHSManagerActor extends CategoriesManagerActor
{
	private Map<String, String> runningLeafActors = null;
	private String managerRequestID = "";
	private ActorRef requester;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PartSearchHelperService helperService;

	public ChinaROHSManagerActor()
	{
		runningLeafActors = new HashMap<>();
	}

	@Override
	public void sendRequestToLeafActor(CategoryExportMessage message)
	{
		this.requester = message.getRequester();
		this.managerRequestID = message.getRequestID();
		for(int i = 0; i < message.getComIDs().size(); i += message.getPartsPerActor())
		{
			List<String> actorList = message.getComIDs().subList(i, Math.min(i + message.getPartsPerActor(), message.getComIDs().size()));
			String requestID = System.nanoTime() + "";
			runningLeafActors.put(requestID, getSelf().path().toString());
			// logger.info("ChinaROHS Running leaf actors:{}", runningLeafActors);
			message.getCatRouter().route(new BOMExporterActor.ExportMessage(actorList, requestID, message.getConfiguredFeaturesName()), self());
		}
		logger.info("ChinaROHS Manager Sends message to leaf actors:{}", runningLeafActors);
		getContext().become(respondToMessage(new LinkedHashMap<>()));
	}

	@Override
	public Receive createReceive()
	{
		return respondToMessage(new LinkedHashMap<>());
	}

	private Receive respondToMessage(Map<String, List<FeatureNameValueDTO>> finalResult)
	{
		return receiveBuilder().match(CategoryExportMessage.class, r -> {
			sendRequestToLeafActor(r);
		}).match(ChinaROHSExportLeafActor.ChinaROHSExportResultMessage.class, r -> {
			receiveChinaROHSLeafActorMessage(r, finalResult);
		}).build();
	}

	private void receiveChinaROHSLeafActorMessage(ChinaROHSExportResultMessage r, Map<String, List<FeatureNameValueDTO>> finalResult)
	{
		try
		{
			String requestID = r.getRequestId();
			logger.info("ChinaROHS Manager receieve message from leaf with requestID:{}", requestID);
			markActorAsFinished(requestID, runningLeafActors);
			Map<String, List<FeatureNameValueDTO>> freshResult = r.getResponse();
			finalResult = Stream.concat(freshResult.entrySet().stream(), finalResult.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));
			if(isAllActorsFinishWork(runningLeafActors))
			{
				sendMessageToBOMActorManager(this.requester, new ExportResultMessage.ExportResponseMessage(Constants.CHINAROHS_STRATEGY, finalResult, this.managerRequestID), getSelf());
			}
			else
			{
				getContext().become(respondToMessage(finalResult));
			}
		}
		catch(Exception e)
		{
			logger.error("Error during receiving message ChinaRohsCategoryManager", e);
			Map<String, List<FeatureNameValueDTO>> resultMap = new HashMap<>();
			sendMessageToBOMActorManager(this.requester, new ExportResultMessage.ExportResponseMessage(Constants.CHINAROHS_STRATEGY, resultMap, this.managerRequestID), getSelf());
		}

	}

	private Map<String, List<FeatureNameValueDTO>> convertResultToFeatureNameValue(Map<String, ChinaROHSDTO> finalResult)
	{
		long start = System.currentTimeMillis();
		Map<String, List<FeatureNameValueDTO>> result = new LinkedHashMap<>();
		finalResult.entrySet().forEach(e -> {
			List<FeatureNameValueDTO> features = new java.util.LinkedList<>();
			ChinaROHSDTO chinaRohs = e.getValue();
			if(chinaRohs != null & chinaRohs.getChinaId() > 0)
			{
				features.add(new FeatureNameValueDTO("ChinaId", getSafeString(chinaRohs.getChinaId() + "")));
				features.add(new FeatureNameValueDTO("CadmiumConc", getSafeString(chinaRohs.getCadmiumConc())));
				features.add(new FeatureNameValueDTO("CadmiumFlag", getSafeString(chinaRohs.getCadmiumFlag())));
				features.add(new FeatureNameValueDTO("ChromiumConc", getSafeString(chinaRohs.getChromiumConc())));
				features.add(new FeatureNameValueDTO("ChromiumFlag", getSafeString(chinaRohs.getChromiumFlag())));
				features.add(new FeatureNameValueDTO("LeadConc", getSafeString(chinaRohs.getLeadConc())));
				features.add(new FeatureNameValueDTO("LeadFlag", getSafeString(chinaRohs.getLeadFlag())));
				features.add(new FeatureNameValueDTO("MercuryConc", getSafeString(chinaRohs.getMercuryConc())));
				features.add(new FeatureNameValueDTO("MercuryFlag", getSafeString(chinaRohs.getMercuryFlag())));
				features.add(new FeatureNameValueDTO("PbbConc", getSafeString(chinaRohs.getPbbConc())));
				features.add(new FeatureNameValueDTO("PbbFlag", getSafeString(chinaRohs.getPbbFlag())));
				features.add(new FeatureNameValueDTO("PbdeConc", getSafeString(chinaRohs.getPbdeConc())));
				features.add(new FeatureNameValueDTO("PbdeFlag", getSafeString(chinaRohs.getPbdeFlag())));
				features.add(new FeatureNameValueDTO("Epup", getSafeString(chinaRohs.getEpup())));
				features.add(new FeatureNameValueDTO("LeadFree", getSafeString(chinaRohs.getLeadFree())));
				if(chinaRohs.getSources() != null && !chinaRohs.getSources().isEmpty())
				{
					Source source = chinaRohs.getSources().get(0);
					features.add(new FeatureNameValueDTO("URL", getSafeString(source.getUrl())));
					features.add(new FeatureNameValueDTO("PdfId", getSafeString(source.getPdfId() + "")));
					features.add(new FeatureNameValueDTO("SourceType", getSafeString(source.getSourceType())));
					features.add(new FeatureNameValueDTO("SourceTypeId", getSafeString(source.getSourceTypeId() + "")));

				}
			}
			result.put(e.getKey(), features);
		});
		logger.info("Parsing of ChinaROHS json takes about:{}", (System.currentTimeMillis() - start));
		return result;
	}

}
