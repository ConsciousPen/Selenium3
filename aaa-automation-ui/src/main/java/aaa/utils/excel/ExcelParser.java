package aaa.utils.excel;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.ExcelUtils;
import toolkit.exceptions.IstfException;

public class ExcelParser {
	protected static Logger log = LoggerFactory.getLogger(ExcelParser.class);

	private Workbook workbook;
	private Sheet sheet;

	public ExcelParser(File excelFile) {
		this.workbook = ExcelUtils.getWorkbook(excelFile.getAbsolutePath());
		this.sheet = ExcelUtils.getSheet(this.workbook);
	}

	public ExcelParser(File excelFile, String sheetName) {
		this.workbook = ExcelUtils.getWorkbook(excelFile.getAbsolutePath());
		this.sheet = ExcelUtils.getSheet(this.workbook, sheetName);
	}

	public ExcelParser(File excelFile, SearchPattern sheetNamePattern) {
		this.workbook = ExcelUtils.getWorkbook(excelFile.getAbsolutePath());
		this.sheet = getSheet(sheetNamePattern);
	}

	public ExcelParser(Sheet sheet) {
		this.workbook = sheet.getWorkbook();
		this.sheet = sheet;
	}

	public List<Sheet> getSheets() {
		return IntStream.range(0, getWorkbook().getNumberOfSheets()).mapToObj(sheetNumber -> getWorkbook().getSheetAt(sheetNumber)).collect(Collectors.toList());
	}

	public List<String> getSheetNames() {
		return getSheets().stream().map(Sheet::getSheetName).collect(Collectors.toList());
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public int getLastRowNum() {
		return getSheet().getLastRowNum();
	}

	private static String getLocation(Cell cell) {
		assertThat(cell).as("Cell should not be null").isNotEqualTo(null);
		return String.format("sheet name: \"%1$s\", row number: %2$s, column numver: %3$s", cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex());
	}

	public final Sheet getSheet(SearchPattern sheetNamePattern) {
		for (Sheet sheet : getSheets()) {
			if (sheetNamePattern.matches(sheet.getSheetName())) {
				return sheet;
			}
		}
		throw new IstfException(String.format("There is no sheet in list %1$s with name which mathches pattern: %2$s", getSheetNames(), sheetNamePattern));
	}

	public ExcelParser switchSheet(String sheetName) {
		Sheet sheet = ExcelUtils.getSheet(this.workbook, sheetName);
		assertThat(sheet).as("Can't find sheet with name \"%s\"", sheetName).isNotEqualTo(null);
		this.sheet = sheet;
		return this;
	}

	public List<String> getRowValues(Row row) {
		return getRowValues(row, 0);
	}

	public List<String> getRowValues(Row row, int fromColumnNumber) {
		assertThat(row).as("Row should not be null").isNotEqualTo(null);
		return getRowValues(row, fromColumnNumber, row.getLastCellNum());
	}

	public List<String> getRowValues(Row row, int fromColumnNumber, int toColumnNumber) {
		int size = toColumnNumber - fromColumnNumber + 1;
		List<String> rowValues = new ArrayList<>(size);
		for (int cNumber = fromColumnNumber; cNumber < fromColumnNumber + size; cNumber++) {
			String value = getValue(row, cNumber);
			if (!value.isEmpty()) {
				rowValues.add(value);
			}
		}
		return rowValues;
	}

	public boolean getBoolValue(Row row, int columnNumber) {
		assertThat(row).as("Row should not be null").isNotEqualTo(null);
		return getBoolValue(row.getCell(columnNumber));
	}

	public boolean getBoolValue(Cell cell) {
		if (cell.getCellType() != Cell.CELL_TYPE_BOOLEAN) { // if boolean value stored as text
			return Boolean.valueOf(getValue(cell));
		}
		return cell.getBooleanCellValue();
	}

	public int getIntValue(Row row, int columnNumber) {
		assertThat(row).as("Row should not be null").isNotEqualTo(null);
		return getIntValue(row.getCell(columnNumber));
	}

	public int getIntValue(Cell cell) {
		if (cell.getCellType() != Cell.CELL_TYPE_NUMERIC) { // if number stored as text
			return Integer.valueOf(getValue(cell));
		}
		return (int) cell.getNumericCellValue();
	}

	public LocalDateTime getDateValue(Row row, int columnNumber) {
		assertThat(row).as("Row should not be null").isNotEqualTo(null);
		return getDateValue(row.getCell(columnNumber));
	}

	public LocalDateTime getDateValue(Cell cell) {
		//TODO-dchubkov: get date if it's stored as text
		return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public String getValue(Row row, int columnNumber) {
		assertThat(row).as("Row should not be null").isNotEqualTo(null);
		return getValue(row.getCell(columnNumber));
	}

	public String getValue(Cell cell) {
		String value = "";
		if (cell == null) {
			return value;
		}

		try {
			if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
				FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
				cell = evaluator.evaluateInCell(cell);
				value = ExcelUtils.getCellValue(cell);
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)) {
				value = new DataFormatter().formatCellValue(cell);
			} else {
				value = ExcelUtils.getCellValue(cell);
			}
		} catch (IllegalStateException e) {
			throw new IstfException("Unable to get string value from cell located in " + getLocation(cell), e);
		}

		return value;
	}

	public Row getHeaderRow(Set<String> headerColumnNames) {
		return getHeaderRow(headerColumnNames, true);
	}

	public Row getHeaderRow(Set<String> headerColumnNames, boolean isLowest) {
		List<Row> foundRows = new ArrayList<>();
		Map<Integer, Pair<Row, String>> foundRowsWithPartialMatch = new HashMap<>();
		for (Row row : this.sheet) {
			List<String> rowValues = getRowValues(row);
			Set<String> columnNames = new HashSet<>(headerColumnNames);
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
			String errorMessage = String.format("Unable to find header row with all these column names: %1$s on sheet \"%2$s\"", headerColumnNames, getSheet().getSheetName());
			if (!foundRowsWithPartialMatch.isEmpty()) {
				int bestMatch = foundRowsWithPartialMatch.keySet().stream().min(Integer::compare).get();
				int rowNumber = foundRowsWithPartialMatch.get(bestMatch).getLeft().getRowNum();
				String missedVales = foundRowsWithPartialMatch.get(bestMatch).getRight();
				errorMessage = String.format("%1$s\nBest match was found in row #%2$s with missed column names: %3$s", errorMessage, rowNumber, missedVales);
			}
			throw new IstfException(errorMessage);
		}

		Row headerRow = foundRows.get(foundRows.size() - 1);
		List<String> extraHeaderColumns = new ArrayList<>(getRowValues(headerRow));
		extraHeaderColumns.removeAll(headerColumnNames);
		if (!extraHeaderColumns.isEmpty()) {
			log.warn("Found header row contains extra column names: {}", extraHeaderColumns);
		}
		return headerRow;
	}

	public ExcelTable getTable(Set<String> headerColumnNames) {
		return getTable(headerColumnNames, true);
	}

	public ExcelTable getTable(Set<String> headerColumnNames, boolean isLowest) {
		TableHeader header = new TableHeader(getHeaderRow(headerColumnNames, isLowest));
		return new ExcelTable(header);
	}
}
