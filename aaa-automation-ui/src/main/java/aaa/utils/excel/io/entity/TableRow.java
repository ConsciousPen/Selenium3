package aaa.utils.excel.io.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import com.atlassian.util.concurrent.NotNull;
import aaa.utils.excel.io.celltype.BaseCellType;
import toolkit.exceptions.IstfException;

public class TableRow implements Iterable<ExcelCell<?>> {
	protected List<ExcelCell<?>> cells;
	private ExcelRow excelRow;
	private TableHeader header;
	private int rowNumber;

	public TableRow(ExcelRow excelRow, TableHeader header, int rowNumber) {
		this.excelRow = excelRow;
		this.header = header;
		this.rowNumber = rowNumber;
		this.cells = new ArrayList<>(excelRow.getCells());
		this.cells.removeIf(c -> !header.getColumnIndexes().contains(c.getCellNumber()));
	}

	public TableHeader getHeader() {
		return header;
	}

	public int getRowNumber() {
		return this.rowNumber;
	}

	public Set<BaseCellType<?>> getCellTypes() {
		return getExcelRow().getCellTypes();
	}

	public List<ExcelCell<?>> getCells() {
		return Collections.unmodifiableList(cells);
	}

	public List<Integer> getCellIndexes() {
		return getCells().stream().map(ExcelCell::getCellNumber).sorted().collect(Collectors.toList());
	}

	public int getLastCellNum() {
		List<Integer> cellIndexes = getCellIndexes();
		return cellIndexes.get(cellIndexes.size());
	}

	public int getSize() {
		return cells.size();
	}

	public Map<String, Object> getValues() {
		Map<String, Object> values = new HashMap<>(getSize());
		for (ExcelCell<?> cell : this) {
			String columnName = getHeader().getColumnName(cell.getCellNumber());
			values.put(columnName, cell.getValue());
		}
		return values;
	}

	public Map<String, String> getStringValues() {
		Map<String, String> values = new HashMap<>(getSize());
		for (ExcelCell<?> cell : this) {
			String columnName = getHeader().getColumnName(cell.getCellNumber());
			values.put(columnName, cell.getStringValue());
		}
		return values;
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
	public
	Iterator<ExcelCell<?>> iterator() {
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

	public ExcelCell<?> getCell(String headerColumnName) {
		int cellIndex = getCellIndex(headerColumnName);
		return getCells().stream().filter(c -> c.getCellNumber() == cellIndex).findFirst().get();
	}

	public ExcelCell<?> getCell(int cellIndex) {
		return getCells().stream().filter(c -> c.getCellNumber() == cellIndex).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no cell with %s column index in the table's header", cellIndex)));
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

	public boolean hasValue(String headerColumnName, Object expectedValue) {
		return Objects.equals(getCell(headerColumnName).getValue(), expectedValue);
	}
}
