package com.se.onprem.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogDTO
{
	String partNumber;
	String user;
	String request;
	String status;
	long responseTime;
	int resultCount;
}
