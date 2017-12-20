package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.iterator.CellIterator;

public class ExcelRow implements Iterable<ExcelCell> {
	protected Row row;
	protected ExcelSheet sheet;
	protected Set<CellType<?>> cellTypes;
	private Map<Integer, ExcelCell> cells;

	public ExcelRow(Row row, ExcelSheet sheet) {
		this(row, sheet, ExcelCell.getBaseTypes());
	}

	public ExcelRow(Row row, ExcelSheet sheet, Set<CellType<?>> cellTypes) {
		this.row = row;
		this.sheet = sheet;
		this.cellTypes = new HashSet<>(cellTypes);
	}

	public ExcelSheet getSheet() {
		return sheet;
	}

	public int getRowNumber() {
		return row.getRowNum() + 1;
	}

	public List<? extends ExcelCell> getCells() {
		return new ArrayList<>(getCellsMap().values());
	}

	public List<Integer> getColumnNumbers() {
		return new ArrayList<>(getCellsMap().keySet());
	}


	public int getFirstColumnNumber() {
		return getColumnNumbers().get(0);
	}

	public int getLastColumnNumber() {
		List<Integer> cellIndexes = getColumnNumbers();
		return cellIndexes.get(cellIndexes.size() - 1);
	}

	public int getSize() {
		return getCellsMap().size();
	}

	public List<Object> getValues() {
		return getCells().stream().map(ExcelCell::getValue).collect(Collectors.toList());
	}

	public List<String> getStringValues() {
		return getCells().stream().map(ExcelCell::getStringValue).collect(Collectors.toList());
	}

	Set<CellType<?>> getCellTypes() {
		return new HashSet<>(this.cellTypes);
	}

	public Row getPoiRow() {
		return row;
	}

	@SuppressWarnings("unchecked")
	public <C extends ExcelCell> C getCell(int columnNumber) {
		assertThat(hasColumn(columnNumber)).as("There is no cell with %s column number", columnNumber).isTrue();
		return (C) getCellsMap().get(columnNumber);
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
		return getCellsMap().containsKey(columnNumber);
	}

	public boolean hasValue(int columnNumber, Object expectedValue) {
		return Objects.equals(getCell(columnNumber).getValue(), expectedValue);
	}

	@SuppressWarnings("unchecked")
	public <R extends ExcelRow> R save() {
		getSheet().save();
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	public <R extends ExcelRow> R save(File destinationFile) {
		getSheet().save(destinationFile);
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	public <R extends ExcelRow> R close() {
		getSheet().close();
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	public <R extends ExcelRow> R saveAndClose() {
		getSheet().saveAndClose();
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	public <R extends ExcelRow> R saveAndClose(File destinationFile) {
		getSheet().saveAndClose(destinationFile);
		return (R) this;
	}

	@Override
	@Nonnull
	public Iterator<ExcelCell> iterator() {
		return new CellIterator(this);
	}

	@SuppressWarnings("unchecked")
	public <R extends ExcelRow> R registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
		getCells().forEach(c -> c.registerCellType(cellTypes));
		return (R) this;
	}

	private Map<Integer, ExcelCell> getCellsMap() {
		if (this.cells == null) {
			this.cells = new HashMap<>();
			for (Cell cell : getPoiRow()) {
				this.cells.put(cell.getColumnIndex() + 1, new ExcelCell(cell, this));
			}
		}
		return new HashMap<>(this.cells);
	}
}
