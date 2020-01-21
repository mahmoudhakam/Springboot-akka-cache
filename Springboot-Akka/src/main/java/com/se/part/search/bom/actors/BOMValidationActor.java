package com.se.part.search.bom.actors;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.se.part.search.bom.messages.BOMRequestType;
import com.se.part.search.bom.messages.PartValidationMessage;
import com.se.part.search.bom.messages.ValidationResponseMessage;
import com.se.part.search.dto.bom.BOMRow;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.Router;
import scala.concurrent.duration.FiniteDuration;

public class BOMValidationActor extends AbstractActor
{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final class CollectionTimeout
	{
	}

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	final long requestId;
	ActorRef requester;
	final int partsPerActor;
	final int actorsPercategory;
	Cancellable queryTimeoutTimer;
	private final Map<String, Router> routers;

	public BOMValidationActor(long requestId, FiniteDuration timeout, int partsPerActor, int actorsPercategory, Map<String, Router> routers)
	{

		this.requestId = requestId;
		this.partsPerActor = partsPerActor;
		queryTimeoutTimer = getContext().getSystem().scheduler().scheduleOnce(timeout, getSelf(), new CollectionTimeout(),
				getContext().getDispatcher(), getSelf());
		this.actorsPercategory = actorsPercategory;
		this.routers = routers;
	}

	public static Props props(long requestId, FiniteDuration timeout, int partsPerActor, int actorsPercategory, Map<String, Router> routers)
	{
		return Props.create(BOMValidationActor.class,
				() -> new BOMValidationActor(requestId, timeout, actorsPercategory, actorsPercategory, routers));
	}

	@Override
	public void preStart()
	{
		log.debug("started validation");
		// System.out.println("started main validator");
	}

	private void setupChildren(List<BOMRow> partsList)
	{
		Router router = routers.get(BOMRequestType.Exact.stepName());
		for(int i = 0; i < partsList.size(); i += partsPerActor)
		{
			List<BOMRow> subList = partsList.subList(i, Math.min(i + partsPerActor, partsList.size()));
			router.route(new BOMValidator.ValidationMessage(subList, false), self());
		}

	}

	@Override
	public void postStop()
	{
		queryTimeoutTimer.cancel();
	}

	public void receivedResponse(ActorRef deviceActor, PartValidationMessage response, Set<BOMRow> stillWaiting,
								 Map<BOMRow, PartValidationMessage> repliesSoFar, Collection<BOMRow> part)
	{
		// logger.info("actor {} Finished validation in: {}", deviceActor.path(), new Date());
		Set<BOMRow> newStillWaiting = new HashSet<>(stillWaiting);
		newStillWaiting.removeAll(part);

		Map<BOMRow, PartValidationMessage> newRepliesSoFar = new HashMap<>(repliesSoFar);
		// newRepliesSoFar.put(part, response);
		if(newStillWaiting.isEmpty())
		{

			requester.tell(new BOMValidationActor.RespondAllParts(requestId, newRepliesSoFar), self());
		}
		else
		{
			getContext().become(waitingForReplies(newRepliesSoFar, newStillWaiting));
		}
	}

	public void receivedNotFoundResponse(BOMRequestType nextStep, Set<BOMRow> stillWaiting, Map<BOMRow, PartValidationMessage> repliesSoFar,
										 Collection<BOMRow> part, boolean ignoreMan)

	{

		Set<BOMRow> newStillWaiting = new HashSet<>(stillWaiting);
		Map<BOMRow, PartValidationMessage> newRepliesSoFar = new HashMap<>(repliesSoFar);
		routers.get(nextStep.stepName()).route(new BOMValidator.ValidationMessage(part, ignoreMan), getSelf());
		getContext().become(waitingForReplies(newRepliesSoFar, newStillWaiting));

	}

	@Override
	public Receive createReceive()
	{
		return waitingForReplies(new HashMap<>(), new HashSet<>());
	}

	public Receive waitingForReplies(Map<BOMRow, PartValidationMessage> newRepliesSoFar, Set<BOMRow> stillWaiting)
	{
		return receiveBuilder().match(ValidationResponseMessage.RespondPartFound.class, r -> {
			ActorRef deviceActor = getSender();
			PartValidationMessage response = r.getResponse();
			Collection<BOMRow> part = r.getPartsResponse();
			receivedResponse(deviceActor, response, stillWaiting, newRepliesSoFar, part);

		}).match(ValidationResponseMessage.RespondPartNotFound.class, r -> {
			BOMRequestType nextStep = r.getNextStep();
			Collection<BOMRow> part = r.getPartsResponse();
			receivedNotFoundResponse(nextStep, stillWaiting, newRepliesSoFar, part, r.isIgnoreManufacturer());

		}).match(RequestAllParts.class, request -> {
			System.out.println("actor -" + getSelf().path() + " recived message at" + new Date());
			this.requester = request.requester;
			getContext().become(waitingForReplies(newRepliesSoFar, new HashSet<>(request.getRequestedPart())));
			setupChildren(request.getRequestedPart());
		}).build();
	}

	public static final class RespondAllParts
	{
		final long partId;
		final Map<BOMRow, PartValidationMessage> partsResponse;

		public RespondAllParts(long partId, Map<BOMRow, PartValidationMessage> responses)
		{
			this.partId = partId;
			this.partsResponse = responses;
		}

		@Override
		public String toString()
		{
			return "RespondAllParts [partId=" + partId + ", partsResponse=" + partsResponse.size() + "]";
		}

		public Map<BOMRow, PartValidationMessage> getPartsResponse()
		{
			return partsResponse;
		}

	}

	public static final class RequestAllParts
	{
		final List<BOMRow> requestedPart;
		final ActorRef requester;

		public RequestAllParts(List<BOMRow> requestedPart, ActorRef requester)
		{
			super();
			this.requestedPart = requestedPart;
			this.requester = requester;
		}

		public List<BOMRow> getRequestedPart()
		{
			return requestedPart;
		}

		public ActorRef getRequester()
		{
			return requester;
		}

	}

}
