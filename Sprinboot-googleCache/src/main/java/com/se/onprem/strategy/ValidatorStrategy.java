package com.se.onprem.strategy;

import org.springframework.stereotype.Service;

@Service
public interface ValidatorStrategy
{
boolean validateInput(String input);
}
