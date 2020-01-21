package com.se.onprem.strategy;

import org.springframework.stereotype.Service;

import com.se.onprem.dto.ws.LogDTO;

@Service
public interface LoggerStrategy
{
void logRequest(LogDTO log);
}
