package aaa.utils.excel.io.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.atlassian.util.concurrent.NotNull;
import aaa.utils.excel.io.celltype.CellType;
import toolkit.exceptions.IstfException;

public class TableRow implements Iterable<ExcelCell> {
	private ExcelRow excelRow;
	protected List<ExcelCell> cells;
	private TableHeader header;
	private int rowNumber;

	public TableRow(ExcelRow excelRow, TableHeader header, int rowNumber) {
		this.excelRow = excelRow;
		this.header = header;
		this.rowNumber = rowNumber;
		this.cells = new ArrayList<>(excelRow.getCells());
		this.cells.removeIf(c -> !header.getColumnIndexes().contains(c.getColumnNumber()));
	}

	public TableHeader getHeader() {
		return header;
	}

	public int getRowNumber() {
		return this.rowNumber;
	}

	public List<ExcelCell> getCells() {
		return Collections.unmodifiableList(cells);
	}

	public List<Integer> getCellIndexes() {
		return getCells().stream().map(ExcelCell::getColumnNumber).sorted().collect(Collectors.toList());
	}

	public int getLastCellNum() {
		List<Integer> cellIndexes = getCellIndexes();
		return cellIndexes.get(cellIndexes.size() - 1);
	}

	public int getSize() {
		return getCells().size();
	}

	public Map<String, Object> getValues() {
		Map<String, Object> values = new HashMap<>(getSize());
		for (ExcelCell cell : this) {
			String columnName = getHeader().getColumnName(cell.getColumnNumber());
			values.put(columnName, cell.getValue());
		}
		return values;
	}

	public Map<String, String> getStringValues() {
		Map<String, String> values = new HashMap<>(getSize());
		for (ExcelCell cell : this) {
			String columnName = getHeader().getColumnName(cell.getColumnNumber());
			values.put(columnName, cell.getStringValue());
		}
		return values;
	}

	Set<CellType<?>> getCellTypes() {
		return getExcelRow().getCellTypes();
	}

	ExcelRow getExcelRow() {
		return excelRow;
	}

	@Override
	public String toString() {
		return "TableRow{" +
				"rowNumber=" + rowNumber +
				", values=" + getValues().entrySet() +
				'}';
	}

	@NotNull
	@Override
	public Iterator<ExcelCell> iterator() {
		return new CellIterator(getCellIndexes(), getExcelRow());
	}

	public boolean hasColumnName(String headerColumnName) {
		return getHeader().getColumnNames().contains(headerColumnName);
	}

	public boolean hasCellIndex(int columnIndex) {
		return getHeader().getColumnIndexes().contains(columnIndex);
	}

	public Integer getCellIndex(String headerColumnName) {
		return getHeader().getColumnIndexes().stream().filter(i -> getHeader().getColumnName(i).equals(headerColumnName)).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no cell with \"%s\" column name in the table's header", headerColumnName)));
	}

	public ExcelCell getCell(String headerColumnName) {
		int cellIndex = getCellIndex(headerColumnName);
		return getCells().stream().filter(c -> c.getColumnNumber() == cellIndex).findFirst().get();
	}

	public ExcelCell getCell(int cellIndex) {
		return getCells().stream().filter(c -> c.getColumnNumber() == cellIndex).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no cell with %s column index in the table's header", cellIndex)));
	}

	public Object getValue(String headerColumnName) {
		return getCell(headerColumnName).getValue();
	}

	public String getStringValue(String headerColumnName) {
		return getCell(headerColumnName).getStringValue();
	}

	public boolean getBoolValue(String headerColumnName) {
		return getCell(headerColumnName).getBoolValue();
	}

	public int getIntValue(String headerColumnName) {
		return getCell(headerColumnName).getIntValue();
	}

	public LocalDateTime getDateValue(String headerColumnName) {
		return getCell(headerColumnName).getDateValue();
	}

	public boolean hasCell(int columnNumber) {
		return getCellIndexes().contains(columnNumber);
	}

	public boolean hasCell(String headerColumnName) {
		int columnNumber = getHeader().getColumnIndex(headerColumnName);
		return hasCell(columnNumber);
	}

	public boolean hasValue(String headerColumnName, Object expectedValue) {
		return getCell(headerColumnName).hasValue(expectedValue);
	}
}
