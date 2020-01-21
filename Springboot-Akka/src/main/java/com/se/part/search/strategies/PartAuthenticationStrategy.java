package com.se.part.search.strategies;

import java.io.UnsupportedEncodingException;

import com.se.part.search.messages.PartSearchOperationMessages;

public interface PartAuthenticationStrategy
{
	PartSearchOperationMessages validate(String userName, String apiKey);

	boolean authenticate(String userName, String apiKey);

	String createToken(String userName, String apiKey) throws UnsupportedEncodingException;
}
