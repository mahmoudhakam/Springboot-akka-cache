package com.se.part.search.controller;

import static com.se.part.search.configuration.SpringExtension.SPRING_EXTENSION_PROVIDER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.se.part.search.bom.actors.BOMValidationActor;
import com.se.part.search.bom.messages.BOMRequestType;
import com.se.part.search.bom.similar.SimilarPartsResolver;
import com.se.part.search.dto.ParentSearchRequest;
import com.se.part.search.dto.PartSearchStep;
import com.se.part.search.dto.authentication.ArrowAuthenticationRequest;
import com.se.part.search.dto.authentication.PartSearchAuthenticationResponse;
import com.se.part.search.dto.bom.BOMRow;
import com.se.part.search.dto.bom.BOMStatistics;
import com.se.part.search.dto.export.BomExportRequest;
import com.se.part.search.dto.export.BomExportResponse;
import com.se.part.search.dto.export.ExportRow;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.dto.keyword.KeywordSearchRequest;
import com.se.part.search.dto.keyword.OperationMessages;
import com.se.part.search.dto.keyword.RequestParameters;
import com.se.part.search.dto.keyword.RestResponseWrapper;
import com.se.part.search.dto.keyword.Status;
import com.se.part.search.dto.partSearch.PartSearchDTO;
import com.se.part.search.dto.partSearch.PartSearchRequest;
import com.se.part.search.dto.partSearch.PartSearchResponse;
import com.se.part.search.dto.partSearch.PartSearchResult;
import com.se.part.search.messages.PartSearchOperationMessages;
import com.se.part.search.messages.PartSearchStatus;
import com.se.part.search.services.PartSearchloggerService;
import com.se.part.search.services.authentication.TokenBasedAuthentication;
import com.se.part.search.services.export.BomExportService;
import com.se.part.search.services.keywordSearch.JsonHandler;
import com.se.part.search.services.keywordSearch.KeywordSearchService;
import com.se.part.search.services.keywordSearch.TransformerService;
import com.se.part.search.services.keywordSearch.Util;
import com.se.part.search.strategies.PartAuthenticationStrategy;
import com.se.part.search.strategies.PartSearchStrategy;
import com.se.part.search.strategies.PartTransformationStrategy;
import com.se.part.search.strategies.PartValidationStrategy;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.PoisonPill;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author mahmoud_abdelhakam
 * 
 */

@Service
public class PartSearchServiceDelegate
{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TransformerService transformerService;

	@Autowired
	private KeywordSearchService keywordSearchService;
	@Autowired
	private Util<?> util;
	@Autowired
	ActorSystem actorSystem;
	JsonHandler<BOMRow> bomJsonConverter = new JsonHandler<>();
	// private Router router;

	@Autowired
	Environment env;

	@Autowired
	ApplicationContext ctx;
	private int numberofPartsInValidationBatch;

	private int partsPerThread;

	private int actorsPerCategory;
	private ActorRef bomExportActor;
	private Map<String, Router> routers;

	@PostConstruct
	public void init()
	{
		partsPerThread = env.getProperty("partsPerThread", Integer.class, 25);
		actorsPerCategory = env.getProperty("actorsPerCategory", Integer.class, 25);
		numberofPartsInValidationBatch = ConfigFactory.defaultApplication().getInt("akka.parts-per-actor");
		// List<Routee> routees = new ArrayList<Routee>();
		routers = createRouter();

	}

	private Map<String, Router> createRouter()
	{
		long start = System.currentTimeMillis();
		Map<String, Router> routers = new HashMap<>();
		for(BOMRequestType requestType : BOMRequestType.values())
		{
			List<Routee> routees = new ArrayList<Routee>();
			for(int i = 0; i < actorsPerCategory; i++)
			{
				ActorRef validator = actorSystem.actorOf(SPRING_EXTENSION_PROVIDER.get(actorSystem).props(requestType.stepName()));
				// actorSystem..watch(bomValidator);
				routees.add(new ActorRefRoutee(validator));
			}
			Router router = new Router(new RoundRobinRoutingLogic(), routees);
			routers.put(requestType.stepName(), router);
		}
		System.out.println("finished creaing router in" + (System.currentTimeMillis() - start));
		return routers;
	}

	public Optional<PartSearchResponse> partSearch(ParentSearchRequest request, PartValidationStrategy validationStrategy,
			PartSearchStrategy searchStrategy, PartTransformationStrategy transformationStrategy, PartSearchloggerService dbLoggerService,
			TokenBasedAuthentication tokenAuthService)
	{
		PartSearchResponse response = null;
		PartSearchRequest partSearchRequest = (PartSearchRequest) request;
		long start = System.currentTimeMillis();
		try
		{
			logger.info("Start getting part search data:{}", partSearchRequest.getFullURL());
			PartSearchOperationMessages validMessage = validationStrategy.validateArrowRequest(partSearchRequest, tokenAuthService);
			if(validMessage != PartSearchOperationMessages.SUCCESSFULL_OPERATION)
			{
				response = new PartSearchResponse(new PartSearchStatus(validMessage, false));
				return Optional.ofNullable(response);
			}
			List<PartSearchStep> steps = new ArrayList<>();
			List<PartSearchDTO> result = (List<PartSearchDTO>) searchStrategy.partSearchRequest(partSearchRequest, searchStrategy, steps);
			response = (PartSearchResponse) transformationStrategy.transformResult(result, transformationStrategy);
			if(!partSearchRequest.getDebugMode().trim().isEmpty())
			{
				response.setSteps(steps);
			}
			long end = System.currentTimeMillis() - start;
			// dbLoggerService.logPartSearch(partSearchRequest, end);
			logger.info("Finish getting part search data:{}", partSearchRequest.getFullURL());
		}
		catch(Exception e)
		{
			logger.error("Error during getting [partSearch]", e);
			response = new PartSearchResponse(new PartSearchStatus(PartSearchOperationMessages.FAILED_OPERATION, false));
			return Optional.ofNullable(response);
		}
		return Optional.ofNullable(response);
	}

	/**
	 * keyword search algorithm Remove white spaces and special chars and search in partsSummary nanPartNumber begin with if no result search with
	 * each term in (part or man or desc) AND (part or man or desc) if no result search with each term in (part or man or desc) OR (part or man or
	 * desc) if no result Search in passive and lookup cores for the first time only if no result Cut last letter and repeat the two steps without
	 * searching in passive and lookup cores
	 **/

	public Optional<PartSearchResponse> keywordSearch(ParentSearchRequest request, PartValidationStrategy validationStrategy,
			PartSearchStrategy searchStrategy, PartTransformationStrategy transformationStrategy, PartSearchloggerService dbLoggerService,
			TokenBasedAuthentication tokenAuthService)
	{
		PartSearchResponse response = null;
		KeywordSearchRequest keywordSearchRequest = (KeywordSearchRequest) request;
		long start = System.currentTimeMillis();
		try
		{
			PartSearchOperationMessages validMessage = validationStrategy.validateArrowRequest(keywordSearchRequest, tokenAuthService);
			if(validMessage != PartSearchOperationMessages.SUCCESSFULL_OPERATION)
			{
				response = new PartSearchResponse(new PartSearchStatus(validMessage, false));
				return Optional.ofNullable(response);
			}
			List<PartSearchStep> steps = new ArrayList<>();
			PartSearchResult parts = (PartSearchResult) searchStrategy.partSearchRequest(keywordSearchRequest, searchStrategy, steps);
			response = (PartSearchResponse) transformationStrategy.transformResult(parts, transformationStrategy);
			if(!keywordSearchRequest.getDebugMode().isEmpty())
			{
				response.setSteps(steps);
			}
			long end = System.currentTimeMillis() - start;
			// dbLoggerService.logKeywordSearch(keywordSearchRequest, end);
			logger.info("Finish getting keyword search data:{}", keywordSearchRequest.getFullURL());
		}
		catch(Exception e)
		{
			logger.error("Error during getting [keywordSearch]", e);
			response = new PartSearchResponse(new PartSearchStatus(PartSearchOperationMessages.FAILED_OPERATION, false));
			return Optional.ofNullable(response);
		}

		return Optional.ofNullable(response);
	}

	public Optional<PartSearchAuthenticationResponse> getToken(ArrowAuthenticationRequest arrowAuthenticationRequest,
			PartAuthenticationStrategy authStrategy)
	{
		PartSearchAuthenticationResponse response = new PartSearchAuthenticationResponse(
				new PartSearchStatus(PartSearchOperationMessages.SUCCESSFULL_OPERATION, true));
		try
		{
			logger.info("Start getting part authentication data:{}", arrowAuthenticationRequest.getFullURL());
			PartSearchOperationMessages validMessage = authStrategy.validate(arrowAuthenticationRequest.getUserName(),
					arrowAuthenticationRequest.getApiKey());
			if(validMessage != PartSearchOperationMessages.SUCCESSFULL_OPERATION)
			{
				response = new PartSearchAuthenticationResponse(new PartSearchStatus(validMessage, false));
				return Optional.ofNullable(response);
			}
			boolean isAuthenticated = authStrategy.authenticate(arrowAuthenticationRequest.getUserName(), arrowAuthenticationRequest.getApiKey());
			if(!isAuthenticated)
			{
				response = new PartSearchAuthenticationResponse(new PartSearchStatus(PartSearchOperationMessages.WRONG_USENAME_OR_PASSWORD, true));
				return Optional.ofNullable(response);
			}
			String token = authStrategy.createToken(arrowAuthenticationRequest.getUserName(), arrowAuthenticationRequest.getApiKey());
			if(!token.isEmpty())
			{
				response = new PartSearchAuthenticationResponse(new PartSearchStatus(PartSearchOperationMessages.SUCCESSFULL_OPERATION, true));
				response.setToken(token);
			}
			logger.info("Finish getting part authentication data:{}", arrowAuthenticationRequest.getFullURL());
		}
		catch(Exception e)
		{
			logger.error("Error during getting [getToken]", e);
			response = new PartSearchAuthenticationResponse(new PartSearchStatus(PartSearchOperationMessages.FAILED_OPERATION, false));
			return Optional.ofNullable(response);
		}
		return Optional.ofNullable(response);
	}

	public RestResponseWrapper getKeywordSearch(RequestParameters requestParameters) throws SolrServerException, IOException
	{
		Long start = System.currentTimeMillis();

		RestResponseWrapper resultWrapper = keywordSearchService.getKeywordSearch(requestParameters, false);

		if(!requestParameters.isBomFacetsRequest() && (resultWrapper == null || resultWrapper.getKeywordResults().isEmpty()))
		{
			return new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));
		}

		Long time = (System.currentTimeMillis() - start);
		resultWrapper.setServiceTime(time + " ms");

		return resultWrapper;
	}

	public String getKeywordSearchQuery(RequestParameters requestParameters)
	{
		return util.buildKeywordSearchQueryString(requestParameters.getKeyword(), requestParameters.getAutocompleteSection(),
				requestParameters.getKeywordOperator(), " OR ", true, null);

	}

	public RestResponseWrapper getAutoComplete(RequestParameters requestParameters)
			throws SolrServerException, IOException, InterruptedException, ExecutionException
	{
		Long start = System.currentTimeMillis();

		RestResponseWrapper resultWrapper = keywordSearchService.getAutoComplete(requestParameters);

		if(resultWrapper == null || resultWrapper.getAutoCompleteResult().isEmpty())
		{
			return new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));
		}

		Long time = (System.currentTimeMillis() - start);
		resultWrapper.setServiceTime(time + " ms");

		return resultWrapper;
	}

	public RestResponseWrapper validateBOM(RequestParameters requestParameters)
	{
		RestResponseWrapper wrapper = new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));
		List<BOMRow> requestedRows = bomJsonConverter.convertJSONToList(requestParameters.getBomData(), BOMRow.class);
		long start = System.currentTimeMillis();
		ActorRef bomVMainalidator = actorSystem.actorOf(
				BOMValidationActor.props(System.nanoTime(), FiniteDuration.create(25, TimeUnit.SECONDS), partsPerThread, actorsPerCategory, routers));
		final Inbox inbox = Inbox.create(actorSystem);
		for(int i = 0; i < requestedRows.size(); i += numberofPartsInValidationBatch)
		{
			List<BOMRow> subList = requestedRows.subList(i, Math.min(i + numberofPartsInValidationBatch, requestedRows.size()));
			bomVMainalidator.tell(new BOMValidationActor.RequestAllParts(subList, inbox.getRef()), inbox.getRef());

			FiniteDuration duration = FiniteDuration.create(15, TimeUnit.SECONDS);
			try
			{
				inbox.receive(duration);
			}
			catch(Exception e)
			{
				logger.error("bom validation failed", e);
			}
		}
		bomVMainalidator.tell(PoisonPill.getInstance(), ActorRef.noSender());
		wrapper.setBomResult(requestedRows);
		wrapper.setBomStatistics(createBOMStatisticsFromResults(requestedRows));
		wrapper.setServiceTime((System.currentTimeMillis() - start) + " ms");
		wrapper.setStatus(new Status(OperationMessages.SUCCESSFULL_OPERATION, true));

		return wrapper;
	}

	private BOMStatistics createBOMStatisticsFromResults(List<BOMRow> requestedRows)
	{
		Map<String, Integer> partMatchStatus = new LinkedHashMap<>();
		Map<String, Integer> manMatchStatus = new LinkedHashMap<>();
		Map<String, Integer> rohsStatus = new LinkedHashMap<>();
		Map<String, Integer> lcStatus = new LinkedHashMap<>();
		BOMStatistics bomStatistics = new BOMStatistics(partMatchStatus, manMatchStatus, rohsStatus, lcStatus);
		for(BOMRow part : requestedRows)
		{

			String matchStatus = part.getMatchStatus();
			String manValidationStatus = part.getManStatus();
			String rohs = StringUtils.defaultString(part.getRohs(), "Unknown");
			String lc = StringUtils.defaultString(part.getLifecycle(), "Unknown");
			updateStatistics(partMatchStatus, matchStatus);
			updateStatistics(manMatchStatus, manValidationStatus);
			updateStatistics(rohsStatus, rohs);
			updateStatistics(lcStatus, lc);
		}
		return bomStatistics;
	}

	private void updateStatistics(Map<String, Integer> statisticsMap, String value)
	{
		Integer previousCount = statisticsMap.get(value);
		if(previousCount == null)
		{
			previousCount = 0;
		}
		statisticsMap.put(value, ++previousCount);

	}

	public RestResponseWrapper getSimilarParts(String partNumber, String manufacturer, String similarStep)
	{
		RestResponseWrapper wrapper = new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));
		SimilarPartsResolver resolver = ctx.getBean(similarStep, SimilarPartsResolver.class);
		if(resolver == null)
		{
			return wrapper;
		}
		long start = System.currentTimeMillis();
		List<BOMRow> similarParts = resolver.similarParts(partNumber, manufacturer);
		wrapper.setBomResult(similarParts);
		wrapper.setServiceTime((System.currentTimeMillis() - start) + " ms");
		wrapper.setStatus(new Status(OperationMessages.SUCCESSFULL_OPERATION, true));
		return wrapper;
	}

	public RestResponseWrapper createFacetMap(RequestParameters requestParameters) throws SolrServerException, IOException
	{
		Long start = System.currentTimeMillis();

		RestResponseWrapper resultWrapper = keywordSearchService.facetMap(requestParameters);

		Long time = (System.currentTimeMillis() - start);
		resultWrapper.setServiceTime(time + " ms");

		return resultWrapper;
	}

	public BomExportResponse bomExport(BomExportRequest bomExportRequest, BomExportService bomExportService)
	{
		logger.info("Start exporting bom ");
		BomExportResponse bomExportResponse = new BomExportResponse(new Status(OperationMessages.NO_RESULT_FOUND, false));
		long start = System.currentTimeMillis();
		long end = 0l;
		try
		{
			Set<String> comIds = new HashSet<>();
			if(bomExportRequest.getCategories() != null && !bomExportRequest.getCategories().isEmpty())
			{
				List<String> summaryFeatures = bomExportRequest.getCategories().get(Constants.SUMMARY_STRATEGY);
				if(summaryFeatures != null)
				{
					bomExportRequest.getCategories().put(Constants.GENERAL_PASSIVE_STRATEGY, summaryFeatures);
				}
			}
			else
			{
				logger.info("No Categories are received");
				bomExportResponse = new BomExportResponse(new Status(OperationMessages.NO_CATEGORIES_RECEIVED, false));
				return bomExportResponse;
			}
			if(bomExportRequest.getComIDs() != null && !bomExportRequest.getComIDs().isEmpty())
			{
				comIds.addAll(bomExportRequest.getComIDs());
			}
			List<String> uniqueParts = new ArrayList<>();
			uniqueParts.addAll(comIds);
			Map<String, ExportRow> response = bomExportService.processRequest(uniqueParts, bomExportRequest.getCategories(),
					bomExportRequest.getBatchSize());
			if(!response.isEmpty())
			{
				bomExportResponse = new BomExportResponse(new Status(OperationMessages.SUCCESSFULL_OPERATION, true));
				bomExportResponse.setRespose(response);
			}
		}
		catch(Exception e)
		{
			logger.error("Error during exporting bom:", e);
		}
		end = System.currentTimeMillis() - start;
		bomExportResponse.setServiceTime(end + " ms");
		logger.info("Exporting bom Via Actor Model finished in :{} ms", end);
		return bomExportResponse;
	}
}
