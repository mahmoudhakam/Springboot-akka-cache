package com.se.onprem.configuration;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.concurrent.Executor;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.se.onprem.util.ParametricConstants;

@Configuration
@PropertySource(value = { "file:C:\\session\\custom-serverice.properties", "file:/home/dev1/tc/session/custom-serverice.properties" }, ignoreResourceNotFound = true)
public class APIConfiguration implements AsyncConfigurer
{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private Environment env;

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean("bomValidationURL")
	public String bomValidationURL() {
		return env.getProperty("bom.validation.url", "http://10.68.24.201:8072/SE-SearchAPI/validateBOM");
	}
	@Bean(name = "aclCore")
	public SolrClient getAclCore() throws MalformedURLException
	{
		SolrClient parametricSolrServer = new HttpSolrClient(env.getProperty(ParametricConstants.ACL_CORE_URL));
		return parametricSolrServer;
	}
	@Bean(name = "facetMapServiceURL")
	public String facetMapServiceURL() throws MalformedURLException
	{
		return env.getProperty("facetmap.service.url", "http://localhost:8072/SE-SearchAPI/facetDistribution");
	}
	
	@Bean(name = "bomCore")
	public SolrClient getBomCore() throws MalformedURLException
	{
		SolrClient parametricSolrServer = new HttpSolrClient(env.getProperty(ParametricConstants.BOM_CORE_URL));
		return parametricSolrServer;
	}
	@Bean(name = "bomPartsCore")
	public SolrClient getBOMPartsCore() throws MalformedURLException
	{
		SolrClient parametricSolrServer = new HttpSolrClient(env.getProperty(ParametricConstants.BOM_PARTS_CORE_URL));
		return parametricSolrServer;
	}

    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory()
    {
        JettyEmbeddedServletContainerFactory jettyContainer = new JettyEmbeddedServletContainerFactory();
//        jettyContainer.setPort(9000);
//        jettyContainer.setContextPath("/SE-SearchAPI");
        return jettyContainer;
    }


	@Bean(name = "httpClientRequestFactory")
	HttpComponentsClientHttpRequestFactory factory()
	{
		return new HttpComponentsClientHttpRequestFactory();
	}

	@Bean(name = "restTemplate")
	RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory httpClientRequestFactory)
	{
		RestTemplate restTemplate = new RestTemplate(httpClientRequestFactory);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		return restTemplate;
	}

	@Bean(name = "asyncExecutor")
	@Override
	public Executor getAsyncExecutor()
	{
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(Integer.parseInt(env.getProperty(ParametricConstants.AsyncConfig.MAX_NUMBER_OF_THREADS)));
		taskExecutor.setCorePoolSize(Integer.parseInt(env.getProperty(ParametricConstants.AsyncConfig.NUMBER_OF_THREADS)));
		taskExecutor.setThreadNamePrefix("ParaAsync-");
		taskExecutor.initialize();

		return taskExecutor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler()
	{
		return new AsyncUncaughtExceptionHandler() {

			@Override
			public void handleUncaughtException(Throwable ex, Method method, Object... params)
			{
				logger.error("Method: " + method.getName() + ", Parameters: " + Arrays.toString(params), ex);
			}

		};
	}
}
