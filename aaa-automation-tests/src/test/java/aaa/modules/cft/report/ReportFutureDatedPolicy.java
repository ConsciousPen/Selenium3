package aaa.modules.cft.report;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.*;

import aaa.helpers.cft.CFTHelper;
import aaa.modules.cft.ControlledFinancialBaseTest;

public class ReportFutureDatedPolicy extends ControlledFinancialBaseTest {

	public static void generateReport(List<List<Map<String, String>>> accNumberTable, String filePath) {

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet("Result");

			// sheet color and style scheme
			StylesTable stylesTable = workbook.getStylesSource();
			XSSFFont fontBold = workbook.createFont();
			fontBold.setBold(true);
			XSSFCellStyle xssfCellCorrect = stylesTable.createCellStyle();
			xssfCellCorrect.setFillForegroundColor(new XSSFColor(new Color(190, 215, 155)));
			xssfCellCorrect.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			xssfCellCorrect.setAlignment(HorizontalAlignment.CENTER);

			XSSFCellStyle xssfCellWrong = stylesTable.createCellStyle();
			xssfCellWrong.setFillForegroundColor(new XSSFColor(new Color(190, 0, 0)));
			xssfCellWrong.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			xssfCellWrong.setAlignment(HorizontalAlignment.CENTER);

			Row expRow = sheet.createRow(0);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 5));
			XSSFCellStyle xssfExpRow = stylesTable.createCellStyle();
			xssfExpRow.setAlignment(HorizontalAlignment.CENTER);
			xssfExpRow.setFont(fontBold);
			CellUtil.createCell(expRow, 2, "Verification of Future Dated Policy", xssfExpRow);
			Row expRow1 = sheet.createRow(2);
			CellUtil.createCell(expRow1, 1, StringUtils.EMPTY, xssfCellCorrect);
			CellUtil.createCell(expRow1, 2, "- payments are not posted to the 1065 after policy become active");
			Row expRow2 = sheet.createRow(4);
			CellUtil.createCell(expRow2, 1, StringUtils.EMPTY, xssfCellWrong);
			CellUtil.createCell(expRow2, 2, "- wrong transactions to the 1065");
			// Table header
			XSSFCellStyle xssfCellHeader = stylesTable.createCellStyle();
			xssfCellHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			xssfCellHeader.setFillForegroundColor(new XSSFColor(new Color(100, 185, 250)));
			xssfCellHeader.setAlignment(HorizontalAlignment.CENTER);
			xssfCellHeader.setVerticalAlignment(VerticalAlignment.CENTER);

			int headerRowNumber = 6;
			Row headerRow = sheet.createRow(headerRowNumber);

			sheet.addMergedRegion(new CellRangeAddress(headerRowNumber, headerRowNumber, 1, 2));
			sheet.addMergedRegion(new CellRangeAddress(headerRowNumber, headerRowNumber, 4, 6));
			sheet.addMergedRegion(new CellRangeAddress(headerRowNumber, headerRowNumber, 8, 10));
			CFTHelper.setBorderToCellStyle(xssfCellHeader);

			CellUtil.createCell(headerRow, 1, "Account Number", xssfCellHeader);
			CellUtil.createCell(headerRow, 2, "", xssfCellHeader);
			CellUtil.createCell(headerRow, 4, "Policy Effective Date", xssfCellHeader);
			CellUtil.createCell(headerRow, 6, "", xssfCellHeader);
			CellUtil.createCell(headerRow, 8, "Last Transaction Date", xssfCellHeader);
			CellUtil.createCell(headerRow, 10, "", xssfCellHeader);

			int rowNumber = headerRowNumber + 1;
			for (List<Map<String, String>> accEntry : accNumberTable) {
				Row accRow = sheet.createRow(rowNumber);
				sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 1, 2));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 4, 6));
				sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 8, 10));
				if (accEntry.size() == 1) {
					CellUtil.createCell(accRow, 1, accEntry.get(0).get("ACCNUMBER"), xssfCellCorrect);
					CellUtil.createCell(accRow, 4, accEntry.get(0).get("TXDATE"), xssfCellCorrect);
					CellUtil.createCell(accRow, 8, accEntry.get(0).get("TXDATE"), xssfCellCorrect);
				} else {
					CellUtil.createCell(accRow, 1, accEntry.get(0).get("ACCNUMBER"), xssfCellWrong);
					CellUtil.createCell(accRow, 4, accEntry.get(0).get("TXDATE"), xssfCellWrong);
					CellUtil.createCell(accRow, 8, accEntry.get(accEntry.size() - 1).get("TXDATE"), xssfCellWrong);
				}
				rowNumber++;
			}

			// save workbook
			FileOutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(filePath);
				workbook.write(outputStream);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
