package com.se.onprem;

import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.se.onprem.controller.IBomController;
import com.se.onprem.controller.NormalBomController;
import com.se.onprem.dto.business.bom.BomRequestParameters;
import com.se.onprem.dto.ws.RestResponseWrapper;
import com.se.onprem.dto.ws.Status;
import com.se.onprem.messages.OperationMessages;
import com.se.onprem.services.HelperService;
import com.se.onprem.strategy.LoggerStrategy;
import com.se.onprem.strategy.bom.BOMData;
import com.se.onprem.strategy.bom.BOMExport;
import com.se.onprem.strategy.bom.BOMIndentedOpen;
import com.se.onprem.strategy.bom.BOMList;
import com.se.onprem.strategy.bom.BOMOpen;
import com.se.onprem.strategy.bom.BomDelete;
import com.se.onprem.strategy.bom.BomSave;
import com.se.onprem.strategy.bom.IBOMActions;
import com.se.onprem.util.UaaConstants;

@RestController
@CrossOrigin(maxAge = 3600)
public class BomAPIEndPoint
{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private NormalBomController bomController;
	private IBomController iBomController;
	private HelperService helperService;
	private LoggerStrategy databaseLoggerStrategy;
	private IBOMActions saveAction;
	private IBOMActions openAction;
	private IBOMActions listBomAction;
	private IBOMActions openIndentedAction;
	private IBOMActions deleteAction;
	private IBOMActions bomDataAction;
	private IBOMActions bomExportAction;

	private String bomValidationURL;

	@Autowired
	public BomAPIEndPoint(NormalBomController bomController, IBomController iBomController, HelperService helperService, BomSave saveAction,
			BOMOpen openAction, BOMList listBomAction, String bomValidationURL, BOMIndentedOpen openIndentedAction, BomDelete deleteAction,
			BOMData bomDataAction, BOMExport bomExportAction)
	{

		super();
		this.bomController = bomController;
		this.iBomController = iBomController;
		this.helperService = helperService;
		this.saveAction = saveAction;
		this.openAction = openAction;
		this.listBomAction = listBomAction;
		this.openIndentedAction = openIndentedAction;
		this.deleteAction = deleteAction;
		this.bomDataAction = bomDataAction;
		this.bomExportAction = bomExportAction;
		this.bomValidationURL = bomValidationURL;
	}

	@RequestMapping(value = "/validateBOM", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> validateBOM(@RequestParam(name = "bomData", defaultValue = "") String bomData,
			@RequestParam(name = "bomName", defaultValue = "") String bomName, @RequestParam(name = "bomId", defaultValue = "") String bomId,
			@RequestParam(name = "rowId", defaultValue = "0") int rowId, HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = bomController.saveBom(new BomRequestParameters(bomData, bomName, bomId, rowId), saveAction,
					databaseLoggerStrategy, helperService, bomValidationURL, request.getHeader(UaaConstants.AUTHORIZATION));

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [validateBOM] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/openBOM", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> openBOM(@RequestParam(name = "bomId", defaultValue = "") String bomId,
			@RequestParam(name = "filters", defaultValue = "") String filters,
			@RequestParam(name = "matchFilters", defaultValue = "") String matchFilters,
			@RequestParam(name = "lastFilter", defaultValue = "") String lastFilter,
			@RequestParam(name = "sortField", defaultValue = "") String sortField,
			@RequestParam(name = "sortType", defaultValue = "") String sortType, @RequestParam(name = "page", defaultValue = "1") int page,
			HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = bomController.openBom(bomId, filters, matchFilters, lastFilter.trim(), sortField, sortType,
					page, openAction, databaseLoggerStrategy, helperService, bomValidationURL, request.getHeader(UaaConstants.AUTHORIZATION));

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [openBOM] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/bomList", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> listBOMs(@RequestParam(name = "page", defaultValue = "1") int page, HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = bomController.ListBoms(page, listBomAction, databaseLoggerStrategy, helperService,
					bomValidationURL);

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [bomList] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/saveExcelAsBOM", method = { RequestMethod.POST }, consumes = { "multipart/form-data" }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> saveExcelAsBOM(@RequestParam(name = "excelFile") MultipartFile excelFile,
			@RequestParam(name = "excelMapping", defaultValue = "") String excelMapping,
			@RequestParam(name = "startRow", defaultValue = "0") int startRow, HttpServletRequest request)
	{

		try
		{
			RestResponseWrapper restResponseWrapper = iBomController.uploadExcel(excelFile, excelMapping, startRow, saveAction,
					databaseLoggerStrategy, helperService, bomValidationURL, request.getHeader(UaaConstants.AUTHORIZATION));

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [saveExcelAsBOM] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/openIndentedBOM", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> openIndentedBOM(@RequestParam(name = "bomId", defaultValue = "") String bomId,
			@RequestParam(name = "levels", defaultValue = "") String levels, @RequestParam(name = "paths", defaultValue = "") String paths,
			@RequestParam(name = "ignoredPaths", defaultValue = "") String ignoredPaths, @RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "pageSize", defaultValue = "1") int pageSize, HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = iBomController.openBom(bomId, levels, paths, ignoredPaths, pageSize, page, openIndentedAction,
					databaseLoggerStrategy, helperService, bomValidationURL, request.getHeader(UaaConstants.AUTHORIZATION));

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [openIndentedBOM] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/deleteBom", method = { RequestMethod.POST, RequestMethod.GET }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> deleteBom(@RequestParam(name = "bomId", defaultValue = "") String bomId, HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = bomController.deleteBom(bomId, deleteAction, databaseLoggerStrategy, helperService,
					bomValidationURL);

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [deleteBom] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/deleteAllBoms", method = { RequestMethod.POST, RequestMethod.GET }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> deleteAllBoms(HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = bomController.deleteAllBoms(deleteAction, listBomAction, databaseLoggerStrategy, helperService,
					bomValidationURL);
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [deleteAllBoms] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/replaceBOMPart", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> replaceBOMPart(@RequestParam(name = "bomData", defaultValue = "") String bomData,
			@RequestParam(name = "bomId", defaultValue = "") String bomId, @RequestParam(name = "rowId", defaultValue = "0") int rowId,
			HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = bomController.replaceBOMPart(bomData, bomId, rowId, saveAction, databaseLoggerStrategy,
					helperService, bomValidationURL, request.getHeader(UaaConstants.AUTHORIZATION));

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [replaceBOMPart] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/bomData", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RestResponseWrapper> bomData(@RequestParam(name = "bomId", defaultValue = "") String bomId, HttpServletRequest request)
	{
		try
		{
			RestResponseWrapper restResponseWrapper = bomController.bomData(bomId, bomDataAction, databaseLoggerStrategy, helperService,
					bomValidationURL);

			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.OK);

		}
		catch(Exception e)
		{
			logger.error("Error During getting data [bomData] ", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/bomExport", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> bomExport(@RequestParam(name = "bomId", defaultValue = "") String bomId,
			@RequestParam(name = "columns", defaultValue = "") String columns,
			@RequestParam(name = "matchStatus", defaultValue = "") String matchStatus, HttpServletRequest request)
	{

		try
		{
			ZonedDateTime start = ZonedDateTime.now();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			RestResponseWrapper wrapper = bomController.bomExport(bomId, columns, matchStatus, bomExportAction, databaseLoggerStrategy, helperService,
					bomValidationURL, request.getHeader(UaaConstants.AUTHORIZATION));

			wrapper.getExcelFile().write(bos);
			bos.close();

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION,
					String.format("attachment; filename=%s", wrapper.getBomData().getBomName().replace(" ", "_") + ".xlsx"));
			MediaType mediaType = new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			ZonedDateTime end = ZonedDateTime.now();
			logger.info("The exported excel took {} milli seconds" + ChronoUnit.MILLIS.between(start, end));

			return ResponseEntity.ok().headers(headers).contentType(mediaType).body(bos.toByteArray());

		}
		catch(Exception e)
		{
			logger.error("Error During Export BOM Excel", e);
			RestResponseWrapper restResponseWrapper = new RestResponseWrapper(new Status(OperationMessages.FAILED_OPERATION, false));
			return new ResponseEntity<RestResponseWrapper>(restResponseWrapper, HttpStatus.UNAUTHORIZED);
		}

	}

}
