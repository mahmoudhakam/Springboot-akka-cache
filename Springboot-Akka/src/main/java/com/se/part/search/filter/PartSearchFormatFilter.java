package com.se.part.search.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.se.part.search.util.PartSearchServiceConstants;

/***
 * @author Mahmoud_Abdelhakam
 */

@Component
@Order(10) // higher number runs first
public class PartSearchFormatFilter implements Filter
{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void destroy()
	{
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse respose, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) respose;
		String format = req.getParameter(PartSearchServiceConstants.REQUEST_FORMAT);
		String acceptedHeader = MediaType.APPLICATION_JSON_VALUE;
		if(format != null && format.equals(PartSearchServiceConstants.XML_FORMAT))
		{
			acceptedHeader = MediaType.APPLICATION_XML_VALUE;
		}
		HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(req);
		requestWrapper.addHeader(PartSearchServiceConstants.ACCEPT_HEADER, acceptedHeader);
		chain.doFilter(requestWrapper, resp);
		if(format != null && format.equals(PartSearchServiceConstants.XML_FORMAT))
		{
			resp.setContentType(MediaType.APPLICATION_XML_VALUE);
		}
		else
		{
			resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException
	{
	}

}
