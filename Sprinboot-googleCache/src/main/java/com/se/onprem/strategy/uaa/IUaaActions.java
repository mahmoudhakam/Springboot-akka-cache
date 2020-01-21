package com.se.onprem.strategy.uaa;

import com.se.onprem.dto.uaa.UaaMessage;
import com.se.onprem.dto.ws.RestResponseWrapper;

public interface IUaaActions
{
	RestResponseWrapper doAction(UaaMessage message);

}
