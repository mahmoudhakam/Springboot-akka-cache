package com.se.part.search.services.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.solr.common.util.Hash;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.export.ExportRow;
import com.se.part.search.dto.export.OnPremExportFeatures;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.BOMExportActorManager.ResponsdForAllParts;
import com.se.part.search.services.export.threading.BaseCategoryManager;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import scala.concurrent.duration.FiniteDuration;

@Service
public class BomExportService
{
	private final String BOM_EXPORT_ACTOR_NAME = "bomExportRequesterActor";
	private PartSearchHelperService helperService;
	private ActorSystem actorSystem;
	private ActorRef bomExportrequesterActor;
	private Environment env;
	private int partsPerActor;
	private int actorsPerCategory;
	@Value("#{environment['export.batch']}")
	private int exportBatch;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, ExportRow> allResponses = null;
	@Value("#{environment['export.thread.batch']}")
	private int exportThreadBatch;
	private ApplicationContext context;
	private Map<String, ExportRow> concurrentMap = null;
	@Value("#{environment['export.manager.timeout']}")
	private int exportManagerTimeout;
	private Map<String, Map<String, String>> uiFeaturesVsBackendFeatures;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public BomExportService(PartSearchHelperService helperService, ActorSystem actorSystem, Environment env, ApplicationContext context)
	{
		super();
		this.helperService = helperService;
		this.actorSystem = actorSystem;
		this.env = env;
		this.context = context;
	}

	@PostConstruct
	void init()
	{
		partsPerActor = env.getProperty("export.partsPerActor", Integer.class, 15);
		actorsPerCategory = env.getProperty("export.actorsPerCategory", Integer.class, 15);
		bomExportrequesterActor = actorSystem.actorOf(BOMExportActorManager.props(System.nanoTime(), FiniteDuration.create(exportManagerTimeout, TimeUnit.SECONDS), partsPerActor, actorsPerCategory), BOM_EXPORT_ACTOR_NAME);
		cacheExportFeatures();
	}

	private void cacheExportFeatures()
	{
		long start = System.currentTimeMillis();
		logger.info("Caching export features in progress");
		uiFeaturesVsBackendFeatures = new HashMap<>();
		List<OnPremExportFeatures> features = getFeaturesFromDB();
		Map<Integer, String> idVsSectionName = new HashMap<>();
		features.forEach(f -> {
			if(f.getParentFeatureId() == 0)
			{
				idVsSectionName.put(f.getId(), f.getFeatureKey());
			}
		});
		features.forEach(f -> {
			if(f.getParentFeatureId() != 0)
			{
				String categoryName = idVsSectionName.get(f.getParentFeatureId());
				Map<String, String> uiFeatures = uiFeaturesVsBackendFeatures.get(categoryName);
				if(uiFeatures == null)
				{
					uiFeatures = new HashMap<>();
					uiFeaturesVsBackendFeatures.put(categoryName, uiFeatures);
				}
				uiFeatures.put(f.getFeatureKey(), f.getJsonKey());
			}
		});
		logger.info("Caching export features takes about:{}", (System.currentTimeMillis() - start));
	}

	private List<OnPremExportFeatures> getFeaturesFromDB()
	{
		List<OnPremExportFeatures> features = new LinkedList<>();
		String sql = "SELECT * FROM onprem_export_features WHERE is_Active=1";
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		result.forEach(row -> {
			int id = (int) row.get("ID");
			String featureKey = (String) row.get("FEATURE_KEY");
			int parentFetId = (int) row.get("PARENT_FETURE");
			String jsonKey = (String) row.get("JSON_KEY");
			boolean isActive = (int) row.get("IS_ACTIVE") > 0 ? true : false;
			OnPremExportFeatures feature = new OnPremExportFeatures(id, featureKey, parentFetId, jsonKey, isActive);
			features.add(feature);
		});
		return features;
	}

	private Map<String, Map<String, String>> getConfiguredFeaturesAccordingToSentFeatures(Map<String, List<String>> categoriesVsFeatures)
	{
		Map<String, Map<String, String>> configuredFeatures = new HashMap<>();
		categoriesVsFeatures.entrySet().forEach(e -> {
			Map<String, String> filteredFeatures = uiFeaturesVsBackendFeatures.get(e.getKey());
			String categoryName = e.getKey();
			List<String> sentFeatures = e.getValue();
			Map<String, String> featuresMap = new HashMap<>();
			if(filteredFeatures != null)
			{
				sentFeatures.forEach(featureKey -> {
					String jsonKey = filteredFeatures.get(featureKey);
					if(jsonKey != null)// sent feature are available
					{
						featuresMap.put(jsonKey, featureKey);
					}
				});
			}
			configuredFeatures.put(categoryName, featuresMap);
		});
		logger.info("Filtered Features:{} ready to be exported ...",configuredFeatures);
		return configuredFeatures;
	}

	public Map<String, ExportRow> processRequest(List<String> comIDs, Map<String, List<String>> categoriesVsFeatures, int batchSize)
	{
		Map<String, Map<String, String>> configuredFeaturesName = getConfiguredFeaturesAccordingToSentFeatures(categoriesVsFeatures);
		allResponses = new java.util.LinkedHashMap<>();
		int inProgress = 0;
		int batchNumber = 1;
		int originalPartsSize = comIDs.size();
		int finishedParts = 0;
		if(batchSize > 0)
		{
			this.exportBatch = batchSize;
		}
		logger.info("Start proccessing all requests with part:{} and Categories:{}", originalPartsSize, categoriesVsFeatures.keySet());
		for(int i = 0; i < comIDs.size(); i += exportBatch)
		{
			List<String> actorComIDs = comIDs.subList(i, Math.min(i + exportBatch, comIDs.size()));
			inProgress = actorComIDs.size();
			final Inbox inbox = Inbox.create(actorSystem);
			finishedParts += inProgress;
			logger.info("Sending message contains :{} part and still remaining :{} parts -- batch:{}", inProgress, (originalPartsSize - finishedParts), batchNumber);
			bomExportrequesterActor.tell(new BOMExportActorManager.ExportAllParts(actorComIDs, categoriesVsFeatures.keySet(), inbox.getRef(), batchNumber, configuredFeaturesName), inbox.getRef());
			FiniteDuration duration = FiniteDuration.create(exportManagerTimeout + 2, TimeUnit.SECONDS);
			try
			{
				BOMExportActorManager.ResponsdForAllParts message = (ResponsdForAllParts) inbox.receive(duration);
				logger.info("The main controller revceived message for batch: {}", batchNumber);
				allResponses = Stream.concat(message.getResponse().entrySet().stream(), allResponses.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));
			}
			catch(Exception e)
			{
				logger.error("bom export failed", e);
			}
			batchNumber++;
		}
		mergeSummaryWithPassiveData(allResponses);
		return allResponses;
	}

	private void mergeSummaryWithPassiveData(Map<String, ExportRow> allResponses)
	{
		allResponses.entrySet().stream().forEach(e -> {
			ExportRow row = e.getValue();
			if(row.getSummaryData() != null || row.getGeneralPassiveData() != null)
			{
				if(row.getSummaryData() == null)
				{
					row.setSummaryData(new ArrayList<>());
				}
				if(row.getGeneralPassiveData() != null)
				{
					row.getSummaryData().addAll(row.getGeneralPassiveData());
				}
				row.setGeneralPassiveData(null);
			}
		});
	}

	public Map<String, ExportRow> processRequestV2(List<String> comIDs, Set<String> categories, int batchSize)
	{
		concurrentMap = new ConcurrentHashMap<>();
		comIDs.forEach(c -> {
			concurrentMap.put(c, new ExportRow());
		});
		int noOfThreads = categories.size();
		int batchNumber = 1;
		int inProgressParts = 0;
		int originalListSize = comIDs.size();
		int finishedParts = 0;
		if(batchSize > 0)
		{
			this.exportThreadBatch = batchSize;
		}
		for(int i = 0; i < comIDs.size(); i += exportThreadBatch)
		{

			long start = System.currentTimeMillis();
			CountDownLatch latch = new CountDownLatch(noOfThreads);
			logger.info("Creating latch for :{} category mangers", noOfThreads);
			List<String> actorComIDs = comIDs.subList(i, Math.min(i + exportThreadBatch, comIDs.size()));
			inProgressParts = actorComIDs.size();
			finishedParts += inProgressParts;
			logger.info("Start Exporting batch:{} with inprogress parts:{} and remaining parts:{}", batchNumber, inProgressParts, (originalListSize - finishedParts));
			categories.forEach(category -> {
				try
				{
					BaseCategoryManager exportingBean = context.getBean(category, BaseCategoryManager.class);
					exportingBean.exportCategoryResult(actorComIDs, concurrentMap, latch);
				}
				catch(Exception e)
				{
					logger.error("Error during creating exporting category bean:{}", category);
					latch.countDown();
				}
			});
			try
			{
				logger.info("Waiting for batch:{} to complete", batchNumber);
				latch.await(22000, TimeUnit.MILLISECONDS);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			long end = System.currentTimeMillis() - start;
			logger.info("Exporting batch finished:{} in about:{}", batchNumber, end);
			batchNumber++;
		}
		return concurrentMap;
	}

	private int calculateNumberOfThreads(int listSize, int batchSize)
	{
		int threadNumber = listSize / batchSize;
		if(listSize % batchSize == 0)
		{
			return threadNumber;
		}
		return threadNumber + 1;
	}
}
