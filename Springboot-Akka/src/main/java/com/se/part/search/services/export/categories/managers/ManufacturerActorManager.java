package com.se.part.search.services.export.categories.managers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.solr.common.util.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.se.part.search.dto.export.ExportResultMessage;
import com.se.part.search.dto.export.NameValueDTO;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.export.BOMExporterActor;
import com.se.part.search.services.export.leafActors.ManufacturerExportLeafActor;
import com.se.part.search.services.export.leafActors.ManufacturerExportLeafActor.ManufacturerExportResultMessage;

import akka.actor.ActorRef;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.MANUFACTURER_STRATEGY + Constants.ACTOR_MANAGER_POSTFIX)
public class ManufacturerActorManager extends CategoriesManagerActor
{
	private Map<String, String> runningLeafActors = null;
	private String managerRequestID = "";
	private ActorRef requester;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public ManufacturerActorManager()
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
			message.getCatRouter().route(new BOMExporterActor.ExportMessage(actorList, requestID,message.getConfiguredFeaturesName()), self());
		}
		logger.info("manufacturer Manager Sends message to leaf actors:{}", runningLeafActors);
		getContext().become(respondToMessage(new LinkedHashMap<>()));
	}

	@Override
	public Receive createReceive()
	{
		return respondToMessage(new LinkedHashMap<>());
	}

	private Receive respondToMessage(Map<String, List<NameValueDTO>> finalResult)
	{
		return receiveBuilder().match(CategoryExportMessage.class, r -> {
			sendRequestToLeafActor(r);
		}).match(ManufacturerExportLeafActor.ManufacturerExportResultMessage.class, r -> {
			try
			{
				receiveManufacturerLeafActorMessage(r, finalResult);
			}
			catch(Exception e)
			{
				logger.error("Error during receiveing message ManufacturerActorManager", e);
				Map<String, List<NameValueDTO>> emptyMap = new HashMap<>();
				sendMessageToBOMActorManager(this.requester, new ExportResultMessage.ExportResponseMessage(Constants.MANUFACTURER_STRATEGY, emptyMap, this.managerRequestID), getSelf());
			}
		}).build();
	}

	private void receiveManufacturerLeafActorMessage(ManufacturerExportResultMessage r, Map<String, List<NameValueDTO>> finalResult)
	{
		String requestID = r.getRequestId();
		logger.info("Manufacturer Manager receieve message from leaf with requestID:{}", requestID);
		markActorAsFinished(requestID, runningLeafActors);
		Map<String, List<NameValueDTO>> freshResult = r.getResponse();
		finalResult = Stream.concat(freshResult.entrySet().stream(), finalResult.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));
		if(isAllActorsFinishWork(runningLeafActors))
		{
			sendMessageToBOMActorManager(this.requester, new ExportResultMessage.ExportResponseMessage(Constants.MANUFACTURER_STRATEGY, finalResult, this.managerRequestID), getSelf());
		}
		else
		{
			getContext().become(respondToMessage(finalResult));
		}
	}

}
