package aaa.utils.excel.io;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.ExcelCell;
import aaa.utils.excel.io.entity.ExcelRow;
import aaa.utils.excel.io.entity.ExcelTable;
import toolkit.exceptions.IstfException;

public class ExcelReader {
	protected static Logger log = LoggerFactory.getLogger(ExcelReader.class);

	private File file;
	private Workbook workbook;
	private Sheet sheet;
	private CellType<?>[] allowableCellTypes;

	public ExcelReader(File excelFile) {
		this(excelFile, null);
	}

	public ExcelReader(File excelFile, String sheetName) {
		this(excelFile, sheetName, ExcelCell.getBaseTypes());
	}

	public ExcelReader(File file, String sheetName, CellType<?>... allowableCellTypes) {
		this.file = file;
		this.workbook = getWorkbook(file);
		this.sheet = getSheet(workbook, sheetName);
		this.allowableCellTypes = allowableCellTypes.clone();
	}

	public ExcelReader(Sheet sheet) {
		this(sheet, ExcelCell.getBaseTypes());
	}

	public ExcelReader(Sheet sheet, CellType<?>... allowableCellTypes) {
		this.workbook = sheet.getWorkbook();
		this.sheet = sheet;
		this.allowableCellTypes = allowableCellTypes.clone();
	}

	public File getFile() {
		return file;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public List<Sheet> getSheets() {
		return IntStream.rangeClosed(0, getWorkbook().getNumberOfSheets()).mapToObj(sheetNumber -> getWorkbook().getSheetAt(sheetNumber)).collect(Collectors.toList());
	}

	public List<String> getSheetNames() {
		return getSheets().stream().map(Sheet::getSheetName).collect(Collectors.toList());
	}

	public Sheet getCurrentSheet() {
		return sheet;
	}

	public int getLastRowNum() {
		return getCurrentSheet().getLastRowNum() + 1;
	}

	public CellType<?>[] getCellTypes() {
		return this.allowableCellTypes.clone();
	}

	public ExcelRow getFirstRow() {
		return getRow(getCurrentSheet().getFirstRowNum() + 1);
	}

	public ExcelRow getLastRow() {
		return getRow(getCurrentSheet().getLastRowNum() + 1);
	}

	public ExcelReader registerCellType(CellType<?>... cellTypes) {
		allowableCellTypes = ArrayUtils.addAll(allowableCellTypes, cellTypes);
		return this;
	}

	public ExcelReader switchSheet(String sheetName) {
		Sheet sheet = getSheet(getWorkbook(), sheetName);
		assertThat(sheet).as("Can't find sheet with name \"%s\"", sheetName).isNotNull();
		this.sheet = sheet;
		return this;
	}

	public ExcelRow getRow(int rowNumber) {
		return new ExcelRow(getCurrentSheet().getRow(rowNumber - 1), getCellTypes());
	}

	public ExcelCell getCell(int rowNumber, int columnNumber) {
		return getRow(rowNumber).getCell(columnNumber);
	}

	public List<Object> getRowValues(int rowNumber) {
		return getRowValues(rowNumber, 1);
	}

	public List<Object> getRowValues(int rowNumber, int fromColumnNumber) {
		ExcelRow row = getRow(rowNumber);
		return getRowValues(rowNumber, fromColumnNumber, row.getLastColumnNumber());
	}

	/**
	 * Get all non-null Object cell values from provided {@code rowNumber} starting inclusively from {@code fromColumnNumber} and up to inclusively {@code toColumnNumber}
	 *
	 * @param rowNumber row number from which values should be taken, index starts from 1
	 * @param fromColumnNumber the inclusive initial column number on sheet to get values from. Should be positive, index starts from 1
	 * @param toColumnNumber the inclusive last column number on sheet to get values from. Should be greater than {@code fromColumnNumber}
	 *
	 * @return List of non-null String cell values found on provided {@code row} within {@code fromColumnNumber/toColumnNumber} bounds
	 */
	public List<Object> getRowValues(int rowNumber, int fromColumnNumber, int toColumnNumber) {
		assertThat(fromColumnNumber).as("From column number should be greater than 0").isPositive();
		ExcelRow row = getRow(rowNumber);
		return IntStream.rangeClosed(fromColumnNumber, toColumnNumber).filter(row::hasColumn).mapToObj(row::getValue).collect(Collectors.toList());
	}

	public List<String> getRowStringValues(int rowNumber) {
		return getRowStringValues(rowNumber, 1);
	}

	public List<String> getRowStringValues(int rowNumber, int fromColumnNumber) {
		ExcelRow row = getRow(rowNumber);
		return getRowStringValues(rowNumber, fromColumnNumber, row.getLastColumnNumber());
	}

	/**
	 * Get all non-null String cell values from provided {@code rowNumber} starting inclusively from {@code fromColumnNumber} and up to inclusively {@code toColumnNumber}
	 *
	 * @param rowNumber row number from which values should be taken, index starts from 1
	 * @param fromColumnNumber the inclusive initial column number on sheet to get values from. Should be positive, index starts from 1
	 * @param toColumnNumber the inclusive last column number on sheet to get values from. Should be greater than {@code fromColumnNumber}
	 *
	 * @return List of non-null String cell values found on provided {@code row} within {@code fromColumnNumber/toColumnNumber} bounds
	 */
	public List<String> getRowStringValues(int rowNumber, int fromColumnNumber, int toColumnNumber) {
		assertThat(fromColumnNumber).as("From column number should be greater than 0").isPositive();
		ExcelRow row = getRow(rowNumber);
		return IntStream.rangeClosed(fromColumnNumber, toColumnNumber).filter(row::hasColumn).mapToObj(row::getStringValue).collect(Collectors.toList());
	}

	public ExcelRow getRow(String... valuesInCells) {
		return getRow(false, valuesInCells);
	}

	public ExcelRow getRow(boolean isLowest, String... valuesInCells) {
		Set<String> expectedColumnNames = new HashSet<>(Arrays.asList(valuesInCells));
		List<Row> foundRows = new ArrayList<>();
		Map<Integer, Pair<Row, String>> foundRowsWithPartialMatch = new HashMap<>();
		for (Row row : getCurrentSheet()) {
			List<String> rowValues = getRowStringValues(row.getRowNum() + 1);
			Set<String> columnNames = new HashSet<>(expectedColumnNames);
			if (rowValues.containsAll(columnNames)) {
				foundRows.add(row);
			} else if (columnNames.removeAll(rowValues)) {
				foundRowsWithPartialMatch.put(columnNames.size(), Pair.of(row, columnNames.toString()));
			}

			if (!foundRows.isEmpty() && !isLowest) {
				break;
			}
		}

		if (foundRows.isEmpty()) {
			String errorMessage = String.format("Unable to find row with all these values: %1$s on sheet \"%2$s\"", expectedColumnNames, getCurrentSheet().getSheetName());
			if (!foundRowsWithPartialMatch.isEmpty()) {
				int bestMatch = foundRowsWithPartialMatch.keySet().stream().min(Integer::compare).get();
				int rowNumber = foundRowsWithPartialMatch.get(bestMatch).getLeft().getRowNum();
				String missedVales = foundRowsWithPartialMatch.get(bestMatch).getRight();
				errorMessage = String.format("%1$s\nBest match was found in row #%2$s with missed cell values: %3$s", errorMessage, rowNumber, missedVales);
			}
			throw new IstfException(errorMessage);
		}

		ExcelRow row = new ExcelRow(foundRows.get(foundRows.size() - 1), getCellTypes());
		List<String> extraHeaderColumns = new ArrayList<>(row.getStringValues());
		extraHeaderColumns.removeAll(expectedColumnNames);
		if (!extraHeaderColumns.isEmpty()) {
			log.warn("Found row contains extra cell values: {}", extraHeaderColumns);
		}
		return row;
	}

	/**
	 * Only columns with unique names from array will be searched. Returns <b>last</b> found ExcelTable
	 */
	public ExcelTable getTable(String... headerColumnNames) {
		return getTable(false, headerColumnNames);
	}

	public ExcelTable getTable(boolean isLowest, String... headerColumnNames) {
		Row headerRow = getRow(isLowest, headerColumnNames).getPoiRow();
		return new ExcelTable(headerRow, getCellTypes());
	}

	/**
	 *  Get ExcelTable object on current sheet with all non-null header column names found in {@code headerRowNumber} row number.
	 *
	 * @param headerRowNumber Table's header row number on {@link #getCurrentSheet()} sheet. Index starts from 1
	 * @return ExcelTable object representation of found excel table
	 */
	public ExcelTable getTable(int headerRowNumber) {
		assertThat(headerRowNumber).as("Header row number should be greater than 0").isPositive();
		return new ExcelTable(getRow(headerRowNumber).getPoiRow(), getCellTypes());
	}

	public void write() {
		File writeFile = new File("D:\\AAA\\Mocks & Stubs\\PolicyPreferenceApiMockData2.xls");
		try (FileOutputStream outputStream = new FileOutputStream(writeFile);
				Workbook workbook = getWorkbook()) {
			workbook.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Workbook getWorkbook(File file) {
		assertThat(file).as("File \"%s\" does not exist", file.getAbsolutePath()).exists();
		/*try (InputStream targetStream = new FileInputStream(file);
				Workbook wb = WorkbookFactory.create(targetStream)) {
			return wb;
		} catch (IOException | InvalidFormatException e) {
			throw new IstfException("Unable to get workbook from file: " + file.getAbsolutePath(), e);
		}*/
		Workbook wb = null;
		try (InputStream targetStream = new FileInputStream(file)) {
			try {
				//TODO-dchubkov: replace with try-with-resources construction
				wb = WorkbookFactory.create(targetStream);
			} catch (IOException | InvalidFormatException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return wb;
	}

	private Sheet getSheet(Workbook wb, String sheetName) {
		return sheetName == null ? wb.getSheetAt(0) : wb.getSheet(sheetName);
	}
}
