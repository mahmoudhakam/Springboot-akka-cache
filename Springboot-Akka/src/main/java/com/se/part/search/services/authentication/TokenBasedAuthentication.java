package com.se.part.search.services.authentication;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class TokenBasedAuthentication
{

	@Value("#{environment['part.jwt.secret']}")
	private String secret;
	@Value("#{environment['part.jwt.token.timeout']}")
	private String authTokenTimeout;

	public String createToken(String userName, String apiKey) throws UnsupportedEncodingException
	{
		String createdToken = "";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, Integer.parseInt(authTokenTimeout));
		Date endDate = cal.getTime();
		Algorithm algorithm = Algorithm.HMAC256(secret);
		String issuer = userName + apiKey;
		createdToken = JWT.create().withIssuer(issuer).withExpiresAt(endDate).sign(algorithm);
		return createdToken;
	}

	public String verifyToken(String sentToken) throws UnsupportedEncodingException
	{
		Algorithm algorithm = Algorithm.HMAC256(secret);
		JWTVerifier verifier = JWT.require(algorithm).acceptExpiresAt(1).build(); // Reusable verifier instance
		verifier.verify(sentToken);
		DecodedJWT decodedJWT = verifier.verify(sentToken);
		return decodedJWT.getIssuer();
	}
}
