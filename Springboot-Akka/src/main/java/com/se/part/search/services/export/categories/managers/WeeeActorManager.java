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
import com.se.part.search.dto.export.WeeeDTO;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.BOMExporterActor;
import com.se.part.search.services.export.leafActors.WeeeExportLeafActor;
import com.se.part.search.services.export.leafActors.WeeeExportLeafActor.WeeeExportResultMessage;

import akka.actor.ActorRef;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component(Constants.WEEE_STRATEGY + Constants.ACTOR_MANAGER_POSTFIX)
public class WeeeActorManager extends CategoriesManagerActor
{

	private Map<String, String> runningLeafActors = null;
	private String managerRequestID = "";
	private ActorRef requester;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PartSearchHelperService helperService;

	public WeeeActorManager()
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
			// logger.info("Weee Running leaf actors:{}", runningLeafActors);
			message.getCatRouter().route(new BOMExporterActor.ExportMessage(actorList, requestID, message.getConfiguredFeaturesName()), self());
		}
		logger.info("Weee Manager Sends message to leaf actors:{}", runningLeafActors);
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
		}).match(WeeeExportLeafActor.WeeeExportResultMessage.class, r -> {
			try
			{
				receiveWeeeLeafActorMessage(r, finalResult);
			}
			catch(Exception e)
			{
				logger.error("Error during receiveing message WeeeActorManager", e);
				Map<String, List<NameValueDTO>> emptyMap = new HashMap<>();
				sendMessageToBOMActorManager(this.requester, new ExportResultMessage.ExportResponseMessage(Constants.WEEE_STRATEGY, emptyMap, this.managerRequestID), getSelf());
			}
		}).build();
	}

	private void receiveWeeeLeafActorMessage(WeeeExportResultMessage r, Map<String, List<FeatureNameValueDTO>> finalResult)
	{
		String requestID = r.getRequestId();
		logger.info("Weee Manager receieve message from leaf with requestID:{}", requestID);
		markActorAsFinished(requestID, runningLeafActors);
		Map<String, List<FeatureNameValueDTO>> freshResult = r.getResponse();
		finalResult = Stream.concat(freshResult.entrySet().stream(), finalResult.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));
		if(isAllActorsFinishWork(runningLeafActors))
		{
			sendMessageToBOMActorManager(this.requester, new ExportResultMessage.ExportResponseMessage(Constants.WEEE_STRATEGY, finalResult, this.managerRequestID), getSelf());
		}
		else
		{
			getContext().become(respondToMessage(finalResult));
		}
	}
}
