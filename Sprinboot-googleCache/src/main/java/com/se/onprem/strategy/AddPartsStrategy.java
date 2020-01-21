package com.se.onprem.strategy;

import java.util.List;

import com.se.onprem.dto.ws.CustomerPart;

public interface AddPartsStrategy
{
	List<CustomerPart> addParts(String string, List<CustomerPart> inputParts);
}
