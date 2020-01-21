package com.se.part.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
//@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableAsync
@EnableWebMvc
@ComponentScans(value = { @ComponentScan("com.se") }) // do not change to scan dependent projects
public class PartSearchApplication extends SpringBootServletInitializer
{

	public static void main(String[] args)
	{
		SpringApplication.run(PartSearchApplication.class, args);
	}

}
