package aaa.modules.cft.report;


import aaa.modules.cft.report.model.DataSourceKey;
import aaa.modules.cft.report.model.EntryStatus;
import aaa.modules.cft.report.model.ReportEntry;
import com.exigen.ipb.etcsa.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReportGeneratorService {

	public static void generateReport(Map<String, ReportEntry> reportObjects) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Result");
		CreationHelper factory = workbook.getCreationHelper();
		//create rows with color scheme explanation
		Row headerRow = sheet.createRow(4);
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 2));
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 5));
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 7, 8));
		StylesTable stylesTable = workbook.getStylesSource();
		XSSFCellStyle xssfCellStyle = stylesTable.createCellStyle();
		xssfCellStyle.setBorderBottom(BorderStyle.MEDIUM);
		xssfCellStyle.setBorderLeft(BorderStyle.MEDIUM);
		xssfCellStyle.setBorderRight(BorderStyle.MEDIUM);
		xssfCellStyle.setBorderTop(BorderStyle.MEDIUM);
		xssfCellStyle.setFillForegroundColor(new XSSFColor(new Color(149, 179, 215)));
		xssfCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		CellUtil.createCell(headerRow, 1, "Feed Files Data", xssfCellStyle);
		CellUtil.createCell(headerRow, 2, StringUtils.EMPTY, xssfCellStyle);
		CellUtil.createCell(headerRow, 4, "Operational Report's Subledger Data", xssfCellStyle);
		CellUtil.createCell(headerRow, 5, StringUtils.EMPTY, xssfCellStyle);
		CellUtil.createCell(headerRow, 7, "DB Subledger Data", xssfCellStyle);
		CellUtil.createCell(headerRow, 8, StringUtils.EMPTY, xssfCellStyle);

		Drawing drawing = sheet.createDrawingPatriarch();

		int rowNumber = 5;
		for (String reportEntry : reportObjects.keySet()) {
			// Feed files table preparation
			Double feedFilesEntryAmount = reportObjects.get(reportEntry).getAmount().get(DataSourceKey.FFD_KEY);
			Row entryRow = sheet.createRow(rowNumber);
			XSSFCellStyle entryCellStyle = stylesTable.createCellStyle();
			prepareCellEntryStyle(reportObjects.get(reportEntry).getEntryStatus(),
					entryCellStyle);
			if (Objects.isNull(feedFilesEntryAmount)) {
				sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 1, 2));
			}
			Cell ffEntryCell1 = CellUtil.createCell(entryRow, 1, Objects.isNull(feedFilesEntryAmount) ? EntryStatus.MISSED.name() : String.valueOf(feedFilesEntryAmount), entryCellStyle);
			CellUtil.createCell(entryRow, 2, StringUtils.EMPTY, entryCellStyle);
			if (Objects.nonNull(feedFilesEntryAmount)) {
				Double fddOrDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.FFD_KEY, DataSourceKey.ORD_KEY);
				Double fddDBDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.FFD_KEY, DataSourceKey.DBD_KEY);
				if (Objects.nonNull(fddOrDelta) || Objects.nonNull(fddDBDelta)) {
					ClientAnchor anchor = factory.createClientAnchor();
					anchor.setCol1(ffEntryCell1.getColumnIndex());
					anchor.setCol2(ffEntryCell1.getColumnIndex() + 5);
					anchor.setRow1(entryRow.getRowNum());
					anchor.setRow2(entryRow.getRowNum() + 2);
					Comment comment = drawing.createCellComment(anchor);
					String commentString = "Delta between OR Subledger Data:" + fddOrDelta + "\n Delta between DB Subledger Data:" + fddDBDelta;
					RichTextString str = factory.createRichTextString(commentString);
					comment.setString(str);
					ffEntryCell1.setCellComment(comment);
				}
			}

			//OR results table
			Double orEntryAmount = reportObjects.get(reportEntry).getAmount().get(DataSourceKey.ORD_KEY);
			if (Objects.isNull(orEntryAmount)) {
				sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 4, 5));
			}
			Cell orEntryCell1 = CellUtil.createCell(entryRow, 4, Objects.isNull(orEntryAmount) ? EntryStatus.MISSED.name() : String.valueOf(orEntryAmount), entryCellStyle);
			CellUtil.createCell(entryRow, 5, StringUtils.EMPTY, entryCellStyle);
			if (Objects.nonNull(orEntryAmount)) {
				Double fddOrDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.ORD_KEY, DataSourceKey.FFD_KEY);
				Double orDBDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.ORD_KEY, DataSourceKey.DBD_KEY);
				if (Objects.nonNull(fddOrDelta) || Objects.nonNull(orDBDelta)) {
					ClientAnchor anchor = factory.createClientAnchor();
					anchor.setCol1(orEntryCell1.getColumnIndex());
					anchor.setCol2(orEntryCell1.getColumnIndex() + 5);
					anchor.setRow1(entryRow.getRowNum());
					anchor.setRow2(entryRow.getRowNum() + 2);
					Comment comment = drawing.createCellComment(anchor);
					String commentString = "Delta between Feed Files Subledger Data:" + fddOrDelta + "\n Delta between DB Subledger Data:" + orDBDelta;
					RichTextString str = factory.createRichTextString(commentString);
					comment.setString(str);
					orEntryCell1.setCellComment(comment);
				}
			}

			//DB results table
			Double dbEntryAmount = reportObjects.get(reportEntry).getAmount().get(DataSourceKey.DBD_KEY);
			if (Objects.isNull(dbEntryAmount)) {
				sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 7, 8));
			}
			Cell dbEntryCell1 = CellUtil.createCell(entryRow, 7, Objects.isNull(dbEntryAmount) ? EntryStatus.MISSED.name() : String.valueOf(dbEntryAmount), entryCellStyle);
			CellUtil.createCell(entryRow, 8, StringUtils.EMPTY, entryCellStyle);
			if (Objects.nonNull(dbEntryAmount)) {
				Double dbOrDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.DBD_KEY, DataSourceKey.ORD_KEY);
				Double ffDBDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.DBD_KEY, DataSourceKey.FFD_KEY);
				if (Objects.nonNull(dbOrDelta) || Objects.nonNull(ffDBDelta)) {
					ClientAnchor anchor = factory.createClientAnchor();
					anchor.setCol1(dbEntryCell1.getColumnIndex());
					anchor.setCol2(dbEntryCell1.getColumnIndex() + 5);
					anchor.setRow1(entryRow.getRowNum());
					anchor.setRow2(entryRow.getRowNum() + 2);
					Comment comment = drawing.createCellComment(anchor);
					String commentString = "Delta between OR Subledger Data:" + dbOrDelta + "\n Delta between Feed Files Subledger Data:" + ffDBDelta;
					RichTextString str = factory.createRichTextString(commentString);
					comment.setString(str);
					dbEntryCell1.setCellComment(comment);
				}
			}
			rowNumber++;
		}

		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream("D:\\CSAA\\test.xlsx");
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
		// auto size for all cells
		Workbook formatCellsWorkbook = ExcelUtils.getWorkbook("D:\\CSAA\\test.xlsx");
		Sheet resultSheet = formatCellsWorkbook.getSheet("Result");
		Row headerRowFormatted = resultSheet.getRow(4);
		for (int i = 0; i < headerRowFormatted.getPhysicalNumberOfCells(); i++) {
			resultSheet.autoSizeColumn(i, true);
		}
		FileOutputStream outputStream2 = null;
		try {
			outputStream2 = new FileOutputStream("D:\\CSAA\\test.xlsx");
			formatCellsWorkbook.write(outputStream2);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream2.flush();
				outputStream2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}

	public static Map<String, ReportEntry> generateReportObjects(Map<String, Double> dbData, Map<String, Double> feedFilesData, Map<String, Double> orData) {

		Map<String, ReportEntry> entries = new HashMap<>();
		populateEntriesData(dbData, DataSourceKey.DBD_KEY, entries);
		populateEntriesData(feedFilesData, DataSourceKey.FFD_KEY, entries);
		populateEntriesData(orData, DataSourceKey.ORD_KEY, entries);

		return entries;
	}

	private static void populateEntriesData(Map<String, Double> entriesData, DataSourceKey dataSourceKey, Map<String, ReportEntry> resultMap) {
		for (String entryKey : entriesData.keySet()) {
			if (Objects.isNull(resultMap.get(entryKey))) {
				ReportEntry reportEntry = new ReportEntry();
				reportEntry.addAmount(dataSourceKey, entriesData.get(entryKey));
				resultMap.put(entryKey, reportEntry);
			} else {
				resultMap.get(entryKey).addAmount(dataSourceKey, entriesData.get(entryKey));
			}
		}
	}

	private static void prepareCellEntryStyle(EntryStatus status, XSSFCellStyle style) {
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);
		switch (status) {
			case MATCHED: {
				style.setFillForegroundColor(new XSSFColor(new Color(196, 215, 155)));
				break;
			}
			case MISSED: {
				style.setFillForegroundColor(new XSSFColor(new Color(218, 150, 148)));
				break;
			}
			case COLLISION: {
				style.setFillForegroundColor(new XSSFColor(new Color(250, 191, 143)));
				break;
			}
		}
	}
}
