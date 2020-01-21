package com.se.part.search.services.keywordSearch;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHandler<T>
{

	public List<T> convertJSONToList(String json, Class<T> clazz)
	{
		try
		{
			ObjectMapper mapper = new ObjectMapper();

			List<T> fetList = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
			return fetList;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public String convertListToJon(List<T> list)
	{
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			String json = mapper.writeValueAsString(list);
			return json;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public T convertJSONToObject(String json, Class<T> clazz)
	{
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			T fetList = mapper.readValue(json, clazz);
			return fetList;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public String convertObjectToJon(T object)
	{
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			String json = mapper.writeValueAsString(object);
			return json;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public T convertToValue(T object, Class<T> clazz)
	{
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			T result = mapper.convertValue(object, clazz);
			return result;
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
