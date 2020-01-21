package com.se.onprem.controller;

import org.springframework.stereotype.Service;

import com.se.onprem.dto.business.bom.BOMDto;
import com.se.onprem.dto.business.bom.BOMMesssage;
import com.se.onprem.dto.business.bom.BOMRow;
import com.se.onprem.dto.ws.CustomerPart;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.dto.ws.Status;
import com.se.onprem.messages.OperationMessages;
import com.se.onprem.services.HelperService;
import com.se.onprem.strategy.LoggerStrategy;
import com.se.onprem.strategy.bom.IBOMActions;
import com.se.onprem.util.JsonHandler;

@Service
public class BomController
{

	public RestResponseWrapper deleteBom(String bomId, IBOMActions deleteAction, LoggerStrategy databaseLoggerStrategy, HelperService helperService,
			String url)
	{

		RestResponseWrapper wrapper = new RestResponseWrapper();
		BOMMesssage requestMessage = new BOMMesssage();
		requestMessage.setBomId(bomId);

		BOMMesssage message = deleteAction.doAction(requestMessage);
		if(!message.isBomDeleted())
		{
			throw new IllegalStateException("Failed to delete BOM");
		}
		wrapper.setBomDeleted(message.isBomDeleted());

		return wrapper;
	}

	public RestResponseWrapper deleteAllBoms(IBOMActions deleteAction, IBOMActions openAction, LoggerStrategy databaseLoggerStrategy,
			HelperService helperService, String url)
	{

		int page = 1;
		RestResponseWrapper listRestResponseWrapper = new RestResponseWrapper();
		RestResponseWrapper restResponseWrapper = new RestResponseWrapper();
		while(true)
		{
			listRestResponseWrapper = ListBoms(page++, openAction, databaseLoggerStrategy, helperService, url);
			if(listRestResponseWrapper.getTotalItems() == null || listRestResponseWrapper.getTotalItems() == 0)
			{
				break;
			}
			StringBuilder bomIdsBuilder = new StringBuilder("(");
			String querySeprator = "";
			for(BOMDto bomDto : listRestResponseWrapper.getBomList())
			{
				bomIdsBuilder.append(querySeprator).append(bomDto.getBomId());
				querySeprator = " ";
			}
			bomIdsBuilder.append(")");

			restResponseWrapper = deleteBom(bomIdsBuilder.toString(), deleteAction, databaseLoggerStrategy, helperService, url);

			if(!restResponseWrapper.getBomDeleted())
			{
				throw new IllegalStateException("Failed to delete BOM");
			}

		}
		return restResponseWrapper;
	}

	public RestResponseWrapper ListBoms(int page, IBOMActions openAction, LoggerStrategy databaseLoggerStrategy, HelperService helperService,
			String bomValidationURL)
	{
		RestResponseWrapper wrapper = new RestResponseWrapper();
		BOMMesssage requestMessage = new BOMMesssage();
		requestMessage.setPageNumber(page);
		BOMMesssage message = openAction.doAction(requestMessage);
		if(message.getResultCount() < 1)
		{
			return new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));
		}
		wrapper.setBomList(message.getSavedBoms());
		wrapper.setTotalItems(message.getResultCount());

		return wrapper;
	}

	public RestResponseWrapper bomData(String bomId, IBOMActions bomDataAction, LoggerStrategy databaseLoggerStrategy, HelperService helperService,
			String bomValidationURL)
	{
		RestResponseWrapper wrapper = new RestResponseWrapper();
		BOMMesssage requestMessage = new BOMMesssage();
		requestMessage.setBomId(bomId);
		BOMMesssage message = bomDataAction.doAction(requestMessage);
		if(message.getResultCount() < 1)
		{
			return new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));
		}
		if(message.getSavedBoms().size() > 0)
		{
			wrapper.setBomData(message.getSavedBoms().get(0));
		}
		wrapper.setTotalItems(message.getResultCount());

		return wrapper;
	}
}
