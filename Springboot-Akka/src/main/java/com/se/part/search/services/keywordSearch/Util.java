package com.se.part.search.services.keywordSearch;

import static com.se.part.search.util.ConstantSolrFields.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.apache.solr.client.solrj.response.Suggestion;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.se.part.search.bom.messages.PartValidationStatuses;
import com.se.part.search.dto.bom.BOMRow;
import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.dto.keyword.parametric.FeatureDTO;
import com.se.part.search.dto.keyword.parametric.FeatureValueDTO;
import com.se.part.search.dto.keyword.parametric.SearchStep;
import com.se.part.search.services.keywordSearch.comparator.FeatureComparator;
import com.se.part.search.services.keywordSearch.comparator.SortIgnoreCase;
import com.se.part.search.util.ConstantSolrFields;

@Component
public class Util<T>
{
	private static final int MAX_OR_QUERY_ELEMENTS = 1000;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private Environment env;
	@Autowired
	private ApplicationContext context;
	@Autowired
	ManValidator manValidator;
	@Autowired
	@Lazy
	private ParametricSearchServiceHelper parametricSearchServiceHelper;
	public static final String INVALID = "invalid";
	public static final String MISSING = "missing";
	public static final String VALID = "valid";
	public static final String UNKNOWN = "unknown";

	// @Value("${" + Constants.SOLR_SHARDS_PARTS_CORE_URL + "}")
	// private String shardsPartsCoreUrl;

	private Set<String> resrevedSolrWords;

	@PostConstruct
	public void init()
	{
		resrevedSolrWords = new HashSet<>();
		resrevedSolrWords.add("and");
		resrevedSolrWords.add("or");
	}

	public String generateOrQuery(String filedName, Collection<String> fieldValues)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(filedName).append(":").append("(");
		int addedEllements = 0;
		for(String value : fieldValues)
		{

			builder.append(value).append(" ");
			addedEllements++;
			if(addedEllements > MAX_OR_QUERY_ELEMENTS)
			{
				break;
			}
		}
		builder.append(")");
		return builder.toString();
	}

	@Async
	public Future<List<String>> getSugesstions(String keyword, SolrClient solrServer, String dictionaryName, String step, CountDownLatch latch,
			List<SearchStep> steps)
	{
		List<String> sugesstions = new ArrayList<>(10);
		SolrQuery query = new SolrQuery();
		query.setRequestHandler(ConstantSolrFields.SUGGEST_HANDLER);
		query.set("suggest.q", keyword);
		query.set("suggest.dictionary", dictionaryName);

		try
		{
			QueryResponse response = solrServer.query(query);
			SuggesterResponse suggesterResponse = response.getSuggesterResponse();
			steps.add(new SearchStep(step, query.toString(), response.getQTime(), response.getElapsedTime()));
			List<Suggestion> suggestionsList = suggesterResponse.getSuggestions().get(dictionaryName);
			for(Suggestion suggestion : suggestionsList)
			{
				sugesstions.add(suggestion.getTerm());
			}

		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		finally
		{
			latch.countDown();
		}
		return new AsyncResult<List<String>>(sugesstions);

	}

	public void fillFeatureDTOsFields(List<FeatureDTO> emptyFeatures, List<FeatureDTO> fullFeatures)
	{
		for(FeatureDTO emptyFeature : emptyFeatures)
		{
			int indexOfEmptyFeature = fullFeatures.indexOf(emptyFeature);

			if(indexOfEmptyFeature != -1)
			{
				FeatureDTO fullFeature = fullFeatures.get(indexOfEmptyFeature);
				emptyFeature.setHcolName(fullFeature.getHcolName());
				emptyFeature.setSortType(fullFeature.getSortType());
			}
		}
	}

	public List<FeatureDTO> createKeywordFeatures()
	{
		List<FeatureDTO> keywordFeatures = new ArrayList<>();

		FeatureDTO vendor = new FeatureDTO();
		FeatureDTO rohs = new FeatureDTO();
		FeatureDTO lifeCycle = new FeatureDTO();

		vendor.setFetName(ConstantSolrFields.VENDOR);
		rohs.setFetName(ConstantSolrFields.ROHS);
		lifeCycle.setFetName(Constants.LIFE_CYCLE_FET_NAME);

		keywordFeatures.add(vendor);
		keywordFeatures.add(rohs);
		keywordFeatures.add(lifeCycle);

		return keywordFeatures;
	}

	public void appendQueryToSolrQuery(SolrQuery solrQuery, String query)
	{
		String oldQuery = solrQuery.getQuery();
		String newQuery = oldQuery;
		if(query != null && query.length() > 0)
		{
			newQuery = oldQuery + " AND " + query;
		}
		solrQuery.setQuery(newQuery);
	}

	public List<FeatureDTO> updateFilterDtoSeletedFilters(List<FeatureDTO> selectedFilters)
	{

		List<FeatureDTO> selectedFiltersToApply = new ArrayList<FeatureDTO>();
		for(FeatureDTO fet : selectedFilters)
		{
			if(fet.getValues().size() > 0 && hasAnyValue(fet.getValues()))
				selectedFiltersToApply.add(fet);
		}
		if(selectedFiltersToApply.size() > 0)
			return selectedFiltersToApply;
		return null;
	}

	private boolean hasAnyValue(List<FeatureValueDTO> values)
	{
		for(FeatureValueDTO valueDTO : values)
		{
			if(!valueDTO.getValue().trim().isEmpty())
			{
				return true;
			}
		}
		return false;
	}

	public String getFetHColnameSolrQuery(FeatureDTO feature)
	{
		String fetName = feature.getFetName();

		if(fetName.equalsIgnoreCase("Vendor") || fetName.equalsIgnoreCase("Manufacturer") || fetName.equalsIgnoreCase("Manufacturer_name"))
		{
			return ConstantSolrFields.MAN_NAME_EXACT;
		}
		if(fetName.equalsIgnoreCase("footprint") || fetName.equalsIgnoreCase("hasfootprint"))
		{
			return ConstantSolrFields.FOOTPRINT;
		}

		if(fetName.equalsIgnoreCase("ROHS") || fetName.equalsIgnoreCase("euROHS"))
		{
			return ConstantSolrFields.ROHS;
		}

		if(fetName.equalsIgnoreCase("LIFE CYCLE") || fetName.equalsIgnoreCase("partStatus") || fetName.equalsIgnoreCase("LIFECYCLE"))
		{
			return ConstantSolrFields.LIFE_CYCLE_SUMMARY;
		}

		if(fetName.equalsIgnoreCase("part number") || fetName.equalsIgnoreCase("full_part"))
		{
			return ConstantSolrFields.COM_PART_NUM;
		}
		if(fetName.equalsIgnoreCase(ConstantSolrFields.PL_NAME))
		{
			return ConstantSolrFields.PL_NAME;
		}
		if(feature.getSortType().equalsIgnoreCase("N") && isFeatureContainRangeValue(feature))
		{
			return feature.getHcolName() + "_TOKEN";
		}
		return feature.getHcolName() + "_VALUE";
	}

	private boolean isFeatureContainRangeValue(FeatureDTO featureDTO)
	{
		for(FeatureValueDTO valueDTO : featureDTO.getValues())
		{
			if(isFeatureValueRange(valueDTO))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isFeatureValueRange(FeatureValueDTO valueDTO)
	{
		String valueLower = valueDTO.getValue().toLowerCase();
		return (valueLower.contains("lt") || valueLower.contains("gt") || valueLower.contains(" to "));
	}

	public void handleNumericValues(FeatureValueDTO value)
	{
		String valueLower = value.getValue().toLowerCase();
		if(valueLower.contains("lt"))
		{
			String valueString = valueLower.replace("lt", "");
			value.setValue("* TO " + valueString);
			return;
		}
		if(valueLower.contains("gt"))
		{
			String valueString = valueLower.replace("gt", "");
			value.setValue(valueString + " TO *");
			return;
		}

		if(valueLower.contains("to"))
		{
			String valueString = valueLower.replaceAll("\\[", "").replaceAll("\\]", "");
			valueString = "[" + valueString.toUpperCase() + "]"; // upper to
																	// make it
																	// TO not to
																	// because
																	// solr will
																	// crash and
																	// world
																	// will be
																	// destroyed
																	// if to is
																	// small
			value.setValue(valueString);
		}
	}

	public void addFacetMethod(SolrQuery solrQuery)
	{
		String envFacet = env.getProperty(Constants.SOLR_FACET_METHOD);
		Integer facetThreads = env.getProperty(Constants.SOLR_FACET_THREADS, Integer.class, 1);
		solrQuery.set("facet.threads", facetThreads);
		if(envFacet != null && envFacet.equalsIgnoreCase("enum"))
		{
			solrQuery.setParam(FacetParams.FACET_METHOD, FacetParams.FACET_METHOD_enum);
		}
	}

	public void sortStringValues(FeatureDTO feature)
	{
		Collections.sort(feature.getValues(), new SortIgnoreCase());
	}

	public boolean isNullSpacesOrEmptyOrNULL(String s)
	{
		return isNullSpacesOrEmpty(s) || s.equalsIgnoreCase("null");
	}

	public boolean isNullSpacesOrEmpty(String s)
	{
		return s == null || "".equals(s.trim());
	}

	public void sortFeatureValues(FeatureDTO feature)
	{
		if(feature.getSortType() != null && feature.getSortType().equalsIgnoreCase("T"))
		{
			stripValues(feature.getValues());
			Collections.sort(feature.getValues(), new SortIgnoreCase());
		}
		else
		{
			Collections.sort(feature.getValues(), new FeatureComparator());
			stripValues(feature.getValues());
		}
		removeDuplicates(feature.getValues());
	}

	/**
	 * Ex: Values like C0G in 3!C0G and 14!C0G for the same one feature C_1774_ORIGINAL is returned twice from solr facet because of the display order
	 * 
	 * @param values
	 */
	private void removeDuplicates(List<FeatureValueDTO> values)
	{
		Map<String, FeatureValueDTO> valuesMap = new LinkedHashMap<>();
		for(FeatureValueDTO value : values)
		{
			FeatureValueDTO mappedValue = valuesMap.get(value.getValue());
			if(mappedValue != null)
			{
				value.setValueCount(value.getValueCount() + mappedValue.getValueCount());
			}
			valuesMap.put(value.getValue(), value);
		}
		values.clear();
		values.addAll(valuesMap.values());
	}

	private void stripValues(List<FeatureValueDTO> values)
	{
		for(int i = 0; i < values.size(); i++)
		{
			String fetValue = values.get(i).getValue();
			if(fetValue.indexOf("!") != -1)
			{
				String newValue = fetValue.split("!")[1];
				values.get(i).setValue(newValue);
			}
		}

	}

	public String removeSpecialCharacters(String value, boolean removeSpace)
	{
		value = value.replaceAll("[^a-zA-Z0-9\\=\\.\\s#+]", "").trim();
		if(removeSpace)
		{
			value = value.replaceAll(" ", "");
		}
		return value;
	}

	// public void appendShardQuery(SolrQuery solrQuery)
	// {
	// logger.info("Shards URLs=" + shardsPartsCoreUrl);
	//
	// if(shardsPartsCoreUrl != null)
	// {
	// solrQuery.set("shards", shardsPartsCoreUrl);
	// }
	// }

	public Object deepCopy(Serializable serializable)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(serializable);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public String buildKeywordSearchQueryString(String keyword, String autoCompleteSection, String mainOperator, String internalOperator,
			boolean firstSearch, Object descriptionField)
	{
		StringBuilder queryString = new StringBuilder();

		String internalDelimeter = "";
		String mainDelimeter = "";

		if(StringUtils.isNotBlank(keyword))
		{
			if(StringUtils.equalsIgnoreCase(autoCompleteSection, Constants.AUTOCOMPLETE_DESC))
			{
				queryString.append(ConstantSolrFields.PART_SUMMARY_DESCRIPTION).append(":").append("\"").append(keyword).append("\"");
				return queryString.toString();
			}
			queryString.append(" ( ");

			String[] wordSplited = keyword.split("\\s+");

			if(wordSplited != null && wordSplited.length > 0)
			{
				Set<String> manIdsPerTerm = null;
				for(String originalTerm : wordSplited)
				{
					Set<String> plIdsPerTerm = null;

					String term = removeSpecialCharacters(originalTerm, false);
					if(firstSearch)
						manIdsPerTerm = checkManidsForCurrentAndPreviousTerms(manIdsPerTerm, originalTerm);

					if(StringUtils.isBlank(term) || term.length() < 2)
					{
						continue;
					}
					queryString.append(mainDelimeter);

					queryString.append(" ( ");
					internalDelimeter = "";
					queryString.append(internalDelimeter);

					if(resrevedSolrWords.contains(term.toLowerCase()))
					{
						term = term.toLowerCase();
						originalTerm = originalTerm.toLowerCase();
					}
					if(firstSearch)
					{
						plIdsPerTerm = parametricSearchServiceHelper.getParametricCategory(originalTerm, CetgoryLookupType.Main);
					}

					// if (manDto == null) {
					// termManDTo = manValidator.getValidatedMan(originalTerm);
					// }

					term = ClientUtils.escapeQueryChars(term);
					originalTerm = ClientUtils.escapeQueryChars(originalTerm);
					queryString.append(ConstantSolrFields.NAN_PARTNUM_EXACT).append(":").append(term).append("*");

					if(descriptionField != null)
					{
						queryString.append(internalOperator);
						queryString.append(descriptionField).append(":").append(originalTerm);
					}

					if(plIdsPerTerm != null)
					{
						queryString.append(internalOperator);
						queryString.append(generateOrQuery(ConstantSolrFields.PL_ID, plIdsPerTerm));

					}

					if(manIdsPerTerm != null && !manIdsPerTerm.isEmpty())
					{
						queryString.append(internalOperator);
						queryString.append(generateOrQuery(ConstantSolrFields.MAN_ID, manIdsPerTerm));

					}
					internalDelimeter = internalOperator;
					queryString.append(" ) ");

					mainDelimeter = mainOperator;
				}
			}
		}
		queryString.append(" )");
		return queryString.toString();

	}

	private Set<String> checkManidsForCurrentAndPreviousTerms(Set<String> manIdsPerTerm, String curretnTerm)
	{
		if(manIdsPerTerm == null)
		{
			return manValidator.getValidatedManIds(curretnTerm);
		}
		else
		{
			Set<String> newIds = manValidator.getValidatedManIds(curretnTerm);
			if(newIds == null || newIds.isEmpty())
			{
				return null;
			}
			Set<String> tempId = new HashSet<String>(newIds);
			tempId.retainAll(manIdsPerTerm);
			if(tempId.isEmpty())
			{
				return newIds;
			}
			return tempId;

		}

	}

	public String dropOneCharacterFromLast(String keyword, int dropLimit)
	{
		String joinedTerms = "";

		String[] terms = (keyword + " ").split(" ");
		for(int i = 0; i < terms.length; i++)
		{
			String term = terms[i];
			int dropLimitForCurrentTerm = 1; // term.length() / 5 + 1;
			if(term.length() > dropLimit)
			{
				terms[i] = term.substring(0, term.length() - dropLimitForCurrentTerm);
			}
		}
		joinedTerms = join(terms);
		return joinedTerms;
	}

	public boolean canDropOneCharacterFromLast(String keyword, int dropLimit)
	{
		String[] terms = (keyword + " ").split(" ");
		for(int i = 0; i < terms.length; i++)
		{
			if(terms[i].length() > dropLimit)
			{
				return true;
			}
		}
		return false;
	}

	private String join(String[] arr)
	{
		if(arr != null && arr.length > 0)
		{
			StringBuffer sb = new StringBuffer();
			for(String s : arr)
			{
				sb.append(s).append(" ");
			}
			return sb.toString().trim();
		}
		return null;
	}

	public boolean hasNoResults(QueryResponse response)
	{
		return (response == null || response.getResults() == null || (response.getResults().isEmpty() && response.getResults().getNumFound() < 1));
	}

	public boolean isReservedSolrTerm(String term)
	{
		return resrevedSolrWords.contains(term.toLowerCase());
	}

	public String getPartManQuery(String partNumber, String validatedManId, StringBuilder queryBuilder, boolean ignoreManInput, boolean isExact,
			String nanFieldName)
	{
		queryBuilder.setLength(0);

		queryBuilder.append(" (").append(nanFieldName).append(":").append(removeSpecialCharacters(partNumber, true).toUpperCase()).append(!isExact ? "*" : "");
		if(StringUtils.isNotEmpty(validatedManId) && !ignoreManInput)
		{
			queryBuilder.append(" AND ( ").append(MAN_ID).append(":").append(validatedManId).append(" ) ");
		}
		queryBuilder.append(") ");
		return queryBuilder.toString();
	}

	public String getSimilarPartManQuery(Collection<String> values, String manufacturerId, boolean ignoreManId)
	{
		StringBuilder queryBuilder = new StringBuilder(values.size() * 5);
		queryBuilder.append(NAN_PARTNUM_EXACT).append(":").append("(");
		values.stream().forEach(term -> {
			queryBuilder.append(" ").append(term);
		});
		queryBuilder.append(") ");
		if(StringUtils.isNotEmpty(manufacturerId) && !ignoreManId)
		{
			queryBuilder.append(" AND ( ").append(MAN_ID).append(":").append(manufacturerId).append(" ) ");
		}
		return queryBuilder.toString();
	}

	public List<BOMRow> getPartsWithResultsFromParameters(Collection<BOMRow> parts, boolean shouldIgnoreManInput, boolean isExact,
			SolrClient solrServer, int rowSize) throws SolrServerException, IOException

	{
		return getPartsWithResultsFromParameters(parts, shouldIgnoreManInput, isExact, solrServer, rowSize, NAN_PARTNUM_EXACT, true);
	}

	public List<BOMRow> getPartsWithResultsFromParameters(Collection<BOMRow> parts, boolean shouldIgnoreManInput, boolean isExact,
			SolrClient solrServer, int rowSize, String nanFieldName, boolean sortResult) throws SolrServerException, IOException

	{
		StringBuilder groupQueryBuilder = new StringBuilder();
		StringBuilder queryBuilder = new StringBuilder();
		Map<String, List<BOMRow>> partsMap = new HashMap<>();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("group", true);
		solrQuery.set("group.format", "simple");
		solrQuery.set("rows", rowSize);
		StringBuilder queryDelimiter = new StringBuilder();
		List<BOMRow> foundParts = new LinkedList<>();

		parts.forEach(part -> {

			if(StringUtils.isEmpty(removeSpecialCharacters(part.getUploadedMpn(), true)))
			{
				part.setMatchStatus(PartValidationStatuses.MISSING.getMessage());
				part.setValidationStatusCode(PartValidationStatuses.MISSING.getCode());
				foundParts.add(part);

			}
			else
			{
				String query = getPartManQuery(removeSpecialCharacters(part.getUploadedMpn(), true), part.getManufacturerId(), groupQueryBuilder,
						shouldIgnoreManInput, isExact, nanFieldName).trim();
				queryBuilder.append(queryDelimiter).append(query);
				queryDelimiter.setLength(0);
				queryDelimiter.append("OR ");

				List<BOMRow> addedParts = partsMap.get(query);
				if(addedParts == null)
				{
					solrQuery.add("group.query", query);
					addedParts = new ArrayList<>();
				}
				addedParts.add(part);
				partsMap.put(query.trim(), addedParts);
			}

		});
		if(queryBuilder.length() > 0)
		{

			solrQuery.setQuery(queryBuilder.toString());
			if(sortResult && isExact)
			{
				solrQuery.setSort(PART_RATING, ORDER.desc);
			}
			QueryResponse response = solrServer.query(solrQuery, METHOD.POST);
			logger.info("Solr query time={}, and total time={}", response.getQTime(), response.getElapsedTime());
			GroupResponse groups = response.getGroupResponse();
			List<GroupCommand> commands = groups.getValues();
			commands.forEach(group -> {
				partsMap.get(group.getName()).forEach(part -> {
					SolrDocumentList results = group.getValues().get(0).getResult();
					if(!results.isEmpty())
					{
						fillPartData(part, results, shouldIgnoreManInput, isExact);
						foundParts.add(part);
					}
				});
				;
			});
		}
		return foundParts;
	}

	protected void fillPartData(BOMRow part, SolrDocumentList documents, boolean ignoreManInput, boolean exact)
	{
		for(SolrDocument doc : documents)
		{

			if(exact)
			{
				switch(part.getManStatus()){
					case INVALID:
						part.setMatchStatus(PartValidationStatuses.OTHER_MATCHES.getMessage());
						part.setValidationStatusCode(PartValidationStatuses.OTHER_MATCHES.getCode());
						part.setSimilarCount((int) documents.getNumFound());
						break;
					case MISSING:
						part.setMatchStatus(PartValidationStatuses.SELECT_MANUFACTURER.getMessage());
						part.setValidationStatusCode(PartValidationStatuses.SELECT_MANUFACTURER.getCode());
						part.setSimilarCount((int) documents.getNumFound());
						break;
					case VALID:
						if(ignoreManInput)
						{
							part.setMatchStatus(PartValidationStatuses.OTHER_MATCHES.getMessage());
							part.setValidationStatusCode(PartValidationStatuses.OTHER_MATCHES.getCode());
							part.setSimilarCount((int) documents.getNumFound());
							break;
						}
						fillFields(part, doc);
						part.setMatchStatus(PartValidationStatuses.VALID.getMessage());
						part.setValidationStatusCode(PartValidationStatuses.VALID.getCode());
						break;
					case UNKNOWN:
						fillFields(part, doc);
						part.setMatchStatus(PartValidationStatuses.LOOKUP.getMessage());
						part.setValidationStatusCode(PartValidationStatuses.LOOKUP.getCode());
				}

				// System.out.println(part);
			}
			else
			{
				part.setMatchStatus(PartValidationStatuses.BEGINWITH_SIMILAR_FOUND.getMessage());
				part.setValidationStatusCode(PartValidationStatuses.BEGINWITH_SIMILAR_FOUND.getCode());
				part.setSimilarCount((int) documents.getNumFound());
			}

		}
	}

	public SolrDocumentList getDocumentsFromQuery(String query, SolrClient partsSummarySolrServer, int pageSize, String originalPartNumber)
			throws SolrServerException, IOException
	{
		SolrQuery solrQuery = new SolrQuery("*:*");
		solrQuery.set("rows", pageSize);
		solrQuery.setFields(MAN_ID, COM_ID, COM_PART_NUM, PART_SUMMARY_DESCRIPTION, ROHS, ROHS_VERSION, LIFE_CYCLE_SUMMARY, PL_NAME,
				NAN_PARTNUM_EXACT, MAN_NAME_EXACT);

		solrQuery.setQuery(query);
		if(StringUtils.isNotEmpty(originalPartNumber))
			solrQuery.addSort("strdist(\"" + ClientUtils.escapeQueryChars(originalPartNumber) + "\",COM_PARTNUM,edit)", ORDER.desc);
		QueryResponse response = partsSummarySolrServer.query(solrQuery, METHOD.POST);

		SolrDocumentList documents = response.getResults();
		return documents;
	}

	public void fillFields(BOMRow part, SolrDocument doc)
	{
		String comId = String.valueOf(doc.getFieldValue(COM_ID));
		String validPart = (String) doc.getFieldValue(COM_PART_NUM);
		String nanPart = (String) doc.getFieldValue(NAN_PARTNUM_EXACT);
		String description = (String) doc.getFieldValue(PART_SUMMARY_DESCRIPTION);
		if(description == null)
		{
			description = (String) doc.getFieldValue(PASSIVE_CORE_DESCRIPTION);
		}
		String rohs = (String) doc.getFieldValue(ROHS);
		String rohsVersion = (String) doc.getFieldValue(ROHS_VERSION);
		String lifecycle = (String) doc.getFieldValue(LIFE_CYCLE_SUMMARY);
		String manName = (String) doc.getFieldValue(MAN_NAME_EXACT);
		if(manName == null)
		{
			manName = (String) doc.getFieldValue(MAN_NAME);
		}
		String manId = String.valueOf(doc.getFieldValue(MAN_ID));
		part.setComID(comId);
		part.setDescription(description);
		part.setNanPartNumber(nanPart);
		part.setRohs(rohs);
		part.setRohsVersion(rohsVersion);
		part.setLifecycle(lifecycle);
		part.setPartNumber(validPart);
		part.setManufacturer(manName);
		part.setManufacturerId(manId);
	}

	public List<String> getNewComId(BOMRow part, boolean ignoreMan, SolrClient lookupSolrServer) throws SolrServerException, IOException
	{
		String nanPart = removeSpecialCharacters(part.getUploadedMpn(), true);
		SolrQuery query = new SolrQuery();
		StringBuilder queryStr = new StringBuilder("NAN_PARTNUM:" + nanPart);
		if(StringUtils.isNotBlank(part.getManufacturerId()) && !ignoreMan)
		{
			queryStr.append(" AND (MAN_ID:").append(part.getManufacturerId()).append(" OR OLD_MAN:\"");
			queryStr.append(part.getUploadedManufacturer()).append("\")");
		}
		query.set("q", queryStr.toString());
		if(!ignoreMan)
		{

			query.set("rows", 1);
		}
		QueryResponse response = lookupSolrServer.query(query);
		SolrDocumentList documents = response.getResults();
		if(documents.getNumFound() != 0)
		{
			return documents.stream().map(doc -> String.valueOf(doc.getFieldValue("NEW_COM_ID"))).collect(Collectors.toList());

		}
		return null;
	}
	public List<BOMRow> extractPartListFromSolrDocuments(String originalPartNumber,  SolrDocumentList documents)
	{
		List<BOMRow> similarParts = new ArrayList<>();
		if(documents != null && documents.size() > 0)
		{
			documents.forEach(document -> {
				BOMRow part = new BOMRow();
				fillFields(part, document);
				int distance = StringUtils.getLevenshteinDistance(removeSpecialCharacters(originalPartNumber, true).toLowerCase(), part.getNanPartNumber().toLowerCase());
				float length = (float) originalPartNumber.length();
				float matchConfidence = length / ((float) distance + length);
				// float matchConfidence=(1-effectivePercentage)*100;
				part.setMatchConfidence(matchConfidence);
				similarParts.add(part);
			});

		}
		return similarParts;
	}
}
