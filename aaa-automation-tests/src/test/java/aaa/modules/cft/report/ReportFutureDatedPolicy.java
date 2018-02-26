package aaa.modules.cft.report;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import aaa.helpers.cft.CFTHelper;
import aaa.modules.cft.ControlledFinancialBaseTest;

public class ReportFutureDatedPolicy extends ControlledFinancialBaseTest {

	public static void generateReport(List<List<Map<String, String>>> accNumberTable, String filePath) {

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet("Result");

			// sheet color and style scheme
			StylesTable stylesTable = workbook.getStylesSource();
			XSSFCellStyle xssfCellCorrect = stylesTable.createCellStyle();
			xssfCellCorrect.setFillForegroundColor(new XSSFColor(new Color(190, 215, 155)));
			xssfCellCorrect.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			xssfCellCorrect.setAlignment(HorizontalAlignment.CENTER);

			XSSFCellStyle xssfCellWrong = stylesTable.createCellStyle();
			xssfCellWrong.setFillForegroundColor(new XSSFColor(new Color(190, 0, 0)));
			xssfCellWrong.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			xssfCellWrong.setAlignment(HorizontalAlignment.CENTER);

			Row expRow = sheet.createRow(1);
			CellUtil.createCell(expRow, 2, "Verification of Future Dated Policy");
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
			CFTHelper.setBorderToCellStyle(xssfCellHeader);

			int headerRowNumber = 6;
			Row headerRow = sheet.createRow(headerRowNumber);

			sheet.addMergedRegion(new CellRangeAddress(headerRowNumber, headerRowNumber, 1, 2));
			sheet.addMergedRegion(new CellRangeAddress(headerRowNumber, headerRowNumber, 4, 6));
			sheet.addMergedRegion(new CellRangeAddress(headerRowNumber, headerRowNumber, 8, 10));
			CellUtil.createCell(headerRow, 1, "Account Number", xssfCellHeader);
			CellUtil.createCell(headerRow, 4, "Policy Effective Date", xssfCellHeader);
			CellUtil.createCell(headerRow, 8, "Last Transaction Date", xssfCellHeader);

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
