package aaa.modules.cft.report;

import aaa.modules.cft.ControlledFinancialBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ReportAccountEntry extends ControlledFinancialBaseTest {

	public static void generateReport(List<List<Map<String, String>>> accNumberTable,String filePath) {

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet("Result");
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
			CellUtil.createCell(expRow, 2, "Verification of Entries into 1065 Ledger Account", xssfExpRow);
			Row expRow1 = sheet.createRow(2);
			CellUtil.createCell(expRow1, 1, StringUtils.EMPTY, xssfCellCorrect);
			CellUtil.createCell(expRow1, 2, "- correct");
			Row expRow2 = sheet.createRow(4);
			CellUtil.createCell(expRow2, 1, StringUtils.EMPTY, xssfCellWrong);
			CellUtil.createCell(expRow2, 2, "- wrong");

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
