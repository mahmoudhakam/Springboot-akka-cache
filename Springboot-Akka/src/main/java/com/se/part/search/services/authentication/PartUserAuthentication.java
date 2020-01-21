package com.se.part.search.services.authentication;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.se.part.search.messages.PartSearchOperationMessages;
import com.se.part.search.strategies.PartAuthenticationStrategy;

@Service
public class PartUserAuthentication implements PartAuthenticationStrategy
{

	private TokenBasedAuthentication tokenBasedAuthentication;

	@Value("#{environment['part.userName']}")
	private String userName;
	@Value("#{environment['part.password']}")
	private String password;

	@Autowired
	public PartUserAuthentication(TokenBasedAuthentication tokenBasedAuthentication)
	{
		this.tokenBasedAuthentication = tokenBasedAuthentication;
	}

	@Override
	public PartSearchOperationMessages validate(String userName, String apiKey)
	{
		if(userName.isEmpty() || apiKey.isEmpty())
		{
			return PartSearchOperationMessages.MISSING_USERNAME_OR_APIKEY;
		}
		return PartSearchOperationMessages.SUCCESSFULL_OPERATION;
	}

	@Override
	public boolean authenticate(String userName, String apiKey)
	{
		return (userName.equals(this.userName) && apiKey.equals(this.password));
	}

	@Override
	public String createToken(String userName, String apiKey) throws UnsupportedEncodingException
	{
		return tokenBasedAuthentication.createToken(userName, apiKey);
	}

}
