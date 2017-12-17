package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;

public class ExcelRow implements Iterable<ExcelCell> {
	protected Row row;
	protected CellType<?>[] cellTypes;
	private Map<Integer, ExcelCell> cells;

	public ExcelRow(Row row) {
		this(row, ExcelCell.getBaseTypes());

	}

	public ExcelRow(Row row, CellType<?>... cellTypes) {
		this.row = row;
		this.cellTypes = cellTypes.clone();
		this.cells = new HashMap<>(row.getLastCellNum() + 1);
		for (Cell cell : row) {
			cells.put(cell.getColumnIndex() + 1, new ExcelCell(cell, cellTypes));
		}
	}

	public int getRowNumber() {
		return row.getRowNum() + 1;
	}

	public List<? extends ExcelCell> getCells() {
		return new ArrayList<>(this.cells.values());
	}

	public List<Integer> getColumnNumbers() {
		return new ArrayList<>(cells.keySet());
	}


	public int getFirstColumnNumber() {
		return getColumnNumbers().get(0);
	}

	public int getLastColumnNumber() {
		List<Integer> cellIndexes = getColumnNumbers();
		return cellIndexes.get(cellIndexes.size() - 1);
	}

	public int getSize() {
		return this.cells.size();
	}

	public List<Object> getValues() {
		return getCells().stream().map(ExcelCell::getValue).collect(Collectors.toList());
	}

	public List<String> getStringValues() {
		return getCells().stream().map(ExcelCell::getStringValue).collect(Collectors.toList());
	}

	CellType<?>[] getCellTypes() {
		return this.cellTypes.clone();
	}

	public Row getPoiRow() {
		return row;
	}

	public ExcelCell getCell(int columnNumber) {
		assertThat(hasColumn(columnNumber)).as("There is no cell with %s column number", columnNumber).isTrue();
		return this.cells.get(columnNumber);
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

	public boolean hasColumn(int columnNumber) {
		return this.cells.containsKey(columnNumber);
	}

	public boolean hasValue(int columnNumber, Object expectedValue) {
		return Objects.equals(getCell(columnNumber).getValue(), expectedValue);
	}

	protected final Map<Integer, ExcelCell> getCellsMap() {
		return new HashMap<>(cells);
	}

	@Override
	@Nonnull
	public Iterator<ExcelCell> iterator() {
		return new CellIterator(this);
	}
}
