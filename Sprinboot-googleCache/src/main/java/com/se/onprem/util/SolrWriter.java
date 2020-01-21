package com.se.onprem.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import com.se.onprem.util.annotations.SolrField;

@Service
public class SolrWriter<T>
{

	public boolean indexParts(List<T> parts, SolrClient solrServer)
	{
		List<SolrInputDocument> docs = new ArrayList<>();
		for(T part : parts)
		{
			SolrInputDocument doc = getSolrDocumentFromPartDTO(part);
			docs.add(doc);

		}
		try
		{
			solrServer.add(docs);
			solrServer.commit();
			return true;
		}
		catch(SolrServerException | IOException e)
		{

			e.printStackTrace();
			return false;
		}
	}

	private SolrInputDocument getSolrDocumentFromPartDTO(T part)
	{
		SolrInputDocument doc = new SolrInputDocument();
		for(Field field : part.getClass().getDeclaredFields())
		{
			field.setAccessible(true);
			SolrField annotation = field.getAnnotation(SolrField.class);
			if(annotation != null)
			{
				try
				{
					Object value = field.get(part);
					if(value!=null)
					doc.addField(annotation.value(), value.toString());
				}
				catch(IllegalArgumentException | IllegalAccessException e)
				{

					e.printStackTrace();
				}
			}
		}
		return doc;
	}
}
