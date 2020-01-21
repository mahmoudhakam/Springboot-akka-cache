package com.se.onprem.strategy.uaa;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.se.onprem.dto.uaa.UaaMessage;
import com.se.onprem.dto.uaa.UaaResponse;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.dto.uaa.User;
import com.se.onprem.services.UaaHelperService;
import com.se.onprem.util.JsonHandler;
import com.se.onprem.util.UaaConstants;

@Service
public class UserAuthenticate implements IUaaActions
{

	@Autowired
	UaaHelperService uaaHelperService;

	@Autowired
	private Environment env;

	JsonHandler<RestResponseWrapper> RestResponseWrapperJsonConverter = new JsonHandler<>();
	JsonHandler<User> UserJsonConverter = new JsonHandler<>();

	@SuppressWarnings("unchecked")
	@Override
	public RestResponseWrapper doAction(UaaMessage message)
	{

		RestResponseWrapper resultWrapper = new RestResponseWrapper();
		try
		{
			String url = env.getProperty(UaaConstants.AUTHENTICATION_API_URL);

			User user = message.getUser();

			JSONObject params = new JSONObject();

			params.put(UaaConstants.USER_NAME, user.getUsername());
			params.put(UaaConstants.PASSWORD, user.getPassword());

			UaaResponse uaaResponse = uaaHelperService.sendRequest(url, params.toString(), null, HttpMethod.POST);

			if(uaaResponse.getHttpStatus().equals(HttpStatus.OK))
			{
				resultWrapper = RestResponseWrapperJsonConverter.convertJSONToObjectV2(uaaResponse.getJsonResponse(),
						RestResponseWrapper.class);
				
				resultWrapper.setToken(UaaConstants.BEARER + resultWrapper.getToken());

				url = env.getProperty(UaaConstants.USER_DATA_API_URL);

				uaaResponse = uaaHelperService.sendRequest(url, null, resultWrapper.getToken(), HttpMethod.GET);

				if(uaaResponse.getHttpStatus().equals(HttpStatus.OK))
				{
					user = UserJsonConverter.convertJSONToObjectV2(uaaResponse.getJsonResponse(), User.class);

				}

				resultWrapper.setUser(user);

			}
			resultWrapper.setHttpStatus(uaaResponse.getHttpStatus());

		}
		catch(Exception e)
		{

			e.printStackTrace();
		}

		return resultWrapper;

	}

}
