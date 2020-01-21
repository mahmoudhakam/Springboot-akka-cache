package com.se.part.search.services.partSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.ParentSearchRequest;
import com.se.part.search.dto.PartSearchStep;
import com.se.part.search.dto.partSearch.AliasData;
import com.se.part.search.dto.partSearch.PartInput;
import com.se.part.search.dto.partSearch.PartSearchDTO;
import com.se.part.search.dto.partSearch.PartSearchRequest;
import com.se.part.search.dto.partSearch.PartSearchResult;
import com.se.part.search.services.ManufacturerValidatorService;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.PartSearchSolrDelegate;
import com.se.part.search.strategies.PartSearchStrategy;
import com.se.part.search.util.PartSearchServiceConstants;

@Service
public class PartSearchSearch implements PartSearchStrategy
{
	private static final String BEGINWITH_MODE = "beginwith";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private PartSearchSolrDelegate solrDelegateService;
	private PartSearchHelperService helperService;
	private ManufacturerValidatorService manufacturerValidator;
	private Set<String> availableMessageFlags;

	@Autowired
	public PartSearchSearch(PartSearchSolrDelegate solrDelegateService, PartSearchHelperService helperService, ManufacturerValidatorService manufacturerValidator)
	{
		this.manufacturerValidator = manufacturerValidator;
		this.helperService = helperService;
		this.solrDelegateService = solrDelegateService;
	}

	@PostConstruct
	void init()
	{
		availableMessageFlags = new HashSet<>();
		availableMessageFlags.add("0");
		availableMessageFlags.add("1");
		availableMessageFlags.add("2");
		availableMessageFlags.add("3");
		availableMessageFlags.add("4");
	}

	// partNumber=[{"partNumber":"SMM01020C3301FB000","manufacturer":"vishay"},{"partNumber":"bav99","manufacturer":"vishay"}]
	@Override
	public Object partSearchRequest(ParentSearchRequest request, PartSearchStrategy searchStrategy, List<PartSearchStep> steps) throws JSONException, SolrServerException, IOException
	{
		List<PartSearchResult> result = new ArrayList<>();
		PartSearchRequest partSearchRequest = (PartSearchRequest) request;
		List<PartInput> parts = helperService.convertJsonPartsMans(partSearchRequest.getPartNumber());
		if(checkSentComIDs(parts))
		{
			result = complementCustomerDataByComIDs(getListOfComIDs(parts), steps);
			if(!result.isEmpty())
			{
				return result;
			}
		}
		List<String> manNames = getListOfManNames(parts);
		Map<String, String> validatedManfacturers = manufacturerValidator.validateManNames(manNames);
		Map<String, PartSearchResult> partsResult = new HashMap<>();
		Map<String, PartInput> validatedPartMan = getValidatedPartManList(validatedManfacturers, parts, partsResult, partSearchRequest.getWildcardSingle(), partSearchRequest.getWildCardMulti());
		result = performPartSearch(validatedPartMan, partSearchRequest.getMode(), partSearchRequest.getPageNumber(), partSearchRequest.getPageSize(), partSearchRequest.getWildcardSingle(), partSearchRequest.getWildCardMulti(), steps, partsResult,
				partSearchRequest.getExcludedParts(), partSearchRequest.getStart());
		return result;
	}

	private boolean checkSentComIDs(List<PartInput> parts)
	{
		List<String> comIDs = new ArrayList<>();
		parts.forEach(p -> {
			if(!p.getComID().isEmpty())
			{
				comIDs.add(p.getComID());
			}
		});
		return !comIDs.isEmpty();
	}

	private List<PartSearchResult> complementCustomerDataByComIDs(List<String> comIDs, List<PartSearchStep> steps) throws SolrServerException, IOException
	{
		List<PartSearchResult> result = new ArrayList<>();
		SolrQuery query = foramteSummaryQuery(comIDs);
		QueryResponse response = helperService.executeSorlQuery(query, solrDelegateService.getPartsSummarySolrServer());
		if(!response.getResults().isEmpty())
		{
			SolrDocumentList documents = response.getResults();
			documents.forEach(d -> {
				PartSearchResult part = new PartSearchResult();
				part.setRequestedMPN(helperService.safeString((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.COM_ID)));
				PartSearchDTO partDTO = getSummaryDTOObject(d);
				part.getPartResult().add(partDTO);
				result.add(part);
			});
		}
		return result;
	}

	private SolrQuery foramteSummaryQuery(List<String> comIDs)
	{
		SolrQuery query = new SolrQuery();
		StringBuilder q = new StringBuilder();
		q.append(PartSearchServiceConstants.SummaryCoreFields.COM_ID).append(":").append("(");
		StringBuilder delimeter = new StringBuilder();
		comIDs.forEach(c -> {
			q.append(delimeter);
			q.append(c);
			delimeter.setLength(0);
			delimeter.append(" OR ");
		});
		q.append(")");
		query.set("q", q.toString());
		query.setRows(comIDs.size());
		return query;
	}

	private Map<String, PartInput> getValidatedPartManList(Map<String, String> validatedManfacturers, List<PartInput> parts, Map<String, PartSearchResult> partsResult, String wildcardSingle, String wildCardMulti)
	{
		logger.info("Getting validated man");
		Map<String, PartInput> result = new HashMap<>();
		parts.forEach(p -> {
			String manIdVsManName = validatedManfacturers.get(p.getMan());
			String validatedManID = manIdVsManName != null ? manIdVsManName.split("::")[1] : "";
			PartInput searchPart = new PartInput();
			searchPart.setPartNumber(p.getPartNumber());
			searchPart.setMan(validatedManID);
			result.put(p.getPartNumber().toUpperCase() + "::" + validatedManID, searchPart);
			PartSearchResult requestedPartResult = new PartSearchResult();
			requestedPartResult.setRequestedMPN(p.getPartNumber());
			requestedPartResult.setRequestedMan(p.getMan());
			String part = helperService.removeSpecialCharacters(p.getPartNumber(), true, wildcardSingle, wildCardMulti);
			partsResult.put(part.toUpperCase() + "::" + validatedManID, requestedPartResult);
		});
		return result;
	}

	private List<String> getListOfManNames(List<PartInput> parts)
	{
		return parts.stream().map(PartInput::getMan).collect(Collectors.toList());
	}

	private List<String> getListOfComIDs(List<PartInput> parts)
	{
		return parts.stream().map(PartInput::getComID).collect(Collectors.toList());
	}

	public SolrQuery formateExactSearchPartSummary(List<PartInput> validatedPartManName, String searchMode, String pageNumber, String pageSize, String wildcardSingle, String wildCardMulti)
	{
		SolrQuery query = new SolrQuery();
		StringBuilder partQuery = new StringBuilder();
		StringBuilder delimeter = new StringBuilder();
		validatedPartManName.forEach(e -> {
			String part = helperService.removeSpecialCharacters(e.getPartNumber(), true, wildcardSingle, wildCardMulti);
			String man = e.getMan();
			partQuery.append(delimeter);

			partQuery.append("(");
			partQuery.append(PartSearchServiceConstants.SummaryCoreFields.NAN_PARTNUM_EXACT + ":" + "\"" + part + "\"" + (searchMode.isEmpty() ? "" : "*"));
			if(!man.isEmpty())
			{
				partQuery.append(" AND ");
				partQuery.append(PartSearchServiceConstants.SummaryCoreFields.MAN_ID + ":" + "\"" + man + "\"");
			}
			partQuery.append(")");

			delimeter.setLength(0);
			delimeter.append(" OR ");
		});

		StringBuilder q = new StringBuilder();
		q.append(partQuery);

		query.set("q", q.toString());
		query.set("start", helperService.calculateSolrStartingPage(Integer.parseInt(pageNumber), Integer.parseInt(pageSize), null));
		query.set("rows", pageSize);

		return query;
	}

	public SolrQuery formateExactGroupBySearchPartSummary(List<PartInput> validatedPartManName, String searchMode, String pageNumber, String pageSize, String wildcardSingle, String wildCardMulti, Map<String, String> partAndManVsGroupQuery,
			String start, String excludedComIds)
	{
		SolrQuery query = new SolrQuery();
		StringBuilder q = new StringBuilder();
		query.set("group", true);
		StringBuilder or = new StringBuilder();
		boolean beginwith = (StringUtils.isNotEmpty(searchMode) && searchMode.equalsIgnoreCase(BEGINWITH_MODE));

		validatedPartManName.forEach(e -> {
			StringBuilder partQuery = new StringBuilder();
			String part = helperService.removeSpecialCharacters(e.getPartNumber(), true, wildcardSingle, wildCardMulti);
			String manID = e.getMan();

			partQuery.append("(");
			partQuery.append(PartSearchServiceConstants.SummaryCoreFields.NAN_PARTNUM_EXACT).append(":").append(part).append(!beginwith ? "" : "*");
			if(!manID.isEmpty())
			{
				partQuery.append(" AND ");
				partQuery.append(PartSearchServiceConstants.SummaryCoreFields.MAN_ID).append(":").append(manID);
			}
			partQuery.append(")");
			query.add("group.query", partQuery.toString());

			q.append(or);
			q.append(partQuery);
			or.setLength(0);
			or.append(" OR ");

			partAndManVsGroupQuery.put(partQuery.toString(), (part.toUpperCase() + "::" + manID));
		});
		if(validatedPartManName.size() == 1 && beginwith)
		{
			query.addSort("strdist(\"" + ClientUtils.escapeQueryChars(validatedPartManName.get(0).getPartNumber()) + "\",COM_PARTNUM,edit)", ORDER.desc);
		}
		if(StringUtils.isNotEmpty(excludedComIds))
		{
			q.append(" AND -").append(PartSearchServiceConstants.SummaryCoreFields.COM_ID).append(":(").append(excludedComIds).append(")");
		}
		query.set("group.format", "simple");
		query.set("group.limit", validatedPartManName.size());
		query.set("q", q.toString());
		query.set("start", helperService.calculateSolrStartingPage(Integer.parseInt(pageNumber), Integer.parseInt(pageSize), start));
		query.set("rows", pageSize);
		return query;
	}

	public SolrQuery formateExactSearchPartLookup(List<PartInput> validatedPartManName, String searchMode, String pageNumber, String pageSize, String wildcardSingle, String wildCardMulti)
	{
		SolrQuery query = new SolrQuery();
		StringBuilder partQuery = new StringBuilder();
		StringBuilder delimeter = new StringBuilder();
		validatedPartManName.forEach(e -> {
			String part = helperService.removeSpecialCharacters(e.getPartNumber(), true, wildcardSingle, wildCardMulti);
			String man = e.getMan();
			partQuery.append(delimeter);

			partQuery.append("(");
			partQuery.append(PartSearchServiceConstants.LookupCoreFields.NAN_PARTNUM + ":" + ClientUtils.escapeQueryChars(part) + (searchMode.isEmpty() ? "" : "*"));
			if(!man.isEmpty())
			{
				partQuery.append(" AND ");
				partQuery.append(PartSearchServiceConstants.LookupCoreFields.MAN_ID + ":" + man);
			}
			partQuery.append(")");

			delimeter.setLength(0);
			delimeter.append(" OR ");
		});

		StringBuilder q = new StringBuilder();
		q.append(partQuery);

		query.set("q", q.toString());
		query.set("start", helperService.calculateSolrStartingPage(Integer.parseInt(pageNumber), Integer.parseInt(pageSize), null));
		query.set("rows", pageSize);

		return query;
	}

	public SolrQuery formateExactSearchPassive(List<PartInput> validatedPartManName, String pageNumber, String pageSize, String wildcardSingle, String wildCardMulti)
	{
		SolrQuery query = new SolrQuery();
		StringBuilder partQuery = new StringBuilder();
		StringBuilder delimeter = new StringBuilder();
		validatedPartManName.forEach(e -> {
			String part = helperService.removeSpecialCharacters(e.getPartNumber(), true, wildcardSingle, wildCardMulti);
			String man = e.getMan();
			partQuery.append(delimeter);

			partQuery.append("(");
			partQuery.append(PartSearchServiceConstants.PassiveCoreFields.NAN_PARTNUM + ":" + part);
			if(!man.isEmpty())
			{
				partQuery.append(" AND ");
				partQuery.append(PartSearchServiceConstants.PassiveCoreFields.MAN_ID + ":" + "\"" + man + "\"");
			}
			partQuery.append(")");

			delimeter.setLength(0);
			delimeter.append(" OR ");
		});

		StringBuilder q = new StringBuilder();
		q.append(partQuery);

		query.set("q", q.toString());
		query.set("start", helperService.calculateSolrStartingPage(Integer.parseInt(pageNumber), Integer.parseInt(pageSize), null));
		query.set("rows", pageSize);

		return query;
	}

	public String getComIDFromLookupCore(SolrDocumentList documents)
	{
		return (String) documents.get(0).getFieldValue(PartSearchServiceConstants.LookupCoreFields.NEW_COM_ID);
	}

	public List<PartSearchDTO> transformPassiveDocuments(SolrDocumentList results)
	{
		List<PartSearchDTO> result = new ArrayList<>();
		results.forEach(d -> {
			PartSearchDTO dto = new PartSearchDTO();

			result.add(dto);
		});
		return result;
	}

	public List<PartSearchResult> performPartSearch(Map<String, PartInput> partManMap, String searchMode, String pageNumber, String pageSize, String wildcardSingle, String wildCardMulti, List<PartSearchStep> steps,
			Map<String, PartSearchResult> partsResultMap, String excludedComids, String startRow) throws SolrServerException, IOException
	{
		logger.info("Start searching part search");
		List<PartSearchResult> partsResults = new ArrayList<>();
		long start = System.currentTimeMillis();
		List<PartInput> validatedPartManName = new ArrayList<>(partManMap.values());
		// search in parts summary core
		Map<String, String> partAndManVsGroupQuery = new HashMap<>();
		SolrQuery query = formateExactGroupBySearchPartSummary(validatedPartManName, searchMode, pageNumber, pageSize, wildcardSingle, wildCardMulti, partAndManVsGroupQuery, startRow, excludedComids);
		logger.info("Start getting result with query:{}", query);
		QueryResponse response = helperService.executeSorlQuery(query, solrDelegateService.getPartsSummarySolrServer());
		PartSearchStep step = new PartSearchStep("PartSearch Summary", query.toString(), response.getElapsedTime(), System.currentTimeMillis() - start);
		steps.add(step);
		partsResults = getSummaryParts(response, partAndManVsGroupQuery, partsResultMap);
		return partsResults;
	}

	private List<PartSearchResult> getSummaryParts(QueryResponse response, Map<String, String> partAndManVsGroupQuery, Map<String, PartSearchResult> partsResultMap)
	{
		logger.info("Getting final result");
		logger.info("partsResultMap:{}", partsResultMap);
		logger.info("partAndManVsGroupQuery:{}", partAndManVsGroupQuery);
		List<PartSearchResult> partsResults = new ArrayList<>();
		if(response.getGroupResponse() != null)
		{
			GroupResponse groupResponse = response.getGroupResponse();
			List<GroupCommand> groupCommands = groupResponse.getValues();
			for(GroupCommand command : groupCommands)
			{
				String partVsManId = partAndManVsGroupQuery.get(command.getName());
				List<Group> groups = command.getValues();
				for(Group group : groups)
				{
					SolrDocumentList documents = group.getResult();
					if(documents != null && !documents.isEmpty())
					{
						PartSearchResult requestedPartResult = partsResultMap.get(partVsManId);
						List<PartSearchDTO> requestedPartDTOList = new ArrayList<>();
						documents.forEach(d -> {
							PartSearchDTO dto = getSummaryDTOObject(d);
							requestedPartDTOList.add(dto);
						});
						requestedPartResult.setPartResult(requestedPartDTOList);
						partsResults.add(requestedPartResult);
					}
				}
			}
		}
		return partsResults;
	}

	private PartSearchDTO getSummaryDTOObject(SolrDocument d)
	{
		PartSearchDTO dto = new PartSearchDTO();
		dto.setComID(helperService.safeString((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.COM_ID)));
		dto.setDescription(helperService.safeString((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.ROW_COM_DESC)));
		dto.setLifecycle(helperService.safeString((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.LC_STATE)));
		dto.setManufacturer(helperService.safeString((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.MAN_NAME)));
		dto.setPartNumber(helperService.safeString((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.COM_PARTNUM)));
		dto.setPlName(helperService.safeString((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.PL_NAME)));
		dto.setRohs(helperService.safeString((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.ROHS)));
		dto.setRohsVersion(helperService.safeString((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.ROHS_VERSION)));
		dto.setSmallImage(helperService.safeString((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.IMAGE_URL)));
		return dto;
	}

	private List<PartInput> getRemainingPartToSearch(List<PartSearchDTO> partsResults, Map<String, PartInput> partManMap)
	{
		partsResults.forEach(p -> {
			String key = p.getPartNumber().toUpperCase() + "::" + p.getManufacturerId();
			if(partManMap.get(key) != null)
			{// remove found parts from source map
				partManMap.remove(key);
			}
		});
		return new ArrayList<>(partManMap.values());
	}

	public List<PartSearchDTO> transformPartsSummaryDocuments(SolrDocumentList doc)
	{
		List<PartSearchDTO> partsDTOs = new ArrayList<>();
		long totalCount = doc.getNumFound();
		PartSearchDTO partsDTO = null;
		if(doc == null || doc.isEmpty())
		{
			return partsDTOs;
		}
		Iterator<SolrDocument> itr = doc.iterator();
		while(itr.hasNext())
		{
			SolrDocument document = itr.next();
			partsDTO = new PartSearchDTO();
			partsDTOs.add(partsDTO);
		}

		return partsDTOs;
	}

	private Set<AliasData> getAliasData(String messageFlag, String comId, String originalPartNumber)
	{
		Set<AliasData> partAliases = new HashSet<>();
		SolrClient aliasSolrServer = solrDelegateService.getPartsLookupSolrServer();
		String nanPart = helperService.removeSpecialCharacters(originalPartNumber, false).toUpperCase();
		SolrQuery solrQuery = new SolrQuery("(NEW_COM_ID:" + comId + " AND NAN_PARTNUM:" + nanPart + "* AND DATA_TYPE:Alias) OR (NEW_COM_ID:" + comId + " AND DATA_TYPE:Acquired)");
		if(messageFlag.equals("1"))
		{
			aliasSolrServer = solrDelegateService.getPartsAliasSolrServer();
			solrQuery = new SolrQuery("COM_ID:" + comId);
		}
		QueryResponse response = null;
		try
		{
			response = helperService.executeSorlQuery(solrQuery, aliasSolrServer);
		}
		catch(SolrServerException | IOException e)
		{
			logger.error("Error during getting alias data", e);
			return new HashSet<>();
		}
		SolrDocumentList documents = response.getResults();
		if(documents != null && !documents.isEmpty())
		{

			for(SolrDocument doc : documents)
			{
				String alias = (String) doc.getFieldValue("ALIAS_DATA");
				String type = (String) doc.getFieldValue("DATA_TYPE");
				if(StringUtils.isBlank(type))
				{
					type = "Acquired";
				}
				if(StringUtils.isNotBlank(alias))
					partAliases.add(new AliasData(type, alias));
			}
		}
		return partAliases;
	}

	public List<PartSearchDTO> populatePassivePartsResult(SolrDocumentList doc)
	{
		List<PartSearchDTO> partsDTOs = new ArrayList<>();
		long totalCount = doc.getNumFound();
		PartSearchDTO partsDTO = null;
		if(doc == null || doc.isEmpty())
		{
			return partsDTOs;
		}
		Iterator<SolrDocument> itr = doc.iterator();
		while(itr.hasNext())
		{
			SolrDocument document = itr.next();
			partsDTO = new PartSearchDTO();
			// partsDTO.setComId((String) helperService.emptyStringIfNull(document.getFieldValue("COM_ID")));
			// partsDTO.setComDesc((String) helperService.emptyStringIfNull(document.getFieldValue("DESCRIPTION")));
			// partsDTO.setManId(helperService.emptyStringIfNull(document.getFieldValue("MAN_ID")));
			// partsDTO.setManName((String) helperService.emptyStringIfNull(document.getFieldValue("MAN_NAME")));
			// partsDTO.setComExtsheetUrl((String) helperService.emptyStringIfNull(document.getFieldValue("OFFLINE_URL")));
			// partsDTO.setComPartnum(helperService.emptyStringIfNull(document.getFieldValue("COM_PARTNUM")));
			// partsDTO.setPlId(helperService.emptyStringIfNull(document.getFieldValue("PL_ID")));
			// partsDTO.setPlName((String) helperService.emptyStringIfNull(document.getFieldValue("PL_NAME")));
			// partsDTO.setLifeCycle((String) helperService.emptyStringIfNull(document.getFieldValue("LIFE_CYCLE")));
			// partsDTO.setRohas((String) helperService.emptyStringIfNull(document.getFieldValue("ROHS")));
			// partsDTO.setRohsVersion((String) helperService.emptyStringIfNull(document.getFieldValue("ROHS_VERSION")));
			// partsDTO.setTaxonomyPath((String) helperService.emptyStringIfNull(document.getFieldValue("TAX_PATH")));
			partsDTOs.add(partsDTO);
		}
		return partsDTOs;
	}
}
