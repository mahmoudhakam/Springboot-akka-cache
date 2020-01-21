package com.se.onprem.util;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import com.se.onprem.util.annotations.SolrField;

@Service
public class SolrReader<T>
{

	public List<T> objectsListFromSolr(SolrDocumentList documents, Class<T> clazz)
	{
		List<T> parts = new LinkedList<>();

		documents.forEach(document -> parts.add(getObjectFromSolrDocument(document, clazz)));

		return parts;

	}

	public T getObjectFromSolrDocument(SolrDocument doc, Class<T> clazz)
	{
		T part;
		try
		{
			part = clazz.newInstance();
			for(Field field : part.getClass().getDeclaredFields())
			{
				field.setAccessible(true);
				SolrField annotation = field.getAnnotation(SolrField.class);
				if(annotation != null)
				{
					try
					{
						Object value = doc.getFieldValue(annotation.value());
						if(value != null)
						{
							if(field.getType().equals(Instant.class))
							{
								field.set(part, ((Date) value).toInstant());

							}
							else
							{
								field.set(part, value);
							}
						}
					}
					catch(IllegalArgumentException | IllegalAccessException e)
					{

						e.printStackTrace();
					}

				}
			}
			return part;
		}
		catch(InstantiationException | IllegalAccessException e1)
		{

			e1.printStackTrace();
		}

		return null;
		// return doc;
	}
}
