package aaa.modules.cft.report;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import aaa.modules.cft.ControlledFinancialBaseTest;

public class ReportFutureDatedPolicy extends ControlledFinancialBaseTest {

	public static void generateReport(List<List<Map<String, String>>> accNumberTable, String filePath) {

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet("Result");

			// sheet color and style scheme
			StylesTable stylesTable = workbook.getStylesSource();
			XSSFCellStyle xssfCellExp1Header = stylesTable.createCellStyle();
			xssfCellExp1Header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			XSSFCellStyle xssfCellExp1Correct = stylesTable.createCellStyle();
			xssfCellExp1Correct.setFillForegroundColor(new XSSFColor(new Color(196, 215, 155)));
			xssfCellExp1Correct.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			XSSFCellStyle xssfCellExpWrong = stylesTable.createCellStyle();
			xssfCellExpWrong.setFillForegroundColor(new XSSFColor(new Color(192, 0, 0)));
			xssfCellExpWrong.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Row expRow = sheet.createRow(1);
			CellUtil.createCell(expRow, 2, "Verification of Future Dated Policy");
			Row expRow1 = sheet.createRow(2);
			CellUtil.createCell(expRow1, 1, StringUtils.EMPTY, xssfCellExp1Correct);
			CellUtil.createCell(expRow1, 2, "- payments are not posted to the 1065 after policy become active");
			Row expRow2 = sheet.createRow(4);
			CellUtil.createCell(expRow2, 1, StringUtils.EMPTY, xssfCellExpWrong);
			CellUtil.createCell(expRow2, 2, "- wrong transactions to the 1065");
			int RowNumber = 6;
			for (List<Map<String, String>> accEntry : accNumberTable) {
				for (Map<String, String> trEntry : accEntry) {
					expRow = sheet.createRow(RowNumber);
					CellUtil.createCell(expRow, 2, trEntry.get("ACCNUMBER"));
					CellUtil.createCell(expRow, 4, trEntry.get("TXDATE"));
					RowNumber++;
				}
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
