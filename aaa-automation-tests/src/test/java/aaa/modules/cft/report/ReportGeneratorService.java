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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReportGeneratorService {

	private static final String CFT_VALIDATION_REPORT = "D:\\CSAA\\CFT_Validations_v2.xlsx";

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
		setBorderToCellStyle(xssfCellStyle);
		xssfCellStyle.setFillForegroundColor(new XSSFColor(new Color(149, 179, 215)));
		xssfCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		CellUtil.createCell(headerRow, 1, "Feed Files Data", xssfCellStyle);
		CellUtil.createCell(headerRow, 2, StringUtils.EMPTY, xssfCellStyle);
		CellUtil.createCell(headerRow, 4, "Operational Report's Subledger Data", xssfCellStyle);
		CellUtil.createCell(headerRow, 5, StringUtils.EMPTY, xssfCellStyle);
		CellUtil.createCell(headerRow, 7, "DB Subledger Data", xssfCellStyle);
		CellUtil.createCell(headerRow, 8, StringUtils.EMPTY, xssfCellStyle);

		Drawing drawing = sheet.createDrawingPatriarch();

		BigDecimal ffdTotal = new BigDecimal(0);
		BigDecimal ordTotal = new BigDecimal(0);
		BigDecimal dbdTotal = new BigDecimal(0);

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
			Cell ffEntryCell1 = CellUtil.createCell(entryRow, 1, Objects.isNull(feedFilesEntryAmount) ? EntryStatus.MISSED.name() : reportEntry, entryCellStyle);
			CellUtil.createCell(entryRow, 2, Objects.isNull(feedFilesEntryAmount) ? StringUtils.EMPTY : String.valueOf(feedFilesEntryAmount), entryCellStyle);
			if (Objects.nonNull(feedFilesEntryAmount)) {
				Double fddOrDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.FFD_KEY, DataSourceKey.ORD_KEY);
				Double fddDBDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.FFD_KEY, DataSourceKey.DBD_KEY);
				if (Objects.nonNull(fddOrDelta) || Objects.nonNull(fddDBDelta)) {
					ClientAnchor anchor = factory.createClientAnchor();
					anchor.setCol1(ffEntryCell1.getColumnIndex());
					anchor.setCol2(ffEntryCell1.getColumnIndex() + 7);
					anchor.setRow1(entryRow.getRowNum());
					anchor.setRow2(entryRow.getRowNum() + 2);
					Comment comment = drawing.createCellComment(anchor);
					String commentString = "Delta between OR Subledger Data:" + fddOrDelta + "\nDelta between DB Subledger Data:" + fddDBDelta;
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
			Cell orEntryCell1 = CellUtil.createCell(entryRow, 4, Objects.isNull(orEntryAmount) ? EntryStatus.MISSED.name() : reportEntry, entryCellStyle);
			CellUtil.createCell(entryRow, 5, Objects.isNull(orEntryAmount) ? StringUtils.EMPTY : String.valueOf(orEntryAmount), entryCellStyle);

			if (Objects.nonNull(orEntryAmount)) {
				Double fddOrDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.ORD_KEY, DataSourceKey.FFD_KEY);
				Double orDBDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.ORD_KEY, DataSourceKey.DBD_KEY);
				if (Objects.nonNull(fddOrDelta) || Objects.nonNull(orDBDelta)) {
					ClientAnchor anchor = factory.createClientAnchor();
					anchor.setCol1(orEntryCell1.getColumnIndex());
					anchor.setCol2(orEntryCell1.getColumnIndex() + 7);
					anchor.setRow1(entryRow.getRowNum());
					anchor.setRow2(entryRow.getRowNum() + 2);
					Comment comment = drawing.createCellComment(anchor);
					String commentString = "Delta between Feed Files Subledger Data:" + fddOrDelta + "\nDelta between DB Subledger Data:" + orDBDelta;
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
			Cell dbEntryCell1 = CellUtil.createCell(entryRow, 7, Objects.isNull(dbEntryAmount) ? EntryStatus.MISSED.name() : reportEntry, entryCellStyle);
			CellUtil.createCell(entryRow, 8, Objects.isNull(dbEntryAmount) ? StringUtils.EMPTY : String.valueOf(dbEntryAmount), entryCellStyle);
			if (Objects.nonNull(dbEntryAmount)) {
				Double dbOrDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.DBD_KEY, DataSourceKey.ORD_KEY);
				Double ffDBDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.DBD_KEY, DataSourceKey.FFD_KEY);
				if (Objects.nonNull(dbOrDelta) || Objects.nonNull(ffDBDelta)) {
					ClientAnchor anchor = factory.createClientAnchor();
					anchor.setCol1(dbEntryCell1.getColumnIndex());
					anchor.setCol2(dbEntryCell1.getColumnIndex() + 7);
					anchor.setRow1(entryRow.getRowNum());
					anchor.setRow2(entryRow.getRowNum() + 2);
					Comment comment = drawing.createCellComment(anchor);
					String commentString = "Delta between OR Subledger Data:" + dbOrDelta + "\nDelta between Feed Files Subledger Data:" + ffDBDelta;
					RichTextString str = factory.createRichTextString(commentString);
					comment.setString(str);
					dbEntryCell1.setCellComment(comment);
				}
			}
			if (Objects.nonNull(feedFilesEntryAmount)) {
				ffdTotal = ffdTotal.add(new BigDecimal(feedFilesEntryAmount));
			}
			if (Objects.nonNull(orEntryAmount)) {
				ordTotal = ordTotal.add(new BigDecimal(orEntryAmount));
			}
			if (Objects.nonNull(dbEntryAmount)) {
				dbdTotal = dbdTotal.add(new BigDecimal(dbEntryAmount));
			}
			rowNumber++;
		}

		Row totalRow = sheet.createRow(rowNumber);
		XSSFCellStyle totalCellStyle = stylesTable.createCellStyle();
		setBorderToCellStyle(totalCellStyle);
		Cell ffdTotalCell = CellUtil.createCell(totalRow, 1, "Total", totalCellStyle);
		CellUtil.createCell(totalRow, 2, ffdTotal.toString(), totalCellStyle);

		Cell ordTotalCell = CellUtil.createCell(totalRow, 4, "Total", totalCellStyle);
		CellUtil.createCell(totalRow, 5, ordTotal.toString(), totalCellStyle);

		Cell dbdTotalCell = CellUtil.createCell(totalRow, 7, "Total", totalCellStyle);
		CellUtil.createCell(totalRow, 8, dbdTotal.toString(), totalCellStyle);


		ClientAnchor ffAnchor = factory.createClientAnchor();
		ffAnchor.setCol1(ffdTotalCell.getColumnIndex());
		ffAnchor.setCol2(ffdTotalCell.getColumnIndex() + 7);
		ffAnchor.setRow1(totalRow.getRowNum());
		ffAnchor.setRow2(totalRow.getRowNum() + 2);
		Comment ffComment = drawing.createCellComment(ffAnchor);
		String commentString = "Delta between OR Subledger Data:" + ffdTotal.subtract(ordTotal) + "\nDelta between DB Subledger Data:" + ffdTotal.subtract(dbdTotal);
		RichTextString str = factory.createRichTextString(commentString);
		ffComment.setString(str);
		ffdTotalCell.setCellComment(ffComment);

		ClientAnchor orAnchor = factory.createClientAnchor();
		orAnchor.setCol1(ordTotalCell.getColumnIndex());
		orAnchor.setCol2(ordTotalCell.getColumnIndex() + 7);
		orAnchor.setRow1(totalRow.getRowNum());
		orAnchor.setRow2(totalRow.getRowNum() + 2);
		Comment orComment = drawing.createCellComment(orAnchor);
		commentString = "Delta between Feed Files Subledger Data:" + ordTotal.subtract(ffdTotal) + "\nDelta between DB Subledger Data:" + ordTotal.subtract(dbdTotal);
		str = factory.createRichTextString(commentString);
		orComment.setString(str);
		ordTotalCell.setCellComment(orComment);

		ClientAnchor dbAnchor = factory.createClientAnchor();
		dbAnchor.setCol1(dbdTotalCell.getColumnIndex());
		dbAnchor.setCol2(dbdTotalCell.getColumnIndex() + 7);
		dbAnchor.setRow1(totalRow.getRowNum());
		dbAnchor.setRow2(totalRow.getRowNum() + 2);
		Comment dbComment = drawing.createCellComment(dbAnchor);
		commentString = "Delta between OR Subledger Data:" + dbdTotal.subtract(ordTotal) + "\nDelta between Feed Files Subledger Data:" + dbdTotal.subtract(ffdTotal);
		str = factory.createRichTextString(commentString);
		dbComment.setString(str);
		dbdTotalCell.setCellComment(dbComment);

		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(CFT_VALIDATION_REPORT);
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
		Workbook formatCellsWorkbook = ExcelUtils.getWorkbook(CFT_VALIDATION_REPORT);
		Sheet resultSheet = formatCellsWorkbook.getSheet("Result");
		Row headerRowFormatted = resultSheet.getRow(4);
		for (int i = 0; i < headerRowFormatted.getPhysicalNumberOfCells(); i++) {
			resultSheet.autoSizeColumn(i, true);
		}
		FileOutputStream outputStream2 = null;
		try {
			outputStream2 = new FileOutputStream(CFT_VALIDATION_REPORT);
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
		setBorderToCellStyle(style);
		switch (status) {
			case MATCHED: {
				style.setFillForegroundColor(new XSSFColor(new Color(196, 215, 155)));
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				break;
			}
			case MISSED: {
				style.setFillForegroundColor(new XSSFColor(new Color(218, 150, 148)));
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				break;
			}
			case COLLISION: {
				style.setFillForegroundColor(new XSSFColor(new Color(250, 191, 143)));
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				break;
			}
		}
	}

	private static void setBorderToCellStyle(XSSFCellStyle style) {
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);
	}
}
