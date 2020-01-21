package com.se.part.search.services.keywordSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.ParentSearchRequest;
import com.se.part.search.dto.PartSearchStep;
import com.se.part.search.dto.keyword.KeywordSearchRequest;
import com.se.part.search.dto.partSearch.PartSearchDTO;
import com.se.part.search.dto.partSearch.PartSearchResult;
import com.se.part.search.services.ManufacturerValidatorService;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.PartSearchSolrDelegate;
import com.se.part.search.strategies.PartSearchStrategy;
import com.se.part.search.util.PartSearchServiceConstants;

@Service
public class KeywordSearchSearch implements PartSearchStrategy
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private PartSearchHelperService helperService;
	private PartSearchSolrDelegate solrDelegateService;
	private ManufacturerValidatorService manufacturerValidator;

	@Value("#{environment['part.keyword.drop.limit']}")
	public String dropLimit;

	private Set<String> resrevedSolrWords;

	@PostConstruct
	public void init()
	{
		resrevedSolrWords = new HashSet<>();
		resrevedSolrWords.add("and");
		resrevedSolrWords.add("or");
	}

	@Autowired
	public KeywordSearchSearch(PartSearchHelperService helperService, PartSearchSolrDelegate solrDelegateService, ManufacturerValidatorService manufacturerValidator)
	{
		this.helperService = helperService;
		this.solrDelegateService = solrDelegateService;
		this.manufacturerValidator = manufacturerValidator;
	}

	@Override
	public Object partSearchRequest(ParentSearchRequest request, PartSearchStrategy searchStrategy, List<PartSearchStep> steps) throws SolrServerException, IOException
	{
		KeywordSearchRequest keywordSearchRequest = (KeywordSearchRequest) request;
		PartSearchResult result = new PartSearchResult();
		result.setRequestedMPN(keywordSearchRequest.getKeyword());
		List<PartSearchDTO> parts = new ArrayList<>();
		parts = searchWithTermsBeginwith(keywordSearchRequest.getKeyword(), keywordSearchRequest.getWildcardSingle(), keywordSearchRequest.getWildCardMulti(), keywordSearchRequest.getPageNumber(), keywordSearchRequest.getPageSize(), steps);
		if(!parts.isEmpty())
		{
			result.setPartResult(parts);
			return result;
		}
		parts = searchWithTerms(keywordSearchRequest.getKeyword(), keywordSearchRequest.getPageNumber(), keywordSearchRequest.getPageSize(), steps, true);
		result.setPartResult(parts);
		return result;
	}

	private List<PartSearchDTO> searchWithTerms(String keyword, String pageNumber, String pageSize, List<PartSearchStep> steps, boolean firstSearch) throws SolrServerException, IOException
	{
		List<PartSearchDTO> parts = new ArrayList<>();
		QueryResponse response = null;
		if(helperService.keyWordHasSpaces(keyword))
		{
			// Step 1: search with each term in (part or man or desc) AND (part or man or desc)
			String operator = " AND ";
			response = searchByTerms(operator, keyword, pageNumber, pageSize, steps, firstSearch);
			if(helperService.hasQueryEmptyResults(response))
			{
				// Step 2: search with each term in (part or man or desc) OR (part or man or desc)
				operator = " OR ";
				response = searchByTerms(operator, keyword, pageNumber, pageSize, steps, firstSearch);
			}
			// Step 3: Join search word and search in passive and lookup cores for the first time only
			// if(helperService.hasQueryEmptyResults(response) && firstSearch)
			// {
			// response = searchInPassive(keyword, pageNumber, pageSize, steps, firstSearch);
			// if(!response.getResults().isEmpty())
			// {
			// parts = transformPassiveDocuments(response.getResults());
			// return parts;
			// }
			// }
			// if(helperService.hasQueryEmptyResults(response) && firstSearch)
			// {
			// response = searchInLookup(keyword, pageNumber, pageSize, steps, firstSearch);
			// }
			// Step 4: Cut last letter and repeat the two steps without searching in passive and lookup cores
			if(helperService.hasQueryEmptyResults(response) && helperService.canDropOneCharacterFromLast(keyword, Integer.parseInt(dropLimit)))
			{
				keyword = helperService.dropOneCharacterFromLast(keyword, Integer.parseInt(dropLimit));
				return searchWithTerms(keyword, pageNumber, pageSize, steps, false);
			}
			// fill result
			parts = transformPartsSummaryDocuments(response.getResults());
		}

		return parts;
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

	private QueryResponse searchInLookup(String keyword, String pageNumber, String pageSize, List<PartSearchStep> steps, boolean firstSearch) throws SolrServerException, IOException
	{
		long start = System.currentTimeMillis();
		QueryResponse response = null;
		if(firstSearch)
		{
			SolrQuery query = formateTermsLookupQuery(keyword, pageNumber, pageSize);
			response = helperService.executeSorlQuery(query, solrDelegateService.getPartsLookupSolrServer());
			PartSearchStep setp = new PartSearchStep("Search lookupcore", query.toString(), response.getElapsedTime(), (System.currentTimeMillis() - start));
			steps.add(setp);
			if(!response.getResults().isEmpty())
			{
				start = System.currentTimeMillis();
				// if result found in lookup get comid then search in partsSummary
				String comID = getComIDFromLookupCore(response.getResults());
				SolrQuery partsQuery = new SolrQuery(PartSearchServiceConstants.SummaryCoreFields.COM_ID + ":" + comID);
				response = helperService.executeSorlQuery(partsQuery, solrDelegateService.getPartsSummarySolrServer());
				setp = new PartSearchStep("Search in summary after lookupcore", partsQuery.toString(), response.getElapsedTime(), (System.currentTimeMillis() - start));
				steps.add(setp);
				return response;
			}
		}
		return response;
	}

	public String getComIDFromLookupCore(SolrDocumentList documents)
	{
		return (String) documents.get(0).getFieldValue(PartSearchServiceConstants.LookupCoreFields.NEW_COM_ID);
	}

	private QueryResponse searchInPassive(String keyword, String pageNumber, String pageSize, List<PartSearchStep> steps, boolean firstSearch) throws SolrServerException, IOException
	{
		long start = System.currentTimeMillis();
		QueryResponse response = null;
		SolrQuery query = formateTermsPassiveQuery(keyword, pageNumber, pageSize);
		response = helperService.executeSorlQuery(query, solrDelegateService.getPassiveSolrServer());
		PartSearchStep setp = new PartSearchStep("Search in passive", query.toString(), response.getElapsedTime(), (System.currentTimeMillis() - start));
		steps.add(setp);
		return response;
	}

	private QueryResponse searchByTerms(String operator, String keyword, String pageNumber, String pageSize, List<PartSearchStep> steps, boolean firstSearch) throws SolrServerException, IOException
	{
		long start = System.currentTimeMillis();
		QueryResponse response = null;
		SolrQuery query = formateSearchByTermsQuery(operator, keyword, pageNumber, pageSize, firstSearch);
		response = helperService.executeSorlQuery(query, solrDelegateService.getPartsSummarySolrServer());
		PartSearchStep setp = new PartSearchStep(operator + " each term", query.toString(), response.getElapsedTime(), (System.currentTimeMillis() - start));
		steps.add(setp);
		return response;
	}

	private SolrQuery formateTermsLookupQuery(String keyword, String pageNumber, String pageSize)
	{
		SolrQuery query = new SolrQuery();
		StringBuilder q = new StringBuilder();
		q.append(PartSearchServiceConstants.LookupCoreFields.NAN_PARTNUM);
		q.append(":");
		q.append("\"" + keyword + "\"" + "*");
		query.set("q", q.toString());
		query.setStart(helperService.calculateSolrStartingPage(Integer.parseInt(pageNumber), Integer.parseInt(pageSize)));
		query.setRows(Integer.parseInt(pageSize));
		return query;
	}

	private SolrQuery formateTermsPassiveQuery(String keyword, String pageNumber, String pageSize)
	{
		SolrQuery query = new SolrQuery();
		StringBuilder q = new StringBuilder();
		q.append(PartSearchServiceConstants.PassiveCoreFields.NAN_PARTNUM);
		q.append(":");
		q.append("\"" + keyword + "\"" + "*");
		query.set("q", q.toString());
		query.setStart(helperService.calculateSolrStartingPage(Integer.parseInt(pageNumber), Integer.parseInt(pageSize)));
		query.setRows(Integer.parseInt(pageSize));
		return query;
	}

	private SolrQuery formateSearchByTermsQuery(String operator, String keyword, String pageNumber, String pageSize, boolean firstSearch)
	{
		SolrQuery query = new SolrQuery();
		String[] wordSplited = keyword.split("\\s+");
		Set<String> manIdsPerTerm = null;
		StringBuilder queryString = new StringBuilder();
		if(wordSplited != null && wordSplited.length > 0)
		{
			queryString.append(" ( ");
			String orDelimeter = " OR ";
			String termsDelimeter = "";
			for(String originalTerm : wordSplited)
			{
				if(firstSearch)
				{
					manIdsPerTerm = helperService.checkManidsForCurrentAndPreviousTerms(manIdsPerTerm, originalTerm, manufacturerValidator);
				}
				String term = helperService.removeSpecialCharacters(originalTerm, false);

				if(StringUtils.isBlank(term) || term.length() < 2)
				{
					continue;
				}

				if(resrevedSolrWords.contains(term.toLowerCase()))
				{
					term = term.toLowerCase();
					originalTerm = originalTerm.toLowerCase();
				}

				originalTerm = helperService.escapeSolrQueryChars(originalTerm);

				queryString.append(termsDelimeter); // btn terms

				queryString.append(" ( ");
				queryString.append(PartSearchServiceConstants.SummaryCoreFields.NAN_PARTNUM_EXACT).append(":").append(term).append("*");
				queryString.append(orDelimeter);
				queryString.append(PartSearchServiceConstants.SummaryCoreFields.ROW_COM_DESC).append(":").append(originalTerm);

				if(manIdsPerTerm != null && !manIdsPerTerm.isEmpty())
				{
					queryString.append(orDelimeter);
					queryString.append(helperService.generateOrQuery(PartSearchServiceConstants.SummaryCoreFields.MAN_ID, manIdsPerTerm));

				}
				queryString.append(" ) ");
				termsDelimeter = operator;
			}
			queryString.append(" ) ");
		}
		query.set("q", queryString.toString());
		query.setStart(helperService.calculateSolrStartingPage(Integer.parseInt(pageNumber), Integer.parseInt(pageSize)));
		query.setRows(Integer.parseInt(pageSize));
		return query;
	}

	private List<PartSearchDTO> searchWithTermsBeginwith(String terms, String wildcardSingle, String wildCardMulti, String pageNumber, String pageSize, List<PartSearchStep> steps) throws SolrServerException, IOException
	{
		List<PartSearchDTO> parts = new ArrayList<>();
		long start = System.currentTimeMillis();
		String searchWord = helperService.removeSpecialCharacters(terms, true, wildcardSingle, wildCardMulti);
		SolrQuery query = formateSearchByPartNumberBeginwith(searchWord, pageNumber, pageSize);
		QueryResponse response = helperService.executeSorlQuery(query, solrDelegateService.getPartsSummarySolrServer());
		PartSearchStep setp = new PartSearchStep("Joined Keyword Beginwith Search", query.toString(), response.getElapsedTime(), (System.currentTimeMillis() - start));
		steps.add(setp);
		if(!response.getResults().isEmpty())
		{
			parts = transformPartsSummaryDocuments(response.getResults());
			return parts;
		}
		return parts;
	}

	private SolrQuery formateSearchByPartNumberBeginwith(String searchWord, String pageNumber, String pageSize)
	{
		SolrQuery query = new SolrQuery();
		String q = PartSearchServiceConstants.SummaryCoreFields.NAN_PARTNUM_EXACT + ":" + searchWord + "*";
		query.set("q", q);

		query.setStart(helperService.calculateSolrStartingPage(Integer.parseInt(pageNumber), Integer.parseInt(pageSize)));
		query.setRows(Integer.parseInt(pageSize));

		return query;
	}

	public List<PartSearchDTO> transformPartsSummaryDocuments(SolrDocumentList doc)
	{
		List<PartSearchDTO> partsDTOs = new ArrayList<>();
		PartSearchDTO dto = null;
		if(doc == null || doc.isEmpty())
		{
			return partsDTOs;
		}
		Iterator<SolrDocument> itr = doc.iterator();
		while(itr.hasNext())
		{
			SolrDocument document = itr.next();
			dto = new PartSearchDTO();
			dto.setComID(helperService.safeString((String) document.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.COM_ID)));
			dto.setDescription(helperService.safeString((String) document.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.ROW_COM_DESC)));
			dto.setLifecycle(helperService.safeString((String) document.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.LC_STATE)));
			dto.setManufacturer(helperService.safeString((String) document.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.MAN_NAME)));
			dto.setPartNumber(helperService.safeString((String) document.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.COM_PARTNUM)));
			dto.setPlName(helperService.safeString((String) document.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.PL_NAME)));
			dto.setRohs(helperService.safeString((String) document.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.ROHS)));
			dto.setSmallImage(helperService.safeString((String) document.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.IMAGE_URL)));
			partsDTOs.add(dto);
		}
		return partsDTOs;
	}

}
