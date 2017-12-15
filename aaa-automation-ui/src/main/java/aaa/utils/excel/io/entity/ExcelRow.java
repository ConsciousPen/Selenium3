package aaa.utils.excel.io.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import toolkit.exceptions.IstfException;

public class ExcelRow {
	protected Row row;
	protected Set<CellType<?>> cellTypes;
	private List<ExcelCell> cells;

	public ExcelRow(Row row, Set<CellType<?>> cellTypes) {
		this.row = row;
		this.cellTypes = new HashSet<>(cellTypes);
		this.cells = new ArrayList<>(row.getLastCellNum() + 1);
		for (Cell cell : row) {
			cells.add(new ExcelCell(cell, cellTypes));
		}
	}

	public int getRowNumber() {
		return row.getRowNum() + 1;
	}

	public List<? extends ExcelCell> getCells() {
		return Collections.unmodifiableList(this.cells);
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

	public List<Object> getValues() {
		return getCells().stream().map(ExcelCell::getValue).collect(Collectors.toList());
	}

	public List<String> getStringValues() {
		return getCells().stream().map(ExcelCell::getStringValue).collect(Collectors.toList());
	}

	Set<CellType<?>> getCellTypes() {
		return Collections.unmodifiableSet(cellTypes);
	}

	Row getPoiRow() {
		return row;
	}

	public ExcelCell getCell(int columnNumber) {
		return getCells().stream().filter(c -> c.getColumnNumber() == columnNumber).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no cell with %s column index", columnNumber)));
	}

	public Object getValue(int columnNumber) {
		return getCell(columnNumber).getValue();
	}

	public String getStringValue(int columnNumber) {
		return getCell(columnNumber).getStringValue();
	}

	public boolean getBoolValue(int columnNumber) {
		return getCell(columnNumber).getBoolValue();
	}

	public int getIntValue(int columnNumber) {
		return getCell(columnNumber).getIntValue();
	}

	public LocalDateTime getDateValue(int columnNumber) {
		return getCell(columnNumber).getDateValue();
	}

	public boolean hasCell(int columnNumber) {
		return getCellIndexes().contains(columnNumber);
	}

	public boolean hasValue(int columnNumber, Object expectedValue) {
		return Objects.equals(getCell(columnNumber).getValue(), expectedValue);
	}
}
