package aaa.utils.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.exigen.ipb.etcsa.utils.ExcelUtils;
import toolkit.exceptions.IstfException;

public class ExcelParser {
	private Workbook workbook;
	private Sheet sheet;

	public ExcelParser(File excelFile, String sheetName) {
		this.workbook = ExcelUtils.getWorkbook(excelFile.getAbsolutePath());
		this.sheet = ExcelUtils.getSheet(this.workbook, sheetName);
	}

	public ExcelParser(Sheet sheet) {
		this.workbook = sheet.getWorkbook();
		this.sheet = sheet;
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
		if (cell == null) {
			return "cell is null";
		}
		return String.format("sheet name: \"%1$s\", row number: %2$s, column numver: %3$s", cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex());
	}

	public List<String> getRowValues(Row row) {
		return getRowValues(row, 0);
	}

	public List<String> getRowValues(Row row, int fromColumnNumber) {
		if (null == row) {
			throw new IstfException("Impossible to get values from null row");
		}
		return getRowValues(row, fromColumnNumber, row.getLastCellNum());
	}

	public List<String> getRowValues(Row row, int fromColumnNumber, int toColumnNumber) {
		int size = toColumnNumber - fromColumnNumber + 1;
		List<String> rowValues = new ArrayList<>(size);
		for (int cNumber = fromColumnNumber; cNumber < fromColumnNumber + size; cNumber++) {
			rowValues.add(getValue(row, cNumber));
		}
		return rowValues;
	}

	public String getValue(Row row, int columnNumber) {
		if (null == row) {
			throw new IstfException("Impossible to get value from null row");
		}
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

		for (Row row : this.sheet) {
			if (getRowValues(row).containsAll(headerColumnNames)) {
				foundRows.add(row);
			}
			if (!foundRows.isEmpty() && !isLowest) {
				break;
			}
		}

		if (foundRows.isEmpty()) {
			throw new IstfException("Unable to find header row with column values " + headerColumnNames);
		}

		return foundRows.get(foundRows.size() - 1);
	}

	public ExcelTable getTable(Set<String> headerColumnNames) {
		return getTable(headerColumnNames, true);
	}

	public ExcelTable getTable(Set<String> headerColumnNames, boolean isLowest) {
		TableHeader header = new TableHeader(getHeaderRow(headerColumnNames, isLowest));
		return new ExcelTable(header);
	}
}
