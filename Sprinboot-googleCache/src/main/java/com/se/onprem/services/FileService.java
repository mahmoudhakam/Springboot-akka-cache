package com.se.onprem.services;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.se.onprem.dto.business.bom.BOMMesssage;
import com.se.onprem.util.ParametricConstants;

@Service
public class FileService
{

	@Autowired
	private Environment env;

	private int startingRowIndex = 5;

	public Workbook createExcelForDownlaod(Map<String, Map<String, String>> excelRows, List<String> excelColumns, BOMMesssage message)
	{

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Export Report");

		sheet.setDefaultColumnWidth(40);

		drawExcelHeader("BOM Export Data", workbook, sheet, message.getBomName(), "userName");

		drawDownloadHeader(workbook, sheet, excelColumns);

		CellStyle dataCellStyle = getDataCellStyle(workbook);

		int rowIndex = 1 + startingRowIndex;
		for(Map.Entry<String, Map<String, String>> entry : excelRows.entrySet())
		{
			Row dataRow = sheet.createRow(rowIndex);
			int columnIndex = 0;
			for(Map.Entry<String, String> cellValue : entry.getValue().entrySet())
			{
				Cell dataCell = dataRow.createCell(columnIndex);
				dataCell.setCellValue(cellValue.getValue());
				dataCell.setCellStyle(dataCellStyle);
				columnIndex++;
			}
			rowIndex++;
		}
		return workbook;
	}

	private void drawExcelHeader(String ExcelHeaderTitle, Workbook workbook, Sheet sheet, String bomName, String userName)
	{

		drawCompanyLogoInWorkbook(workbook, sheet);

		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setFontHeightInPoints((short) 16);
		headerFont.setBold(true);

		// Create a CellStyle with the font
		CellStyle excelHeaderTitleCellStyle = workbook.createCellStyle();
		excelHeaderTitleCellStyle.setFont(headerFont);
		excelHeaderTitleCellStyle.setAlignment(HorizontalAlignment.CENTER);

		excelHeaderTitleCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

		excelHeaderTitleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		Row heaterTitleRow = sheet.createRow(1);
		Cell heaterTitleCell = heaterTitleRow.createCell(2);
		heaterTitleCell.setCellValue(ExcelHeaderTitle);

		heaterTitleCell.setCellStyle(excelHeaderTitleCellStyle);

		Row usernameAndDateRow = sheet.createRow(2);
		Cell usernameCell = usernameAndDateRow.createCell(2);
		usernameCell.setCellValue("User Name : " + userName);

		Cell dateCell = usernameAndDateRow.createCell(3);
		dateCell.setCellValue(String.format("%s : %s", "Date", LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))));

		Row projectAndBomNameRow = sheet.createRow(3);

		Cell bomNameCell = projectAndBomNameRow.createCell(3);
		bomNameCell.setCellValue("BOM name : " + bomName);

	}

	private void drawCompanyLogoInWorkbook(Workbook workbook, Sheet sheet)
	{

		if(StringUtils.isEmpty(ParametricConstants.BOM_EXPORT_LOGO_PATH))
			return;

		CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 2, 0, 0);
		sheet.addMergedRegion(cellRangeAddress);

		CellRangeAddress cellRangeAddress1 = new CellRangeAddress(1, 1, 2, 3);
		sheet.addMergedRegion(cellRangeAddress1);

		try
		{
			InputStream inputStream = new BufferedInputStream(new FileInputStream(env.getProperty(ParametricConstants.BOM_EXPORT_LOGO_PATH)));
			byte[] imageBytes = IOUtils.toByteArray(inputStream);
			int pictureureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
			inputStream.close();
			CreationHelper helper = workbook.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
			anchor.setCol1(0);
			anchor.setCol2(1);
			anchor.setRow1(0);
			anchor.setRow2(3);
			drawing.createPicture(anchor, pictureureIdx);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private CellStyle getHeaderCellStyle(Workbook workbook)
	{
		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setFontHeightInPoints((short) 14);
		// headerFont.setBold(true);
		headerFont.setColor(IndexedColors.WHITE.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerCellStyle.setWrapText(true);

		headerCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setTopBorderColor(IndexedColors.SKY_BLUE.getIndex());

		return headerCellStyle;
	}

	private CellStyle getDataCellStyle(Workbook workbook)
	{
		CellStyle dataCellStyle = workbook.createCellStyle();
		dataCellStyle.setAlignment(HorizontalAlignment.CENTER);
		dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		dataCellStyle.setWrapText(true);
		return dataCellStyle;
	}

	private void drawDownloadHeader(Workbook workbook, Sheet sheet, List<String> columnNames)
	{
		Row headerRow = sheet.createRow(startingRowIndex);
		int columnIndex = 0;
		CellStyle style = getHeaderCellStyle(workbook);
		for(String columnName : columnNames)
		{
			Cell headerCell = headerRow.createCell(columnIndex);
			headerCell.setCellValue(columnName);
			headerCell.setCellStyle(style);
			columnIndex++;
		}
	}

}
