package com.se.onprem.controller;

import org.springframework.stereotype.Service;

import com.se.onprem.dto.uaa.UaaMessage;
import com.se.onprem.dto.uaa.User;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.strategy.uaa.IUaaActions;

@Service
public class UaaController
{

	public RestResponseWrapper authenticate(User user, IUaaActions authenticateAction)
	{

		RestResponseWrapper wrapper = new RestResponseWrapper();
		UaaMessage requestMessage = UaaMessage.builder().user(user).build();

		wrapper = authenticateAction.doAction(requestMessage);

		return wrapper;
	}

}
