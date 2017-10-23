package com.exigen.ipb.etcsa.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ExcelUtils {

	public static Workbook getWorkbook(String filePath) {
		Workbook wb;
		InputStream fileToRead;
		String exceptionMessage = "Can't read from input stream. File might be corrupted or has wrong extension.";
		byte[] buf = new byte[1024];
		byte[] content;
		int n;
		fileToRead = getFileInputStream(new File(FilenameUtils.separatorsToSystem(filePath)));
		// Get byte array content from filePath
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			while ((n = fileToRead.read(buf)) >= 0) {
				baos.write(buf, 0, n);
			}
			content = baos.toByteArray();
		} catch (IOException e) {
			throw new IstfException(exceptionMessage, e);
		} finally {
			try {
				fileToRead.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			// Try to create workbook for file with 'xlsx' extension;
			wb = new XSSFWorkbook(new ByteArrayInputStream(content));
		} catch (POIXMLException e) {
			// If failed then try to create for file with 'xls' extension;
			try {
				wb = new HSSFWorkbook(new ByteArrayInputStream(content));
			} catch (IOException e1) {
				throw new IstfException(exceptionMessage, e1.getCause());
			}
		} catch (IOException e) {
			throw new IstfException(exceptionMessage, e);
		}

		return wb;
	}

	public static Sheet getSheet(String filePath) {
		return getSheet(getWorkbook(filePath), null);
	}

	public static Sheet getSheet(String filePath, String sheetName) {
		return getSheet(getWorkbook(filePath), sheetName);
	}

	public static Sheet getSheet(Workbook wb) {
		return getSheet(wb, null);
	}

	public static Sheet getSheet(Workbook wb, String sheetName) {
		Sheet sheet;
		if (null == sheetName) {
			sheet = wb.getSheetAt(0);
		} else {
			sheet = wb.getSheet(sheetName);
		}
		return sheet;
	}

	public static List<String> getSheetNames(String filePath) {
		return getSheetNames(getWorkbook(filePath));
	}

	public static List<String> getSheetNames(Workbook wb) {
		List<String> sheetNames = new ArrayList<>();
		for (int sheetNumber = 0; sheetNumber < wb.getNumberOfSheets(); sheetNumber++) {
			sheetNames.add(wb.getSheetName(sheetNumber));
		}
		return sheetNames;
	}

	public static String getCellValue(Sheet sheet, int rowNumber, int columnNumber) {
		return getCellValue(sheet.getRow(rowNumber), columnNumber);
	}

	public static String getCellValue(Row row, int columnNumber) {
		if (null == row) {
			throw new IstfException("Impossible to get value from null row");
		}
		return getCellValue(row.getCell(columnNumber));
	}

	public static String getCellValue(Cell cell) {
		String value = "";
		if (null != cell) {
			switch (cell.getCellType()) {
				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						value = getDateTimeCellValue(cell).toString();
					} else {
						DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
						df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
						value = df.format(cell.getNumericCellValue());
					}
					break;
				case Cell.CELL_TYPE_STRING:
				case Cell.CELL_TYPE_FORMULA:
					value = cell.getStringCellValue().replace("\n", "").trim();
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					value = String.valueOf(cell.getBooleanCellValue()).trim();
					break;
				case Cell.CELL_TYPE_ERROR:
					value = "Error: " + String.valueOf(cell.getErrorCellValue()).trim();
					break;
				case Cell.CELL_TYPE_BLANK:
				default:
					break;
			}
		}
		return value;
	}

	public static Double getDoubleCellValue(Sheet sheet, int rowNumber, int columnNumber) {
		return getDoubleCellValue(sheet.getRow(rowNumber), columnNumber);
	}

	public static Double getDoubleCellValue(Row row, int columnNumber) {
		if (null == row) {
			throw new IstfException("Impossible to parse numeric value from null row");
		}
		return getDoubleCellValue(row.getCell(columnNumber));
	}

	public static Double getDoubleCellValue(Cell cell) {
		if (null == cell) {
			throw new IstfException("Impossible to parse numeric value from null cell");
		}
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && !DateUtil.isCellDateFormatted(cell)) {
			return cell.getNumericCellValue();
		}

		throw new IstfException(String.format("Cell has value \"%s\" which is not numeric", getCellValue(cell)));
	}

	public static LocalDateTime getDateTimeCellValue(Sheet sheet, int rowNumber, int columnNumber) {
		return getDateTimeCellValue(sheet.getRow(rowNumber), columnNumber);
	}

	public static LocalDateTime getDateTimeCellValue(Row row, int columnNumber) {
		if (null == row) {
			throw new IstfException("Impossible to parse date value from null row");
		}
		return getDateTimeCellValue(row.getCell(columnNumber));
	}

	public static LocalDateTime getDateTimeCellValue(Cell cell) {
		if (null != cell && cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)) {
			return LocalDate.parse(cell.getStringCellValue(), DateTimeUtils.MM_DD_YYYY).atStartOfDay();
		}
		throw new IstfException(String.format("Cell has value \"%s\" which is not a date", getCellValue(cell)));
	}

	public static int getColumnNumber(Sheet sheet, String columnName) {
		return getColumnNumber(getFirstRow(sheet), columnName);
	}

	public static int getColumnNumber(Sheet sheet, int headerRowNumber, String columnName) {
		return getColumnNumber(sheet.getRow(headerRowNumber), columnName);
	}

	public static int getColumnNumber(Row row, String columnName) {
		int columnNumber = -1;
		if (null == row) {
			throw new IstfException("Impossible to find column number if \"row\" parameter is null");
		}

		for (int cell = 0; cell <= row.getLastCellNum(); cell++) {
			if (getCellValue(row, cell).equals(columnName)) {
				columnNumber = cell;
				break;
			}
		}

		if (columnNumber < 0) {
			throw new IstfException(String.format("There is no \"%1$s\" column name in header (row #%2$s)", columnName, row.getRowNum()));
		}
		return columnNumber;
	}

	public static Row getFirstRow(Sheet sheet) {
		return sheet.getRow(0);
	}

	public static Row getLastRow(Sheet sheet) {
		return sheet.getRow(sheet.getLastRowNum());
	}

	public static int getPhysicalNumberOfRows(Row fromRow) {
		int fromRowNumber = fromRow.getRowNum();
		Sheet sheet = fromRow.getSheet();
		Row row = fromRow;
		while (!isRowEmpty(row)) {
			row = sheet.getRow(row.getRowNum() + 1);
		}
		return row.getRowNum() - fromRowNumber;
	}

	public static Row getRow(Sheet sheet, String columnName, String valueInCell) {
		return getRow(getFirstRow(sheet), columnName, valueInCell);
	}

	public static Row getRow(Row fromRow, String columnName, String valueInCell) {
		return getRow(fromRow, null, getColumnNumber(fromRow, columnName), valueInCell);
	}

	public static Row getRow(Row fromRow, Row toRow, String columnName, String valueInCell) {
		return getRow(fromRow, toRow, getColumnNumber(fromRow, columnName), valueInCell);
	}

	public static Row getRow(Row fromRow, int columnNumber, String valueInCell) {
		return getRow(fromRow, null, columnNumber, valueInCell);
	}

	/**
	 * Search for <b>first</b> occurrence of a row by {@code valueInCell} in {@code columnNumber} number in range between numbers of {@code fromRow} and {@code toRow} objects
	 *
	 * @param fromRow      - Searching of desired row will be started from row number of this {@code fromRow} object. Could be treated as header of some table in sheet
	 * @param toRow        - Searching of desired row will be stopped after row number of this {@code toRow} object. If it is "null" then last row in sheet will be used as last row for search
	 * @param columnNumber - Column number where {@code valueInCell} value will be searched
	 * @param valueInCell  - String value to be searched in {@code columnNumber} number
	 * @return First occurrence of found row
	 * @throws IstfException in case if {@code fromRow} is null or if desired row was not found
	 */
	public static Row getRow(Row fromRow, Row toRow, int columnNumber, String valueInCell) {
		if (null == fromRow) {
			throw new IstfException("Impossible to find row if \"fromRow\" parameter is null");
		}
		Row searchRow = null;
		Sheet sheet = fromRow.getSheet();
		if (null == toRow) {
			toRow = getLastRow(sheet);
		}

		for (int rowNumber = fromRow.getRowNum(); rowNumber <= toRow.getRowNum(); rowNumber++) {
			Row row = sheet.getRow(rowNumber);
			if (getCellValue(row, columnNumber).equals(valueInCell)) {
				searchRow = row;
				break;
			}
		}

		if (null == searchRow) {
			throw new IstfException(String
					.format("There is no row with \"%1$s\" value in column %2$s in range between row %3$s and %4$s", valueInCell, columnNumber, fromRow.getRowNum(), toRow.getRowNum()));
		}
		return searchRow;
	}

	public static Row getRow(Sheet sheet, Map<String, String> rowQuery) {
		return getRow(getFirstRow(sheet), null, rowQuery);
	}

	public static Row getRow(Row fromRow, Map<String, String> rowQuery) {
		return getRow(fromRow, null, rowQuery);
	}

	/**
	 * Search for <b>first</b> occurrence of a row which has <b>all</b> {@code rowQuery} parameters in range between numbers of {@code fromRow} and {@code toRow} objects.
	 *
	 * @param fromRow  - Searching of desired row will be started from row number of this {@code fromRow} object. Could be treated as header of some table in sheet
	 * @param toRow    - Searching of desired row will be stopped after row number of this {@code toRow} object. If it is "null" then last row in sheet will be used as last row for search
	 * @param rowQuery - "Key:Value" search parameters, where "Key" is a column name in header which should be located in {@code fromRow} row number, and "Value" is value in cell for this column.
	 * @return First occurrence of found row
	 * @throws IstfException in case if {@code fromRow} is null or if desired row was not found
	 */
	public static Row getRow(Row fromRow, Row toRow, Map<String, String> rowQuery) {
		if (null == fromRow) {
			throw new IstfException("Impossible to find row if \"fromRow\" parameter is null");
		}
		if (null == toRow) {
			toRow = getLastRow(fromRow.getSheet());
		}

		for (int rowNumber = fromRow.getRowNum(); rowNumber <= toRow.getRowNum(); rowNumber++) {
			Row row = fromRow.getSheet().getRow(rowNumber);
			int valuesMatch = 0;
			for (Map.Entry<String, String> queryCell : rowQuery.entrySet()) {
				int columnNumber = getColumnNumber(fromRow, queryCell.getKey());
				if (!getCellValue(row, columnNumber).equals(queryCell.getValue())) {
					break;
				}
				valuesMatch++;
				if (valuesMatch == rowQuery.size()) {
					return row;
				}
			}
		}

		throw new IstfException(String.format("Row with query params \"%1$s\" was not found on sheet \"%2$s\" in range between row %3$s and %4$s.",
				rowQuery.entrySet(), fromRow.getSheet().getSheetName(), fromRow.getRowNum(), toRow.getRowNum()));
	}

	public static List<Row> getRowsContains(Sheet sheet, Map<String, String> rowQuery) {
		return getRowsContains(getFirstRow(sheet), rowQuery);
	}

	public static List<Row> getRowsContains(Row fromRow, Map<String, String> rowQuery) {
		return getRowsContains(fromRow, null, rowQuery);
	}

	/**
	 * Get list of unique rows where each has at least one match of [Column Name:Cell Value] from {@code rowQuery} in range between numbers of {@code fromRow} and {@code toRow} objects.
	 *
	 * @param fromRow  - Searching of desired row will be started from row number of this {@code fromRow} object. Could be treated as header of some table in sheet
	 * @param toRow    - Searching of desired row will be stopped after row number of this {@code toRow} object. If it is "null" then last row in sheet will be used as last row for search
	 * @param rowQuery - "Key:Value" search parameters, where "Key" is a column name in header which should be located in {@code fromRow} row number, and "Value" is value in cell for this column.
	 * @return List of found rows
	 * @throws IstfException in case if {@code fromRow} is null or some [Column Name:Cell Value] from {@code rowQuery} was not found on any row
	 */
	public static List<Row> getRowsContains(Row fromRow, Row toRow, Map<String, String> rowQuery) {
		if (null == toRow) {
			toRow = getLastRow(fromRow.getSheet());
		}

		Set<Row> foundRows = new LinkedHashSet<>();
		for (Map.Entry<String, String> queryCell : rowQuery.entrySet()) {
			Row row = getRow(fromRow, toRow, queryCell.getKey(), queryCell.getValue());
			foundRows.add(row);
		}

		return new ArrayList<>(foundRows);
	}

	public static List<String> getColumnValues(Sheet sheet, int columnNumber) {
		return getColumnValues(getFirstRow(sheet), columnNumber);
	}

	public static List<String> getColumnValues(Sheet sheet, String columnName) {
		Row firstRow = getFirstRow(sheet);
		int columnNumber = getColumnNumber(firstRow, columnName);
		return getColumnValues(firstRow, columnNumber);
	}

	public static List<String> getColumnValues(Sheet sheet, int fromRowNumber, int columnNumber) {
		return getColumnValues(sheet.getRow(fromRowNumber), columnNumber);
	}

	public static List<String> getColumnValues(Sheet sheet, int fromRowNumber, int toRowNumber, int columnNumber) {
		return getColumnValues(sheet.getRow(fromRowNumber), sheet.getRow(toRowNumber), columnNumber);
	}

	public static List<String> getColumnValues(Sheet sheet, int fromRowNumber, String columnName) {
		Row fromRow = sheet.getRow(fromRowNumber);
		return getColumnValues(fromRow, getColumnNumber(fromRow, columnName));
	}

	public static List<String> getColumnValues(Sheet sheet, int fromRowNumber, int toRowNumber, String columnName) {
		Row fromRow = sheet.getRow(fromRowNumber);
		Row toRow = toRowNumber < 0 ? null : sheet.getRow(toRowNumber);
		return getColumnValues(fromRow, toRow, getColumnNumber(fromRow, columnName));
	}

	public static List<String> getColumnValues(Row fromRow, String columnName) {
		int columnNumber = getColumnNumber(fromRow, columnName);
		return getColumnValues(fromRow, columnNumber);
	}

	public static List<String> getColumnValues(Row fromRow, int columnNumber) {
		return getColumnValues(fromRow, null, columnNumber);
	}

	public static List<String> getColumnValues(Row fromRow, Row toRow, int columnNumber) {
		List<String> values = new ArrayList<>();
		if (null == fromRow) {
			throw new IstfException("Impossible to find row if \"fromRow\" parameter is null");
		}
		Sheet sheet = fromRow.getSheet();
		if (null == toRow) {
			toRow = getLastRow(sheet);
		}

		for (int rowNumber = fromRow.getRowNum(); rowNumber <= toRow.getRowNum(); rowNumber++) {
			Row row = sheet.getRow(rowNumber);
			if (null == row) {
				continue;
			}
			values.add(getCellValue(row, columnNumber));
		}
		return values;
	}

	public static boolean isRowEmpty(Sheet sheet, int rowNumber) {
		return isRowEmpty(sheet.getRow(rowNumber));
	}

	public static boolean isRowEmpty(Row row) {
		boolean rowIsEmpty = true;
		for (int cellNumber = 0; cellNumber <= row.getLastCellNum(); cellNumber++) {
			if (!isCellEmpty(row, cellNumber)) {
				rowIsEmpty = false;
				break;
			}
		}
		return rowIsEmpty;
	}

	public static boolean rowExists(Sheet sheet, Map<String, String> rowQuery) {
		return rowExists(getFirstRow(sheet), rowQuery);
	}

	public static boolean rowExists(Row fromRow, Map<String, String> rowQuery) {
		return rowExists(fromRow, null, rowQuery);
	}

	/**
	 * Verifies existence of a row which has <b>all</b> {@code rowQuery} parameters in range between numbers of {@code fromRow} and {@code toRow} objects.
	 *
	 * @param fromRow  - Searching of desired row will be started from row number of this {@code fromRow} object. Could be treated as header of some table in sheet
	 * @param toRow    - Searching of desired row will be stopped after row number of this {@code toRow} object. If it is "null" then last row in sheet will be used as last row for search
	 * @param rowQuery - "Key:Value" search parameters, where "Key" is a column name in header which should be located in {@code fromRow} row number, and "Value" is value in cell for this column.
	 * @return "true" if all {@code rowQuery} parameters will be found in same row, otherwise - "false".
	 * @throws IstfException in case if {@code fromRow} is null
	 */
	public static boolean rowExists(Row fromRow, Row toRow, Map<String, String> rowQuery) {
		boolean exists = true;
		try {
			getRow(fromRow, toRow, rowQuery);
		} catch (IstfException ignored) {
			exists = false;
		}
		return exists;
	}

	public static boolean isCellEmpty(Sheet sheet, int rowNumber, int columnNumber) {
		return isCellEmpty(sheet.getRow(rowNumber), columnNumber);
	}

	public static boolean isCellEmpty(Row row, int columnNumber) {
		if (null == row) {
			throw new IstfException("Impossible to parse cell value from null row");
		}
		return isCellEmpty(row.getCell(columnNumber));
	}

	public static boolean isCellEmpty(Cell cell) {
		return getCellValue(cell).isEmpty();
	}

	public static void verifyIsCellEmpty(Sheet sheet, int rowNumber, int columnNumber) {
		verifyIsCellEmpty(sheet, rowNumber, columnNumber, true);
	}

	public static void verifyIsCellEmpty(Sheet sheet, int rowNumber, int columnNumber, boolean expectedValue) {
		verifyIsCellEmpty(sheet.getRow(rowNumber), columnNumber, expectedValue);
	}

	// ========================================== Verify Methods ==========================================

	public static void verifyIsCellEmpty(Row row, int columnNumber) {
		verifyIsCellEmpty(row, columnNumber, true);
	}

	public static void verifyIsCellEmpty(Row row, int columnNumber, boolean expectedValue) {
		verifyIsCellEmpty(row.getCell(columnNumber), true);
	}

	public static void verifyIsCellEmpty(Cell cell) {
		verifyIsCellEmpty(cell, true);
	}

	public static void verifyIsCellEmpty(Cell cell, boolean expectedValue) {
		String cellLocation = "";
		if (null != cell) {
			cellLocation = String.format(" Check row #%1$s and column #%2$s.", cell.getRowIndex(), cell.getColumnIndex());
		}

		if (expectedValue) {
			CustomAssert.assertTrue("Cell is not empty." + cellLocation, isCellEmpty(cell));
		} else {
			CustomAssert.assertFalse("Cell is empty." + cellLocation, isCellEmpty(cell));
		}
	}

	public static void verifyIsRowEmpty(Sheet sheet, int rowNumber) {
		verifyIsRowEmpty(sheet, rowNumber, true);
	}

	public static void verifyIsRowEmpty(Sheet sheet, int rowNumber, boolean expectedValue) {
		verifyIsRowEmpty(sheet.getRow(rowNumber), expectedValue);
	}

	public static void verifyIsRowEmpty(Row row) {
		verifyIsRowEmpty(row, true);
	}

	public static void verifyIsRowEmpty(Row row, boolean expectedValue) {
		String rowLocation = "";
		if (null != row) {
			rowLocation = String.format(" Check row #%1$s in \"%2$s\" sheet.", row.getRowNum(), row.getSheet());
		}

		if (expectedValue) {
			CustomAssert.assertTrue("Row is not empty." + rowLocation, isRowEmpty(row));
		} else {
			CustomAssert.assertFalse("Row is empty." + rowLocation, isRowEmpty(row));
		}
	}

	public static void verifyRowExists(Sheet sheet, Map<String, String> rowQuery) {
		verifyRowExists(sheet, rowQuery, true);
	}

	public static void verifyRowExists(Sheet sheet, Map<String, String> rowQuery, boolean expectedValue) {
		verifyRowExists(getFirstRow(sheet), null, rowQuery, expectedValue);
	}

	public static void verifyRowExists(Row fromRow, Map<String, String> rowQuery) {
		verifyRowExists(fromRow, null, rowQuery, true);
	}

	public static void verifyRowExists(Row fromRow, Map<String, String> rowQuery, boolean expectedValue) {
		verifyRowExists(fromRow, null, rowQuery, expectedValue);
	}

	public static void verifyRowExists(Row fromRow, Row toRow, Map<String, String> rowQuery) {
		verifyRowExists(fromRow, toRow, rowQuery, true);
	}

	public static void verifyRowExists(Row fromRow, Row toRow, Map<String, String> rowQuery, boolean expectedValue) {
		if (null == toRow) {
			toRow = getLastRow(fromRow.getSheet());
		}
		String message = String.format("Row with query params \"%1$s\" was%2$s found as expected on sheet \"%3$s\" in range between row %4$s and %5$s.",
				rowQuery.entrySet(), expectedValue ? " not" : "", fromRow.getSheet().getSheetName(), fromRow.getRowNum(), toRow.getRowNum());
		if (expectedValue) {
			CustomAssert.assertTrue(message, rowExists(fromRow, toRow, rowQuery));
		} else {
			CustomAssert.assertFalse(message, rowExists(fromRow, toRow, rowQuery));
		}
	}

	private static InputStream getFileInputStream(File file) {
		InputStream is = null;
		if (!file.exists()) {
			throw new IstfException(String.format("File \"%s\" does not exist.", file.getAbsolutePath()));
		}
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return is;
	}
}
