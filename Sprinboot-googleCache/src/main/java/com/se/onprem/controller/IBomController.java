package com.se.onprem.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.onprem.ConstantExcelFields;
import com.se.onprem.dto.business.bom.BOMMesssage;
import com.se.onprem.dto.business.bom.BOMRow;
import com.se.onprem.dto.ws.CustomerPart;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.dto.ws.Status;
import com.se.onprem.messages.OperationMessages;
import com.se.onprem.services.FileService;
import com.se.onprem.services.HelperService;
import com.se.onprem.strategy.LoggerStrategy;
import com.se.onprem.strategy.bom.IBOMActions;
import com.se.onprem.util.JsonHandler;
import com.se.onprem.util.UaaConstants;

@Service
public class IBomController extends BomController
{
	JsonHandler<BOMRow> bomConverter = new JsonHandler<>();
	private final DataFormatter dataFormatter = new DataFormatter();

	@Autowired
	FileService fileService;

	public RestResponseWrapper uploadExcel(MultipartFile excelFile, String excelMapping, int startRow, IBOMActions saveAction,
			LoggerStrategy databaseLoggerStrategy, HelperService helperService, String url, String token)
	{

		RestResponseWrapper wrapper = new RestResponseWrapper();

		try
		{
			Workbook workbook = WorkbookFactory.create(excelFile.getInputStream());

			Sheet sheet = workbook.getSheetAt(0);

			Map<String, BOMRow> bomMap = new LinkedHashMap<>();

			Map<String, Integer> excelMap = new ObjectMapper().readValue(excelMapping, HashMap.class);

			List<BOMRow> partsList = new ArrayList<>();

			Stack<String> pathStack = new Stack<>();

			boolean lastRow = false;
			int maxLevel = 0, minLevel = Integer.MAX_VALUE;

			Iterator<Row> rowIterator = sheet.rowIterator();
			BOMRow currentBomRow = null;
			BOMRow nextBomRow = null;
			int rowId = 1;

			while(rowIterator.hasNext() || !lastRow)
			{

				if(rowIterator.hasNext())
				{
					Row excelRow = rowIterator.next();
					if(startRow-- > 0)
					{
						continue;
					}

					nextBomRow = createBomRowFromSheetRow(excelRow, excelMap, dataFormatter, rowId++);
				}
				else
				{
					lastRow = true;
				}

				if(currentBomRow == null)
				{
					currentBomRow = nextBomRow;
					currentBomRow.setHasChild(1);
				}

				if(nextBomRow.getLevel() > currentBomRow.getLevel())
					currentBomRow.setHasChild(1);

				while(pathStack.size() >= currentBomRow.getLevel())
					pathStack.pop();

				StringBuilder pathStringBuilder = new StringBuilder("");

				String pathSeprator = "";
				for(String item : pathStack)
				{
					pathStringBuilder.append(pathSeprator).append(item);
					pathSeprator = "->";
				}

				currentBomRow.setPath(pathStringBuilder.toString());

				maxLevel = Integer.max(maxLevel, currentBomRow.getLevel());
				minLevel = Integer.min(minLevel, currentBomRow.getLevel());

				pathStack.push(currentBomRow.getId());

				bomMap.put(currentBomRow.getPath() + "->" + currentBomRow.getRowKey(), currentBomRow);

				if(StringUtils.isNotEmpty(currentBomRow.getUploadedMpn()))
				{

					partsList.add(currentBomRow);
				}
				currentBomRow = nextBomRow;
			}
			workbook.close();

			String partsListjson = bomConverter.convertListToJon(partsList);

			List<BOMRow> bomResults = validateBOM(helperService, url, partsListjson, token);

			BOMMesssage requestMessage = new BOMMesssage();

			if(bomResults != null)
			{
				for(BOMRow bomRow : bomResults)
				{
					bomMap.put(bomRow.getPath() + "->" + bomRow.getRowKey(), bomRow);
				}
			}

			requestMessage.setBomParts(new ArrayList<BOMRow>(bomMap.values()));
			requestMessage.setBomName(excelFile.getOriginalFilename());
			requestMessage.setIndented(1);
			requestMessage.setMaxLevel(maxLevel);
			requestMessage.setMinLevel(minLevel);

			BOMMesssage message = saveAction.doAction(requestMessage);
			wrapper.setBomSaveResult(message.getBomSaveResult());

		}
		catch(EncryptedDocumentException | InvalidFormatException | IOException e)
		{

			e.printStackTrace();
		}

		return wrapper;
	}

	private List<BOMRow> validateBOM(HelperService helperService, String url, String partsListjson, String token) throws UnsupportedEncodingException
	{
		LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("bomData", partsListjson);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set(UaaConstants.AUTHORIZATION, token);

		RestResponseWrapper validationWrapper = helperService.getResposneFromURL(params, headers, url);
		List<BOMRow> bomResults = validationWrapper.getBomResult();
		return bomResults;
	}

	private BOMRow createBomRowFromSheetRow(Row row, Map<String, Integer> excelMap, DataFormatter dataFormatter, int rowId)
	{
		BOMRow bomRow = new BOMRow();
		bomRow.setRowId(rowId);

		if(excelMap.get(ConstantExcelFields.ID) != null)
		{
			bomRow.setRowKey(rowId + "|" + dataFormatter.formatCellValue(row.getCell(excelMap.get(ConstantExcelFields.ID))));

		}
		if(excelMap.get(ConstantExcelFields.LEVEL) != null)
		{
			bomRow.setLevel(Integer.valueOf(dataFormatter.formatCellValue(row.getCell(excelMap.get(ConstantExcelFields.LEVEL)))));
		}

		if(excelMap.get(ConstantExcelFields.ID) != null)
		{
			bomRow.setId(dataFormatter.formatCellValue(row.getCell(excelMap.get(ConstantExcelFields.ID))));
		}

		if(excelMap.get(ConstantExcelFields.UPLOADEDMANUFACTURER) != null)
		{
			bomRow.setUploadedManufacturer(dataFormatter.formatCellValue(row.getCell(excelMap.get(ConstantExcelFields.UPLOADEDMANUFACTURER))));
		}
		if(excelMap.get(ConstantExcelFields.UPLOADEDMPN) != null)
		{
			bomRow.setUploadedMpn(dataFormatter.formatCellValue(row.getCell(excelMap.get(ConstantExcelFields.UPLOADEDMPN))));
		}
		if(excelMap.get(ConstantExcelFields.PARENTID) != null)
		{
			bomRow.setParentId(dataFormatter.formatCellValue(row.getCell(excelMap.get(ConstantExcelFields.PARENTID))));
		}
		if(excelMap.get(ConstantExcelFields.PL_ID) != null)
		{
			bomRow.setPlId(dataFormatter.formatCellValue(row.getCell(excelMap.get(ConstantExcelFields.PL_ID))));
		}
		if(excelMap.get(ConstantExcelFields.PART_NUMBER) != null)
		{
			bomRow.setPartNumber(dataFormatter.formatCellValue(row.getCell(excelMap.get(ConstantExcelFields.PART_NUMBER))));
		}
		if(excelMap.get(ConstantExcelFields.MANUFACTURER) != null)
		{
			bomRow.setManufacturer(dataFormatter.formatCellValue(row.getCell(excelMap.get(ConstantExcelFields.MANUFACTURER))));
		}
		if(excelMap.get(ConstantExcelFields.MATCH_STATUS) != null)
		{
			bomRow.setMatchStatus(dataFormatter.formatCellValue(row.getCell(excelMap.get(ConstantExcelFields.MATCH_STATUS))));
		}
		return bomRow;
	}

	public RestResponseWrapper openBom(String bomId, String levels, String paths, String ignoredPaths, int pageSize, int page, IBOMActions openAction,
			LoggerStrategy databaseLoggerStrategy, HelperService helperService, String bomValidationURL, String token)
	{
		RestResponseWrapper wrapper = new RestResponseWrapper();
		BOMMesssage requestMessage = BOMMesssage.builder().bomId(bomId).pageNumber(page).levels(levels).paths(paths).ignoredPaths(ignoredPaths)
				.pageSize(pageSize).token(token).build();
		BOMMesssage message = openAction.doAction(requestMessage);
		if(message.getResultCount() < 1 || message.getBomParts().isEmpty())
		{
			return new RestResponseWrapper(new Status(OperationMessages.NO_RESULT_FOUND, false));
		}
		wrapper.setBomResult(message.getBomParts());
		wrapper.setTotalItems(message.getResultCount());

		return wrapper;
	}

}
