package com.se.part.search.services.export.categories.managers;

import java.util.List;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.routing.Router;

public abstract class CategoriesManagerActor extends AbstractActor
{

	public static final class CategoryExportMessage
	{
		private final Router catRouter;
		private final ActorRef requester;
		private final String requestID;
		private final List<String> comIDs;
		private final int partsPerActor;
		private final Map<String, String> configuredFeaturesName;

		public CategoryExportMessage(Router catRouter, ActorRef requester, String requestID, List<String> comIDs, int partsPerActor, Map<String, String> configuredFeaturesName)
		{
			super();
			this.catRouter = catRouter;
			this.requester = requester;
			this.requestID = requestID;
			this.comIDs = comIDs;
			this.partsPerActor = partsPerActor;
			this.configuredFeaturesName = configuredFeaturesName;
		}

		public Router getCatRouter()
		{
			return catRouter;
		}

		public ActorRef getRequester()
		{
			return requester;
		}

		public String getRequestID()
		{
			return requestID;
		}

		public List<String> getComIDs()
		{
			return comIDs;
		}

		public int getPartsPerActor()
		{
			return partsPerActor;
		}
		
		public Map<String, String> getConfiguredFeaturesName()
		{
			return configuredFeaturesName;
		}

	}

	public abstract void sendRequestToLeafActor(CategoryExportMessage message);

	public boolean isAllActorsFinishWork(Map<String, String> runningLeafActors)
	{
		return runningLeafActors.isEmpty();
	}

	public void markActorAsFinished(String requestId2, Map<String, String> runningLeafActors)
	{
		runningLeafActors.remove(requestId2);
	}

	public void sendMessageToBOMActorManager(ActorRef bomActorManager, Object msg, ActorRef sender)
	{
		bomActorManager.tell(msg, sender);
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