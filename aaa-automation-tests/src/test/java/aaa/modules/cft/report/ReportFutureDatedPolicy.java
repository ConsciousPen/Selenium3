package aaa.modules.cft.report;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import aaa.modules.cft.ControlledFinancialBaseTest;

public class ReportFutureDatedPolicy extends ControlledFinancialBaseTest {

	public static void generateReport(List<List<Map<String, String>>> accNumberTable, String filePath) {

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet("Result");
			// StylesTable stylesTable = workbook.getStylesSource();
			Row expRow = sheet.createRow(1);
			CellUtil.createCell(expRow, 2, "Future Dated Policies");
			int RowNumber = 3;
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
