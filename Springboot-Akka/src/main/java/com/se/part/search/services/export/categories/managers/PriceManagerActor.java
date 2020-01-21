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
import com.se.part.search.dto.export.NameValueDTO;
import com.se.part.search.dto.export.price.PriceDTO;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.BOMExporterActor;
import com.se.part.search.services.export.leafActors.PriceExportLeafActor;
import com.se.part.search.services.export.leafActors.PriceExportLeafActor.PriceExportResultMessage;

import akka.actor.ActorRef;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.PRICE_STRATEGY + Constants.ACTOR_MANAGER_POSTFIX)
public class PriceManagerActor extends CategoriesManagerActor
{

	private Map<String, String> runningLeafActors = null;
	private String managerRequestID = "";
	private ActorRef requester;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PartSearchHelperService helperService;

	public PriceManagerActor()
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
			message.getCatRouter().route(new BOMExporterActor.ExportMessage(actorList, requestID, message.getConfiguredFeaturesName()), self());
		}
		logger.info("Price Manager Sends message to leaf actors:{}", runningLeafActors);
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
		}).match(PriceExportLeafActor.PriceExportResultMessage.class, r -> {
			try
			{
				receivePriceLeafActorMessage(r, finalResult);
			}
			catch(Exception e)
			{
				logger.error("Error during receiveing message PriceActorManager", e);
				Map<String, List<NameValueDTO>> emptyMap = new HashMap<>();
				sendMessageToBOMActorManager(this.requester, new ExportResultMessage.ExportResponseMessage(Constants.PRICE_STRATEGY, emptyMap, this.managerRequestID), getSelf());
			}
		}).build();
	}

	private void receivePriceLeafActorMessage(PriceExportResultMessage r, Map<String, List<FeatureNameValueDTO>> finalResult)
	{
		try
		{
			String requestID = r.getRequestId();
			logger.info("Price Manager receieve message from leaf with requestID:{}", requestID);
			markActorAsFinished(requestID, runningLeafActors);
			Map<String, List<FeatureNameValueDTO>> freshResult = r.getResponse();
			finalResult = Stream.concat(freshResult.entrySet().stream(), finalResult.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));
			if(isAllActorsFinishWork(runningLeafActors))
			{
				sendMessageToBOMActorManager(this.requester, new ExportResultMessage.ExportResponseMessage(Constants.PRICE_STRATEGY, finalResult, this.managerRequestID), getSelf());
			}
			else
			{
				getContext().become(respondToMessage(finalResult));
			}
		}
		catch(Exception e)
		{
			logger.error("error during receiving message Price actor manager", e);
			Map<String, List<FeatureNameValueDTO>> resultMap = new HashMap<>();
			sendMessageToBOMActorManager(this.requester, new ExportResultMessage.ExportResponseMessage(Constants.COOS_STRATEGY, resultMap, this.managerRequestID), getSelf());
		}
	}

	private Map<String, List<FeatureNameValueDTO>> convertResultToFeatureNameValue(Map<String, List<PriceDTO>> finalResult)
	{
		long start = System.currentTimeMillis();
		Map<String, List<FeatureNameValueDTO>> result = new LinkedHashMap<>();
		finalResult.entrySet().forEach(e -> {
			List<FeatureNameValueDTO> features = new java.util.LinkedList<>();
			List<PriceDTO> priceDTO = e.getValue();
			if(priceDTO != null && !priceDTO.isEmpty())
			{
				PriceDTO price = priceDTO.get(0);
				features.add(new FeatureNameValueDTO("MaxLeadTime", getSafeString(price.getMaxLeadTime())));
				features.add(new FeatureNameValueDTO("MinLeadTime", getSafeString(price.getMinLeadTime())));
				features.add(new FeatureNameValueDTO("MinPrice", getSafeString(price.getMinPrice())));
				features.add(new FeatureNameValueDTO("AVGPrice", getSafeString(price.getAVGPrice())));
				features.add(new FeatureNameValueDTO("LastUpdatedDate", getSafeString(price.getLastUpdatedDate())));
			}
			result.put(e.getKey(), features);
		});
		logger.info("Parsing of Price json takes about:{}", (System.currentTimeMillis() - start));
		return result;
	}

}
