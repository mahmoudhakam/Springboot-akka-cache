package com.se.part.search.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.se.part.search.dto.partSearch.PartInput;
import com.se.part.search.util.PartSearchServiceConstants;

@Service
public class PartSearchHelperService
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public String generateOrQuery(String filedName, Collection<String> fieldValues)
	{
		StringBuilder builder = new StringBuilder();
		final int MAX_OR_QUERY_ELEMENTS = 1000;
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

	public Set<String> checkManidsForCurrentAndPreviousTerms(Set<String> manIdsPerTerm, String curretnTerm, ManufacturerValidatorService manValidator)
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
			Set<String> tempId = new HashSet<>(newIds);
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
			int dropLimitForCurrentTerm = term.length() / 5 + 1;
			if(term.length() > dropLimit)
			{
				terms[i] = term.substring(0, term.length() - dropLimitForCurrentTerm);
			}
		}
		joinedTerms = join(terms);
		return joinedTerms;
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

	public boolean hasQueryEmptyResults(QueryResponse response)
	{
		return (response == null || response.getResults() == null || (response.getResults().isEmpty() && response.getResults().getNumFound() < 1));
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

	public boolean keyWordHasSpaces(String keyword)
	{
		keyword = keyword.trim();

		if(StringUtils.isEmpty(keyword) || removeSpecialCharacters(keyword, false).length() < 1)
		{
			return false;
		}

		if(StringUtils.isNotBlank(keyword))
		{
			String[] wordSplited = keyword.split("\\s+");

			if(wordSplited != null && wordSplited.length > 1)
			{
				return true;
			}
		}
		return false;
	}

	public String escapeSolrQueryChars(String searchWord)
	{
		return ClientUtils.escapeQueryChars(searchWord);
	}

	public String removeSpecialCharacter(String value)
	{
		value = value.replaceAll("[-\\\\/\\{\\(\\)\\|\\[\\]\\}\\'\t:;±$_,`*<>آ،أ€%@%^&??~!" + '"' + "]", "").trim();
		value = value.replaceAll(" ", "");
		return value;
	}

	public List<String> splitByDelimeter(String value, String delimeter)
	{
		return Arrays.asList(value.split(delimeter));
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

	public String removeSpecialCharacters(String value, boolean removeSpace, String wildcardSingle, String wildCardMulti)
	{
		if(!wildcardSingle.isEmpty() || !wildCardMulti.isEmpty())
		{

			String part = value;
			char singleCharacterWildcard = '?';
			char multiCharacterWildcard = '*';

			char[] c = part.toCharArray();

			if((!wildcardSingle.isEmpty() && part.contains(wildcardSingle)) || (!wildCardMulti.isEmpty() && part.contains(wildCardMulti)))
			{

				int singelWildcardIndex = 0;
				if(!wildcardSingle.isEmpty() && part.contains(wildcardSingle))
				{
					singelWildcardIndex = part.indexOf(wildcardSingle);
				}
				int multiWildcardIndex = 0;
				if(!wildCardMulti.isEmpty() && part.contains(wildCardMulti))
				{
					multiWildcardIndex = part.indexOf(wildCardMulti);
				}

				if(singelWildcardIndex != 0)
				{
					c[singelWildcardIndex] = singleCharacterWildcard;
				}

				if(multiWildcardIndex != 0)
				{
					c[multiWildcardIndex] = multiCharacterWildcard;
				}

				part = String.valueOf(c);
			}

			/**
			 * excluding ?* for wildcard search
			 */
			part = part.replaceAll("[^a-zA-Z0-9\\=\\.\\s#+?*]", "").trim();

			if(removeSpace)
			{
				part = part.replaceAll(" ", "");
			}

			return part;

		}
		else
		{
			value = value.replaceAll("[^a-zA-Z0-9\\=\\.\\s#+]", "").trim();
		}
		if(removeSpace)
		{
			value = value.replaceAll(" ", "");
		}
		return value;
	}

	public String getFullUrl(HttpServletRequest request)
	{
		final int REQ_LEN = 4000;
		return stringTruncate(request.getRequestURL().toString() + "?" + request.getQueryString(), REQ_LEN);
	}

	private String stringTruncate(String str, int len)
	{
		int limit = str.length();
		if(len < limit)
		{
			limit = len;
			return str.substring(0, limit) + "...";
		}
		return str;
	}

	public QueryResponse executeSorlQuery(SolrQuery query, SolrClient solrCore) throws SolrServerException, IOException
	{
		return solrCore.query(query, METHOD.POST);
	}

	public int getSolrDocumentsCount(QueryResponse response)
	{
		return (int) response.getResults().getNumFound();
	}

	public int calculateSolrStartingPage(Integer pageNumber, Integer pageSize, String start)
	{
		if(StringUtils.isNumeric(start))
		{
			return Integer.parseInt(start);
		}
		return calculateSolrStartingPage(pageNumber, pageSize);
	}

	public int calculateSolrStartingPage(Integer pageNumber, Integer pageSize)
	{

		return ((pageNumber - 1) * pageSize);
	}

	public String getRemoteAddress(HttpServletRequest request)
	{
		return request.getRemoteAddr();
	}

	public Integer calculateNumberOfThreads(int size, int chunk)
	{
		int threads = size / chunk;
		if(size % chunk == 0)
		{
			return threads;
		}
		return threads + 1;
	}

	// @Async
	// public Future<Map<String, SummaryDataDTO>> getPartSummaryData(List<String> comIDs, PartSearchStrategy searchStrategy, CountDownLatch latch,
	// SolrClient partsSummarySolrServer, SolrClient parametricSolrServer, SolrClient taxonomySolrServer,
	// String pageNumber, String pageSize, List<ArrowSearchStep> steps)
	// {
	// Map<String, SummaryDataDTO> result = new HashMap<>();
	// try
	// {
	// long start = System.currentTimeMillis();
	// PartDetailsSearch partDetailsSearchStrategy = (PartDetailsSearch) searchStrategy;
	// SolrQuery partsSummaryQuery = partDetailsSearchStrategy.formatePartsSummaryQuery(comIDs, pageNumber, pageSize);
	// QueryResponse response = executeSorlQuery(partsSummaryQuery, partsSummarySolrServer);
	// ArrowSearchStep step = new ArrowSearchStep("PartDetails Summary Data", partsSummaryQuery.toString(), response.getElapsedTime(),
	// System.currentTimeMillis() - start);
	// steps.add(step);
	// if(!response.getResults().isEmpty())
	// {
	// result = partDetailsSearchStrategy.fillSummaryData(response.getResults());
	// return new AsyncResult<>(result);
	// }
	// }
	// catch(Exception e)
	// {
	// logger.error("error getting summary data: ", e);
	// }
	// finally
	// {
	// latch.countDown();
	// }
	// return new AsyncResult<>(result);
	// }

	// @Async
	// public Future<Map<String, List<ArrowFeatureDTO>>> getParametricData(List<String> comIDs, PartSearchStrategy searchStrategy, CountDownLatch
	// latch, SolrClient partsSummarySolrServer, SolrClient parametricSolrServer, SolrClient taxonomySolrServer,
	// String pageNumber, String pageSize, List<ArrowSearchStep> steps, Map<String, List<ArrowFeatureDTO>> plTaxFeatures)
	// {
	// /**
	// * features holds com_id::pl_id
	// */
	// Map<String, List<ArrowFeatureDTO>> features = new HashMap<>();
	// try
	// {
	// long start = System.currentTimeMillis();
	// PartDetailsSearch partDetailsSearchStrategy = (PartDetailsSearch) searchStrategy;
	// SolrQuery parametricQuery = partDetailsSearchStrategy.formateParametricCoreQuery(comIDs, pageNumber, pageSize);
	// /**
	// * query Parametric_core with comId to get hcolnames and plid
	// */
	// QueryResponse response = executeSorlQuery(parametricQuery, parametricSolrServer);
	// ArrowSearchStep step = new ArrowSearchStep("PartDetails Pl ParametricCore", parametricQuery.toString(), response.getElapsedTime(),
	// System.currentTimeMillis() - start);
	// steps.add(step);
	// start = System.currentTimeMillis();
	// if(!response.getResults().isEmpty())
	// {
	// features = partDetailsSearchStrategy.getParametricFeatures(response.getResults(), plTaxFeatures);
	// return new AsyncResult<>(features);
	// }
	// }
	// catch(Exception e)
	// {
	// logger.error("error getting parametric data: ", e);
	// }
	// finally
	// {
	// latch.countDown();
	// }
	// return new AsyncResult<>(features);
	// }

	public Object getSolrFieldFieldData(SolrDocument d, String fieldName)
	{
		return (d.getFieldValue(fieldName) != null && d.getFieldValue(fieldName).toString().isEmpty()) ? d.getFieldValue(fieldName) : "";
	}

	public String extractHColName(String f)
	{
		return f.substring(0, f.lastIndexOf("_"));
	}

	public static void main(String[] args)
	{
		new PartSearchHelperService().extractHColName("C_2256_VALUE");
	}

	public List<PartInput> convertJsonPartsMans(String partNums) throws JSONException
	{
		logger.info("Converting string partnumber to json format");
		List<PartInput> partInputs = null;
		PartInput input = null;
		if(partNums == null || partNums.isEmpty())
		{
			return Collections.emptyList();
		}
		JSONArray jsonParts = new JSONArray(partNums);
		partInputs = new ArrayList<PartInput>(jsonParts.length());
		JSONObject request = null;
		for(int i = 0; i < jsonParts.length(); i++)
		{
			request = jsonParts.getJSONObject(i);
			input = new PartInput(request.optString("partNumber"), request.optString("manufacturer"), request.optString("comID"));
			if(!partInputs.contains(input))
			{
				partInputs.add(input);
			}
		}
		return partInputs;
	}

	public String emptyStringIfNull(Object fieldValue)
	{
		return fieldValue == null ? "" : fieldValue.toString();
	}

	public String getMatchrating(String comPartNumber, String originalPartNumber)
	{
		if(removeSpecialCharacters(comPartNumber, true).equalsIgnoreCase(removeSpecialCharacters(originalPartNumber, true)))
		{
			return "Exact";
		}
		return "Similar";
	}

	public boolean validateAgainistRegex(String input, String regex)
	{
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		return matcher.find();
	}

	public boolean validateSizeRange(int pageSize, int maxSize)
	{
		if(pageSize > maxSize)
		{
			return false;
		}
		return true;
	}

	public boolean validateWrongPageNumber(int pageNumber)
	{
		if(pageNumber < 1)
		{
			return false;
		}
		return true;
	}

	public boolean validateWrongPageSize(int pageSize)
	{
		if(pageSize < 1)
		{
			return false;
		}
		return true;
	}

	public Date stringToDate(String dateString) throws ParseException
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PartSearchServiceConstants.API_DATE_FROMAT);
		Date date = simpleDateFormat.parse(dateString);
		return date;
	}

	public boolean isDate1LessThanDate2(String from, String to) throws ParseException
	{
		boolean isBigger = true;
		Date fromDate = stringToDate(from);
		Date toDate = stringToDate(to);

		isBigger = fromDate.compareTo(toDate) < 0;
		return isBigger;
	}

	public boolean isValidDateFormat(String date)
	{
		boolean isValid = false;
		Pattern p = Pattern.compile(PartSearchServiceConstants.REGEX.REGEX_DATE_API);
		Matcher m = p.matcher(date);
		if(m.find())
		{
			isValid = true;
		}
		return isValid;
	}

	public String dateToString(Date date)
	{
		DateFormat df = new SimpleDateFormat(PartSearchServiceConstants.API_DATE_FROMAT);
		String dateText = df.format(date);

		return dateText;
	}

	public String safeString(String value)
	{
		return (value != null && !value.isEmpty()) ? value : "";
	}

	public org.json.simple.JSONObject parseResponseBodyAsJSONObject(String body, String jsonElement)
	{
		JSONParser parser = new JSONParser();
		org.json.simple.JSONObject json = null;
		try
		{
			json = (org.json.simple.JSONObject) parser.parse(body);
			return (org.json.simple.JSONObject) json.get(jsonElement);
		}
		catch(org.json.simple.parser.ParseException e)
		{
			logger.error("Error during parsing request body", e);
			return null;
		}
	}

	public org.json.simple.JSONObject parseResponseBodyAsJSONObject(String body)
	{
		JSONParser parser = new JSONParser();
		org.json.simple.JSONObject json = null;
		try
		{
			json = (org.json.simple.JSONObject) parser.parse(body);
			return json;
		}
		catch(org.json.simple.parser.ParseException e)
		{
			logger.error("Error during parsing request body", e);
			return null;
		}
	}

	public ResponseEntity<String> callRestEndpoint(HttpMethod httpMethod, URI endPoint, HttpEntity entity, RestTemplate restTemplate)
	{
		ResponseEntity<String> responseEntity = null;
		try
		{
			responseEntity = restTemplate.exchange(endPoint, httpMethod, entity, String.class);
		}
		catch(HttpClientErrorException e)
		{
			logger.error("Error calling api", e);
			if(e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
			{
				responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
			{
				responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			return responseEntity;
		}
		if(responseEntity != null)
		{
			String body = responseEntity.getBody();
			logger.info("Getting body for url:{} body:{}", endPoint, body);
			if(responseEntity.getStatusCode() == HttpStatus.OK)
			{
				return responseEntity;
			}
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public URI getTargetEndpoint(String url, Map<String, String> queryParams) throws UnsupportedEncodingException
	{
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		if(queryParams != null && !queryParams.isEmpty())
		{
			queryParams.entrySet().forEach(e -> builder.queryParam(e.getKey(), e.getValue()));

		}
		UriComponents uriComponents = builder.build();
		return uriComponents.toUri();
	}

	public String convertListToSeparatedString(List<String> names, String delimeter)
	{
		return names.stream().collect(Collectors.joining(delimeter));
	}
}
