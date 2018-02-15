package aaa.modules.cft.report;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import aaa.helpers.cft.CFTHelper;
import aaa.modules.cft.report.model.DataSourceKey;
import aaa.modules.cft.report.model.EntryStatus;
import aaa.modules.cft.report.model.ReportEntry;

import com.exigen.ipb.etcsa.utils.ExcelUtils;

public class ReportGeneratorService {

	public static void generateReport(Map<String, ReportEntry> reportObjects, String filePath) {

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet("Result");
			StylesTable stylesTable = workbook.getStylesSource();
			// create rows with color scheme explanation
			XSSFCellStyle xssfCellExp1 = stylesTable.createCellStyle();
			xssfCellExp1.setFillForegroundColor(new XSSFColor(new Color(196, 215, 155)));
			xssfCellExp1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			XSSFCellStyle xssfCellExp2 = stylesTable.createCellStyle();
			xssfCellExp2.setFillForegroundColor(new XSSFColor(new Color(192, 0, 0)));
			xssfCellExp2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			XSSFCellStyle xssfCellExp3 = stylesTable.createCellStyle();
			xssfCellExp3.setFillForegroundColor(new XSSFColor(new Color(246, 142, 56)));
			xssfCellExp3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			Row expRow1 = sheet.createRow(1);
			CellUtil.createCell(expRow1, 1, StringUtils.EMPTY, xssfCellExp1);
			CellUtil.createCell(expRow1, 2, "- Matched");
			Row expRow2 = sheet.createRow(3);
			CellUtil.createCell(expRow2, 1, StringUtils.EMPTY, xssfCellExp2);
			CellUtil.createCell(expRow2, 2, "- Missed");
			Row expRow3 = sheet.createRow(5);
			CellUtil.createCell(expRow3, 1, StringUtils.EMPTY, xssfCellExp3);
			CellUtil.createCell(expRow3, 2, "- Collision");

			Row headerRow = sheet.createRow(7);
			sheet.addMergedRegion(new CellRangeAddress(7, 7, 1, 2));
			sheet.addMergedRegion(new CellRangeAddress(7, 7, 6, 7));
			sheet.addMergedRegion(new CellRangeAddress(7, 7, 11, 12));

			XSSFCellStyle xssfCellStyle = stylesTable.createCellStyle();
			CFTHelper.setBorderToCellStyle(xssfCellStyle);
			xssfCellStyle.setFillForegroundColor(new XSSFColor(new Color(231, 235, 247)));
			xssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			xssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
			CellUtil.createCell(headerRow, 1, "ETL", xssfCellStyle);
			CellUtil.createCell(headerRow, 2, StringUtils.EMPTY, xssfCellStyle);
			CellUtil.createCell(headerRow, 4, "ETL vs TBR", xssfCellStyle);
			CellUtil.createCell(headerRow, 6, "TBR", xssfCellStyle);
			CellUtil.createCell(headerRow, 7, StringUtils.EMPTY, xssfCellStyle);
			CellUtil.createCell(headerRow, 9, "TBR vs DB", xssfCellStyle);
			CellUtil.createCell(headerRow, 11, "DB", xssfCellStyle);
			CellUtil.createCell(headerRow, 12, StringUtils.EMPTY, xssfCellStyle);
			CellUtil.createCell(headerRow, 14, "ETL vs DB", xssfCellStyle);

			Row subHeaderRow = sheet.createRow(8);
			CellUtil.createCell(subHeaderRow, 1, "Row Labels", xssfCellStyle);
			CellUtil.createCell(subHeaderRow, 2, "Amount", xssfCellStyle);
			CellUtil.createCell(subHeaderRow, 4, "Variance", xssfCellStyle);
			CellUtil.createCell(subHeaderRow, 6, "Row Labels", xssfCellStyle);
			CellUtil.createCell(subHeaderRow, 7, "Amount", xssfCellStyle);
			CellUtil.createCell(subHeaderRow, 9, "Variance", xssfCellStyle);
			CellUtil.createCell(subHeaderRow, 11, "Row Labels", xssfCellStyle);
			CellUtil.createCell(subHeaderRow, 12, "Amount", xssfCellStyle);
			CellUtil.createCell(subHeaderRow, 14, "Variance", xssfCellStyle);

			BigDecimal ffdTotal = new BigDecimal(0);
			BigDecimal ordTotal = new BigDecimal(0);
			BigDecimal dbdTotal = new BigDecimal(0);

			int rowNumber = 9;
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
				CellUtil.createCell(entryRow, 1, Objects.isNull(feedFilesEntryAmount) ? EntryStatus.MISSED.name() : reportEntry, entryCellStyle);
				CellUtil.createCell(entryRow, 2, Objects.isNull(feedFilesEntryAmount) ? StringUtils.EMPTY : String.valueOf(feedFilesEntryAmount), entryCellStyle);
				Double fddOrDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.FFD_KEY, DataSourceKey.ORD_KEY);
				Double fddDBDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.FFD_KEY, DataSourceKey.DBD_KEY);
				CellUtil.createCell(entryRow, 4, Objects.isNull(fddOrDelta) ? EntryStatus.MISSED.name() : fddOrDelta.toString(), entryCellStyle);
				CellUtil.createCell(entryRow, 14, Objects.isNull(fddDBDelta) ? EntryStatus.MISSED.name() : fddDBDelta.toString(), entryCellStyle);
				// OR results table
				Double orEntryAmount = reportObjects.get(reportEntry).getAmount().get(DataSourceKey.ORD_KEY);
				if (Objects.isNull(orEntryAmount)) {
					sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 6, 7));
				}
				CellUtil.createCell(entryRow, 6, Objects.isNull(orEntryAmount) ? EntryStatus.MISSED.name() : reportEntry, entryCellStyle);
				CellUtil.createCell(entryRow, 7, Objects.isNull(orEntryAmount) ? StringUtils.EMPTY : String.valueOf(orEntryAmount), entryCellStyle);
				Double orDBDelta = reportObjects.get(reportEntry).getDelta(DataSourceKey.ORD_KEY, DataSourceKey.DBD_KEY);
				CellUtil.createCell(entryRow, 9, Objects.isNull(orDBDelta) ? EntryStatus.MISSED.name() : orDBDelta.toString(), entryCellStyle);
				// DB results table
				Double dbEntryAmount = reportObjects.get(reportEntry).getAmount().get(DataSourceKey.DBD_KEY);
				if (Objects.isNull(dbEntryAmount)) {
					sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 13, 14));
				}
				CellUtil.createCell(entryRow, 11, Objects.isNull(dbEntryAmount) ? EntryStatus.MISSED.name() : reportEntry, entryCellStyle);
				CellUtil.createCell(entryRow, 12, Objects.isNull(dbEntryAmount) ? StringUtils.EMPTY : String.valueOf(dbEntryAmount), entryCellStyle);
				if (Objects.nonNull(feedFilesEntryAmount)) {
					ffdTotal = ffdTotal.add(new BigDecimal(feedFilesEntryAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				if (Objects.nonNull(orEntryAmount)) {
					ordTotal = ordTotal.add(new BigDecimal(orEntryAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				if (Objects.nonNull(dbEntryAmount)) {
					dbdTotal = dbdTotal.add(new BigDecimal(dbEntryAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				rowNumber++;
			}

			Row totalRow = sheet.createRow(rowNumber);
			XSSFCellStyle totalCellStyle = stylesTable.createCellStyle();
			CFTHelper.setBorderToCellStyle(totalCellStyle);
			CellUtil.createCell(totalRow, 1, "Total", totalCellStyle);
			CellUtil.createCell(totalRow, 2, ffdTotal.toString(), totalCellStyle);
			CellUtil.createCell(totalRow, 4, ffdTotal.subtract(ordTotal).toString(), totalCellStyle);
			CellUtil.createCell(totalRow, 6, "Total", totalCellStyle);
			CellUtil.createCell(totalRow, 7, ordTotal.toString(), totalCellStyle);
			CellUtil.createCell(totalRow, 9, ordTotal.subtract(dbdTotal).toString(), totalCellStyle);
			CellUtil.createCell(totalRow, 11, "Total", totalCellStyle);
			CellUtil.createCell(totalRow, 12, dbdTotal.toString(), totalCellStyle);
			CellUtil.createCell(totalRow, 14, ffdTotal.subtract(dbdTotal).toString(), totalCellStyle);

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
			// auto size for all involved cells
			Workbook formatCellsWorkbook = ExcelUtils.getWorkbook(filePath);
			Sheet resultSheet = formatCellsWorkbook.getSheet("Result");
			resultSheet.autoSizeColumn(1, true);
			resultSheet.autoSizeColumn(2, true);
			resultSheet.autoSizeColumn(4, true);
			resultSheet.autoSizeColumn(6, true);
			resultSheet.autoSizeColumn(7, true);
			resultSheet.autoSizeColumn(9, true);
			resultSheet.autoSizeColumn(11, true);
			resultSheet.autoSizeColumn(12, true);
			resultSheet.autoSizeColumn(14, true);
			FileOutputStream outputStream2 = null;
			try {
				outputStream2 = new FileOutputStream(filePath);
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
		} catch (IOException e) {
			e.printStackTrace();
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
		CFTHelper.setBorderToCellStyle(style);
		switch (status) {
			case MATCHED : {
				style.setFillForegroundColor(new XSSFColor(new Color(196, 215, 155)));
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				break;
			}
			case MISSED : {
				style.setFillForegroundColor(new XSSFColor(new Color(192, 0, 0)));
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				break;
			}
			case COLLISION : {
				style.setFillForegroundColor(new XSSFColor(new Color(246, 142, 56)));
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				break;
			}
		}
		style.setAlignment(HorizontalAlignment.CENTER);
	}
}
