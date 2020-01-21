package com.se.part.search.configuration;

import static com.se.part.search.configuration.SpringExtension.SPRING_EXTENSION_PROVIDER;

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
import org.springframework.context.ApplicationContext;
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

import com.se.part.search.dto.keyword.Constants;
import com.se.part.search.util.PartSearchServiceConstants;

import akka.actor.ActorSystem;

@Configuration
@PropertySource(value = { "file:C:\\session\\SEOnPremPartSearch.properties", "file:/home/dev1/tc/session/SEOnPremPartSearch.properties" }, ignoreResourceNotFound = true)
public class PartSearchServiceConfiguration implements AsyncConfigurer
{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private Environment env;

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public ActorSystem actorSystem()
	{
		ActorSystem system = ActorSystem.create("akka-spring");
		SPRING_EXTENSION_PROVIDER.get(system).initialize(applicationContext);
		return system;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean(name = "taxonomySolrServer")
	public SolrClient getTaxonomySolrServer() throws MalformedURLException
	{
		SolrClient taxonomySolrServer = new HttpSolrClient(env.getProperty(Constants.SOLR_TAXONOMY_CORE_URL));
		return taxonomySolrServer;
	}

	@Bean(name = "partsSummarySolrServer")
	public SolrClient getPartsSummarySolrServer()
	{
		return new HttpSolrClient(env.getProperty(PartSearchServiceConstants.PARTS_CORE));
	}

	@Bean(name = "passiveSolrServer")
	public SolrClient getPassiveSolrServer()
	{
		return new HttpSolrClient(env.getProperty(PartSearchServiceConstants.PARTS_PASSIVE_CORE));
	}

	@Bean(name = "lookupSolrServer")
	public SolrClient getlookupSolrServer()
	{
		return new HttpSolrClient(env.getProperty(PartSearchServiceConstants.PARTS_LOOKUP_CORE));
	}

	@Bean(name = "manBasicSolrServer")
	public SolrClient getManBasicSolrServer() throws MalformedURLException
	{
		SolrClient partsSolrServer = new HttpSolrClient(env.getProperty(Constants.SOLR_MAN_BASIC_PARTS_CORE_URL));
		return partsSolrServer;
	}

	@Bean(name = "descSolrServer")
	public SolrClient getDescSolrServer() throws MalformedURLException
	{
		SolrClient partsSolrServer = new HttpSolrClient(env.getProperty(Constants.SOLR_DESC_CORE_URL));
		return partsSolrServer;
	}

	@Bean
	public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory()
	{
		JettyEmbeddedServletContainerFactory jettyContainer = new JettyEmbeddedServletContainerFactory();
		// jettyContainer.setPort(9000);
		// jettyContainer.setContextPath("/SE-SearchAPI");
		return jettyContainer;
	}

	@Bean(name = "manSolrServer")
	public SolrClient getManSolrServer()
	{
		return new HttpSolrClient(env.getProperty(PartSearchServiceConstants.MAN_CORE));
	}

	// @Bean(name = "alertSolrServer")
	// public SolrClient getAlertSolrServer()
	// {
	// return new HttpSolrClient(env.getProperty(PartSearchServiceConstants.ALERT_CORE));
	// }

	@Bean(name = "asyncExecutor")
	@Override
	public Executor getAsyncExecutor()
	{
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(Integer.parseInt(env.getProperty(PartSearchServiceConstants.MAX_NUMBER_OF_THREADS)));
		taskExecutor.setCorePoolSize(Integer.parseInt(env.getProperty(PartSearchServiceConstants.NUMBER_OF_THREADS)));
		taskExecutor.setThreadNamePrefix("ArrowAsync-");
		taskExecutor.initialize();

		return taskExecutor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler()
	{
		return (ex, method, params) -> logger.error("Method:{} , Parameters:{} ", method.getName(), Arrays.toString(params), ex);
	}

	@Bean(name = "httpClientRequestFactory")
	HttpComponentsClientHttpRequestFactory factory()
	{
		return new HttpComponentsClientHttpRequestFactory();
	}

	// @Bean(name = "ctx")
	// AnnotationConfigApplicationContext ctx()
	// {
	// AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
	// Map<String, Object> systemEnvVariables = systemENV.getSystemEnvironment();
	// logger.info("System environemt variables={}", systemEnvVariables.toString());
	// String profile = systemEnvVariables.get("machine_name").toString();
	// ctx.getEnvironment().setActiveProfiles(profile);
	// ctx.refresh();
	// return ctx;
	// }

	@Bean(name = "restTemplate")
	RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory httpClientRequestFactory)
	{
		RestTemplate restTemplate = new RestTemplate(httpClientRequestFactory);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		return restTemplate;
	}
}
