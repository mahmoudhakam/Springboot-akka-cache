package com.se.onprem;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.se.onprem.controller.UaaController;
import com.se.onprem.dto.uaa.User;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.dto.ws.Status;
import com.se.onprem.messages.OperationMessages;
import com.se.onprem.strategy.uaa.IUaaActions;
import com.se.onprem.strategy.uaa.UserAuthenticate;

@RestController
@CrossOrigin(maxAge = 3600)
public class UaaAPIEndPoint
{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private UaaController uaaController;
	private IUaaActions authenticateAction;

	@Autowired
	public UaaAPIEndPoint(UaaController uaaController, UserAuthenticate authenticateAction)
	{
		super();
		this.uaaController = uaaController;
		this.authenticateAction = authenticateAction;
	}

	@RequestMapping(value = "/authenticate", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> authenticate(@RequestParam(name = "username", defaultValue = "") String username,
			@RequestParam(name = "password", defaultValue = "") String password, HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = uaaController.authenticate(User.builder().username(username).password(password).build(),
					authenticateAction);

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, restResponseWrapper.getHttpStatus());

		}
		catch(Exception e)
		{
			logger.error("Error During authentication ", e);
			RestResponseWrapper restResponseWrapper = RestResponseWrapper.builder().status(new Status(OperationMessages.FAILED_OPERATION, false))
					.build();
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.BAD_REQUEST);
		}
	}

}
