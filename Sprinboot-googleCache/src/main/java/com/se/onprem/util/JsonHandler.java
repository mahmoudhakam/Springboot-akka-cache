package com.se.onprem.util;

import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHandler<T>
{

	public T convertJSONToObjectV2(String json, Class<T> clazz)
	{
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.readValue(json, clazz);
		}catch(Exception e)
		{
			System.out.println("Error inside method [convertJSONToObject]");
			e.printStackTrace();
			// logger.error("Error inside method [convertJSONToObject]", e);
			return null;
		}
	}
	public List<T> convertJSONToList(String json, Class<T> clazz) {
		try{
			ObjectMapper mapper = new ObjectMapper();

			List<T> fetList = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
			return fetList;
		} catch (Exception e){
			return null;
		}
	}
	public String convertListToJon(List<T> list) {
		try{
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(list);
			return json;
		} catch (Exception e){
			return null;
		}
	}
}
