package com.se.part.search.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.ManDTO;
import com.se.part.search.util.PartSearchServiceConstants;

@Service
public class ManufacturerValidatorService
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, ManDTO> manMap;
	private Map<String, Set<String>> mfrIDMap;
	private List<String> ignoredWords;
	private SolrClient manSolrServer;
	private PartSearchHelperService helperService;

	@Autowired
	public ManufacturerValidatorService(PartSearchHelperService helperService, SolrClient manSolrServer)
	{
		this.manSolrServer = manSolrServer;
		this.helperService = helperService;
	}

	@PostConstruct
	public void init()
	{
		ignoredWords = new ArrayList<>();
		ignoredWords.add("INDUSTRIAL CO. LTD");
		ignoredWords.add("GROUP CORPORATION");
		ignoredWords.add("GROUP COMPANY");
		ignoredWords.add("COMPANY GMBH");
		ignoredWords.add("GROUP, INC");
		ignoredWords.add("INDUSTRIAL");

		String query = "*:* ";
		SolrQuery solrQuery = new SolrQuery(query);
		manMap = new HashMap<String, ManDTO>();
		mfrIDMap = new HashMap<>();

		try
		{
			solrQuery.setRows(0);
			QueryResponse response = manSolrServer.query(solrQuery);
			SolrDocumentList results = response.getResults();
			if(results == null)
			{
				return;
			}
			long totalRows = results.getNumFound();
			solrQuery.setFields(PartSearchServiceConstants.ManufaturerCoreFields.MAN_ID, PartSearchServiceConstants.ManufaturerCoreFields.MAN_NAME, PartSearchServiceConstants.ManufaturerCoreFields.MAN_SEARCH);
			solrQuery.setRows((int) totalRows);
			response = manSolrServer.query(solrQuery);
			results = response.getResults();
			for(SolrDocument doc : results)
			{
				String validMan = (String) doc.getFieldValue(PartSearchServiceConstants.ManufaturerCoreFields.MAN_NAME);
				String validManCode = (String) doc.getFieldValue(PartSearchServiceConstants.ManufaturerCoreFields.MAN_CODE);
				String searchMan = (String) doc.getFieldValue(PartSearchServiceConstants.ManufaturerCoreFields.MAN_SEARCH);
				Long validManId = (Long) doc.getFieldValue(PartSearchServiceConstants.ManufaturerCoreFields.MAN_ID);
				ManDTO dto = new ManDTO(validMan, validManId, validManCode);
				manMap.put(searchMan, dto);
				addMfrSeparatedWords(mfrIDMap, validMan, "" + validManId);
			}
		}
		catch(SolrServerException | IOException e)
		{
			logger.error("Error during indexing manufaturers:", e);
		}
	}

	private void addMfrSeparatedWords(Map<String, Set<String>> MfrIDMap, String mfr, String mfrId)
	{
		String[] mfrSplit = mfr.split(" ");
		for(String mfrToken : mfrSplit)
		{
			String searchcategoryName = helperService.removeSpecialCharacters(mfrToken, true).toLowerCase();
			Set<String> addedIds = mfrIDMap.get(searchcategoryName);
			if(addedIds == null)
			{
				addedIds = new HashSet<>();
			}
			addedIds.add(mfrId);
			mfrIDMap.put(searchcategoryName, addedIds);
		}
	}

	public String validateManName(String manName)
	{
		String passedMan = manName.toUpperCase();
		ManDTO dto = manMap.get(passedMan);
		if(dto != null)
		{
			return dto.getManName();
		}
		for(String nanMan : manMap.keySet())
		{
			if((passedMan.contains(nanMan) && (((float) nanMan.length() / (float) passedMan.length()) >= 0.7)) || (nanMan.contains(passedMan) && (((float) passedMan.length() / (float) nanMan.length()) >= 0.7)))
			{
				dto = manMap.get(nanMan);
				return dto.getManName();
			}
		}
		return null;
	}

	private String getPreparedMan(String value)
	{
		for(String ignored : ignoredWords)
		{
			value.replaceAll(ignored, "");
		}
		value = helperService.removeSpecialCharacter(value).toUpperCase();
		value = value.replaceAll(Pattern.quote("."), "");
		return value;
	}

	public Map<String, String> validateManNames(List<String> manNames)
	{
		logger.info("Start validating manufacturer");
		Map<String, String> manNamesMap = new HashMap<String, String>();
		for(String manName : manNames)
		{
			if(manName == null || manName.isEmpty())
			{
				continue;
			}
			String passedMan = getPreparedMan(manName);
			ManDTO dto = manMap.get(passedMan);
			if(dto != null)
			{
				manNamesMap.put(manName, dto.getManName() + "::" + dto.getManId());
				continue;
			}
			for(String nanMan : manMap.keySet())
			{

				if((passedMan.contains(nanMan) && (((float) nanMan.length() / (float) passedMan.length()) >= 0.7)) || (nanMan.contains(passedMan) && (((float) passedMan.length() / (float) nanMan.length()) >= 0.7)))
				{
					dto = manMap.get(nanMan);
					manNamesMap.put(manName, dto.getManName() + "::" + dto.getManId());
					break;
				}
			}
		}
		return manNamesMap;
	}

	public ManDTO getValidatedMan(String man)
	{
		if(StringUtils.isBlank(man))
		{
			return null;
		}
		String passedMan = getPreparedMan(man);
		ManDTO dto = manMap.get(passedMan);
		if(dto != null)
		{
			return dto;
		}
		for(String nanMan : manMap.keySet())
		{
			if((passedMan.contains(nanMan) && (((float) nanMan.length() / (float) passedMan.length()) >= 0.7)) || (nanMan.contains(passedMan) && (((float) passedMan.length() / (float) nanMan.length()) >= 0.7)))
			{
				dto = manMap.get(nanMan);
				return dto;
			}
		}
		return null;
	}

	public Set<String> getValidatedManIds(String givenMan)
	{
		String manSearch = helperService.removeSpecialCharacters(givenMan, true).toLowerCase();
		Set<String> matchedManIds = new HashSet<>();
		for(String man : mfrIDMap.keySet())
		{
			float inputLength = manSearch.length();
			float originalLength = man.length();
			if(man.startsWith(manSearch) && inputLength / originalLength > 0.5)
			{
				matchedManIds.addAll(mfrIDMap.get(man));
			}
		}
		return matchedManIds;
	}

}
