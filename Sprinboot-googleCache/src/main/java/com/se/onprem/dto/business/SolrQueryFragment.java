package com.se.onprem.dto.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SolrQueryFragment
{
	private StringBuilder query;

	public SolrQueryFragment()
	{
		query = new StringBuilder("");
	}

	public SolrQueryFragment(String query)
	{
		this.query = new StringBuilder(query);
	}

	public SolrQueryFragment(String field, String value)
	{
		this.query = new StringBuilder(constructQuery(field, value, true));
	}

	public StringBuilder constructQuery(String field, String value, boolean quotes)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(field).append(":");

		if (quotes)
			builder.append("\"");

		builder.append(value);

		if (quotes)
			builder.append("\"");

		return builder;
	}

	public SolrQueryFragment appendQuery(String field, String value, String operator, boolean quotes)
	{
		if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(value))
		{
			if (StringUtils.isEmpty(query))
			{
				query = constructQuery(field, value, quotes);
			}
			else
			{
				query.append(" ");
			}

			if (StringUtils.isNotEmpty(operator))
			{
				query.append(operator).append(" ").append(constructQuery(field, value, quotes));
			}
		}

		return this;
	}

	public SolrQueryFragment appendList(Collection<String> collection, String field, String operator)
	{
		StringBuilder valueBuilder = new StringBuilder("");

		if (collection != null && !collection.isEmpty())
		{
			valueBuilder.append("(");

			collection.forEach(item -> {
				valueBuilder.append("\"").append(item).append("\"").append(" ");
			});

			valueBuilder.deleteCharAt(valueBuilder.lastIndexOf(" ")).append(")");
		}
		return appendQuery(field, valueBuilder.toString(), operator, false);
	}

	public String query()
	{
		return query.toString();
	}

	

	@Override
	public String toString()
	{
		return "query=" + query;
	}

}
