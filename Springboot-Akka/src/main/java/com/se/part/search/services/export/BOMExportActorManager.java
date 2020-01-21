package com.se.part.search.services.export;

import static com.se.part.search.configuration.SpringExtension.SPRING_EXTENSION_PROVIDER;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.se.part.search.bom.actors.BOMValidationActor.CollectionTimeout;
import com.se.part.search.dto.export.CategoryActorEnum;
import com.se.part.search.dto.export.ExportResultMessage;
import com.se.part.search.dto.export.ExportResultMessage.ExportResponseMessage;
import com.se.part.search.dto.export.ExportRow;
import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.export.NameValueDTO;
import com.se.part.search.dto.export.PCNResponseDTO;
import com.se.part.search.dto.export.ParametricFeatureDTO;
import com.se.part.search.dto.export.classification.ClassificationData;
import com.se.part.search.dto.export.risk.RiskDTO;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.export.categories.managers.CategoriesManagerActor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import scala.concurrent.duration.FiniteDuration;

/**
 * This class represents BomExport Actors Manager
 */

public class BOMExportActorManager extends AbstractActor
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	final long requestId;
	final int partsPerActor;
	final int actorsPerCategory;
	Cancellable queryTimeoutTimer;
	private final Map<String, Router> routers;// holds all categories and the corresponding lead actors
	ActorRef requester;
	private final Map<String, Router> parentCatrouters;
	private Map<String, String> runningActors;
	FiniteDuration timeout;
	private Integer batchNumber;
	private Map<String, Map<String, String>> configuredFeaturesName;

	public BOMExportActorManager(long requestId, FiniteDuration timeout, int partsPerActor, int actorsPerCategory)
	{
		this.requestId = requestId;
		this.partsPerActor = partsPerActor;
		this.actorsPerCategory = actorsPerCategory;
		this.timeout = timeout;
		routers = createRoutersForLeafCategoryActors();
		runningActors = new HashMap<>();
		parentCatrouters = createParentRoutersForEachCategory();
	}

	private Map<String, Router> createRoutersForLeafCategoryActors()
	{
		long start = System.currentTimeMillis();
		ActorSystem actorSystem = getContext().getSystem();
		Map<String, Router> actorsRouters = new HashMap<>();
		for(CategoryActorEnum cat : CategoryActorEnum.values())
		{
			String catName = cat.getCategoryName();
			List<Routee> routees = new LinkedList<>();
			for(int i = 0; i < actorsPerCategory; i++)
			{
				ActorRef exporterLeafActor = actorSystem.actorOf(SPRING_EXTENSION_PROVIDER.get(actorSystem).props(catName));// convert spring beans
																															// to akka actors
				routees.add(new ActorRefRoutee(exporterLeafActor));
			}
			Router router = new Router(new RoundRobinRoutingLogic(), routees);
			actorsRouters.put(catName, router);
		}
		logger.info("Creating router takes about:{}", (System.currentTimeMillis() - start));
		return actorsRouters;
	}

	private Map<String, Router> createParentRoutersForEachCategory()
	{
		long start = System.currentTimeMillis();
		ActorSystem actorSystem = getContext().getSystem();
		Map<String, Router> parentActorsRouters = new HashMap<>();
		for(CategoryActorEnum cat : CategoryActorEnum.values())
		{
			String parentCategoryName = cat.getCategoryName() + Constants.ACTOR_MANAGER_POSTFIX;
			List<Routee> routees = new LinkedList<>();
			ActorRef exporterLeafActor = actorSystem.actorOf(SPRING_EXTENSION_PROVIDER.get(actorSystem).props(parentCategoryName));// convert spring
																																	// beans
			routees.add(new ActorRefRoutee(exporterLeafActor));
			Router router = new Router(new RoundRobinRoutingLogic(), routees);
			parentActorsRouters.put(parentCategoryName, router);
		}
		logger.info("Creating parent routers takes about:{}", (System.currentTimeMillis() - start));
		return parentActorsRouters;
	}

	@Override
	public Receive createReceive()
	{
		return responsdToMessages(new LinkedHashMap<>(), new LinkedHashMap<>());
	}

	private Receive responsdToMessages(Map<String, List<Object>> allResponses, Map<String, ExportRow> resV2)
	{
		return receiveBuilder().match(ExportAllParts.class, request -> {
			logger.info("BomExportManager now sending batch RequestAllParts to section Actor managers :{}", request.getCategories());
			this.configuredFeaturesName = request.getConfiguredFeaturesName();
			this.requester = request.getRequester();
			this.batchNumber = request.getBatchNumber();
			getContext().become(responsdToMessages(new LinkedHashMap<>(), new LinkedHashMap<>()));
			setUpCategoryManagerActors(request.getComIDs(), request.getCategories());
		}).match(ExportResultMessage.ExportResponseMessage.class, r -> {
			if(!isAllActorsFinishWork())
			{
				receiveMessageFromCategoryManager(r, allResponses, resV2);

			}
		}).match(CollectionTimeout.class, cancel -> {
			if(!isAllActorsFinishWork())
			{
				logger.info("Deadline passed for batch:{} to be finished, cancelling querytimeout , clear all running actors", this.batchNumber);
				queryTimeoutTimer.cancel();
				runningActors.clear();
				requester.tell(new BOMExportActorManager.ResponsdForAllParts(resV2), self());

			}
		}).build();

	}

	private void receiveMessageFromCategoryManager(ExportResponseMessage r, Map<String, List<Object>> allResponses, Map<String, ExportRow> resV2)
	{
		String categoryName = r.getCategoryName();
		logger.info("BomExportManager actor received message from cateogry:{} batch:{}", categoryName, this.batchNumber);
		Map<String, Object> response = r.getResponse();
		String managerRequestID = r.getRequestId();
		markActorAsFinished(managerRequestID);
		mergeResponseMapWithAllResponsesV2(response, resV2, categoryName);
		if(isAllActorsFinishWork())
		{
			logger.info("Sending Respond all parts message to the main controller");
			queryTimeoutTimer.cancel();
			requester.tell(new BOMExportActorManager.ResponsdForAllParts(resV2), self());
		}
		else
		{
			getContext().become(responsdToMessages(allResponses, resV2));
		}
	}

	private void mergeResponseMapWithAllResponsesV2(Map<String, Object> response, Map<String, ExportRow> allResponses, String categoryName)
	{
		response.entrySet().forEach(e -> {
			String key = e.getKey();
			ExportRow partList = allResponses.get(key);
			if(partList == null)
			{
				partList = new ExportRow();
				allResponses.put(key, partList);
			}
			if(CategoryActorEnum.PARAMETRIC.getCategoryName().equals(categoryName))
			{
				partList.setFeatures((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.MANUFACTURER.getCategoryName().equals(categoryName))
			{
				List<NameValueDTO> nameValueList = (List<NameValueDTO>) e.getValue();
				partList.setManufacturers(serializeResultInFetNameValue(nameValueList));
			}
			if(CategoryActorEnum.PACKAGE.getCategoryName().equals(categoryName))
			{
				List<NameValueDTO> nameValueList = (List<NameValueDTO>) e.getValue();
				partList.setPackageResult(serializeResultInFetNameValue(nameValueList));
			}
			if(CategoryActorEnum.PCN.getCategoryName().equals(categoryName))
			{
				// partList.setPcnList((List<PCNResponseDTO>) e.getValue());
			}
			if(CategoryActorEnum.PACKAGING.getCategoryName().equals(categoryName))
			{
				List<NameValueDTO> nameValueList = (List<NameValueDTO>) e.getValue();
				partList.setPackagingResult(serializeResultInFetNameValue(nameValueList));
			}
			if(CategoryActorEnum.RISK.getCategoryName().equals(categoryName))
			{
				partList.setRiskResult((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.CLASSIFICATION.getCategoryName().equals(categoryName))
			{
				partList.setClassificationData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.REACH.getCategoryName().equals(categoryName))
			{
				partList.setReachData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.QUALIFICATION.getCategoryName().equals(categoryName))
			{
				partList.setQualificationData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.RAREELEMENTS.getCategoryName().equals(categoryName))
			{
				partList.setRareElementsData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.CHINAROHS.getCategoryName().equals(categoryName))
			{
				partList.setChinaROHSData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.WEEE.getCategoryName().equals(categoryName))
			{
				partList.setWeeeData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.COOS.getCategoryName().equals(categoryName))
			{
				partList.setCountryOfOriginData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.PRICE.getCategoryName().equals(categoryName))
			{
				partList.setPriceData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.ROHS.getCategoryName().equals(categoryName))
			{
				partList.setRohsData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.SUMMARY.getCategoryName().equals(categoryName))
			{
				partList.setSummaryData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.CONFLICT_MINIRALS.getCategoryName().equals(categoryName))
			{
				partList.setConflictMiniralsData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.GENERAL_PASSIVE.getCategoryName().equals(categoryName))
			{
				partList.setGeneralPassiveData((List<FeatureNameValueDTO>) e.getValue());
			}
			if(CategoryActorEnum.SUMMARY.getCategoryName().equals(categoryName))
			{
				partList.setSummaryData((List<FeatureNameValueDTO>) e.getValue());
			}
		});
	}

	private List<FeatureNameValueDTO> serializeResultInFetNameValue(List<NameValueDTO> result)
	{
		List<FeatureNameValueDTO> list = new LinkedList<>();
		result.forEach(p -> {
			list.add(new FeatureNameValueDTO(p.getName(), p.getValue()));
		});
		return list;
	}

	// for handling actors from router
	private void setUpCategoryManagerActors(List<String> comIDs, Set<String> categories)
	{
		long start = System.currentTimeMillis();
		queryTimeoutTimer = getContext().getSystem().scheduler().scheduleOnce(timeout, getSelf(), new CollectionTimeout(), getContext().getDispatcher(), getSelf());
		categories.forEach(catName -> {
			Router catRouter = routers.get(catName);
			String catManagerName = catName + Constants.ACTOR_MANAGER_POSTFIX;
			Router parentCatRouter = parentCatrouters.get(catManagerName);
			String managerRequestID = System.nanoTime() + "";
			boolean isConfiguredFeatureExist = this.configuredFeaturesName.get(catName) != null;
			if(!isConfiguredFeatureExist)
			{
				logger.info("There is no configured features for :{}", catName);
			}
			if(catRouter != null && parentCatRouter != null && isConfiguredFeatureExist)
			{
				parentCatRouter.route(new CategoriesManagerActor.CategoryExportMessage(catRouter, self(), managerRequestID, comIDs, partsPerActor, this.configuredFeaturesName.get(catName)), self());
				logger.info("Now sending message to category manager:{}", catManagerName);
				runningActors.put(managerRequestID, catManagerName);
			}
		});
		long end = System.currentTimeMillis() - start;
		logger.info("Finish Running actor managers in:{} with ids:{}", end, runningActors);
	}

	@Override
	public void postStop()
	{
		queryTimeoutTimer.cancel();
	}

	@Override
	public void preStart()
	{
		log.debug("BOM Export starting");
	}

	private boolean isAllActorsFinishWork()
	{
		logger.info("Checking running actors for batch:{} == :{}", this.batchNumber, runningActors);
		return runningActors.isEmpty();
	}

	private void markActorAsFinished(String requestId2)
	{
		runningActors.remove(requestId2);
	}

	public static Props props(long requestId, FiniteDuration timeout, int partsPerActor, int actorsPercategory)
	{
		return Props.create(BOMExportActorManager.class, () -> new BOMExportActorManager(requestId, timeout, partsPerActor, actorsPercategory));
	}

	// this message is received only once for each sublist
	public static class ExportAllParts
	{
		final List<String> comIDs;
		final Set<String> categories;
		final ActorRef requester;
		final Integer batchNumber;
		private final  Map<String, Map<String, String>> configuredFeaturesName;

		public ExportAllParts(List<String> comIDs, Set<String> categories, ActorRef requester, Integer batchNumber,Map<String, Map<String, String>> configuredFeaturesName)
		{
			super();
			this.comIDs = comIDs;
			this.categories = categories;
			this.requester = requester;
			this.batchNumber = batchNumber;
			this.configuredFeaturesName=configuredFeaturesName;
		}

		public Integer getBatchNumber()
		{
			return batchNumber;
		}

		public List<String> getComIDs()
		{
			return comIDs;
		}

		public Set<String> getCategories()
		{
			return categories;
		}

		public ActorRef getRequester()
		{
			return requester;
		}
		
		public Map<String, Map<String, String>> getConfiguredFeaturesName()
		{
			return configuredFeaturesName;
		}
	}

	// this message is responded only once for each sublist
	public static class ResponsdForAllParts
	{
		private final Map<String, ExportRow> response;

		public ResponsdForAllParts(Map<String, ExportRow> response)
		{
			super();
			this.response = response;
		}

		public Map<String, ExportRow> getResponse()
		{
			return response;
		}
	}
}
