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
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.iterator.CellIterator;

public class ExcelRow implements Iterable<ExcelCell> {
	protected Row row;
	protected int rowIndex;
	protected ExcelSheet sheet;
	protected Set<CellType<?>> cellTypes;
	private Map<Integer, ExcelCell> cells;

	public ExcelRow(Row row, int rowIndex, ExcelSheet sheet) {
		this(row, rowIndex, sheet, ExcelCell.getBaseTypes());
	}

	public ExcelRow(Row row, int rowIndex, ExcelSheet sheet, Set<CellType<?>> cellTypes) {
		this.row = row;
		this.rowIndex = rowIndex;
		this.sheet = sheet;
		this.cellTypes = new HashSet<>(cellTypes);
	}

	public ExcelSheet getSheet() {
		return sheet;
	}

	@SuppressWarnings("unchecked")
	<R extends ExcelRow> R setSheet(ExcelSheet sheet) {
		this.sheet = sheet;
		return (R) this;
	}

	public int getRowIndex() {
		return this.rowIndex;
	}

	@SuppressWarnings("unchecked")
	<R extends ExcelRow> R setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
		return (R) this;
	}

	public List<? extends ExcelCell> getCells() {
		return new ArrayList<>(getCellsMap().values());
	}

	public List<Integer> getColumnsIndexes() {
		return new ArrayList<>(getCellsMap().keySet());
	}

	public int getFirstColumnIndex() {
		return getColumnsIndexes().get(0);
	}

	public int getLastColumnIndex() {
		List<Integer> cellIndexes = getColumnsIndexes();
		return cellIndexes.get(cellIndexes.size() - 1);
	}

	public int getSize() {
		return getCellsMap().size();
	}

	public boolean isEmpty() {
		if (getPoiRow() == null) {
			return true;
		}
		if (getPoiRow().getLastCellNum() <= 0) {
			return true;
		}
		for (ExcelCell cell : this) {
			if (!cell.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public List<Object> getValues() {
		return getCells().stream().map(ExcelCell::getValue).collect(Collectors.toList());
	}

	public List<String> getStringValues() {
		return getCells().stream().map(ExcelCell::getStringValue).collect(Collectors.toList());
	}

	public Set<CellType<?>> getCellTypes() {
		return new HashSet<>(this.cellTypes);
	}

	@SuppressWarnings("unchecked")
	<R extends ExcelRow> R setCellTypes(Set<CellType<?>> cellTypes) {
		this.cellTypes = new HashSet<>(cellTypes);
		return (R) this;
	}

	public Row getPoiRow() {
		return row;
	}

	@SuppressWarnings("unchecked")
	<R extends ExcelRow> R setPoiRow(Row row) {
		this.row = row;
		return (R) this;
	}

	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<ExcelCell> iterator() {
		return (Iterator<ExcelCell>) new CellIterator(this);
	}

	@SuppressWarnings("unchecked")
	public <C extends ExcelCell> C getCell(int columnIndex) {
		assertThat(hasColumn(columnIndex)).as("There is no cell with %s column number", columnIndex).isTrue();
		return (C) getCellsMap().get(columnIndex);
	}

	public Object getValue(int columnIndex) {
		return getCell(columnIndex).getValue();
	}

	public String getStringValue(int columnIndex) {
		return getCell(columnIndex).getStringValue();
	}

	public Boolean getBoolValue(int columnIndex) {
		return getCell(columnIndex).getBoolValue();
	}

	public Integer getIntValue(int columnIndex) {
		return getCell(columnIndex).getIntValue();
	}

	public LocalDateTime getDateValue(int columnIndex) {
		return getCell(columnIndex).getDateValue();
	}

	public boolean hasColumn(int columnIndex) {
		return getCellsMap().containsKey(columnIndex);
	}

	public boolean hasValue(int columnIndex, Object expectedValue) {
		return Objects.equals(getCell(columnIndex).getValue(), expectedValue);
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

	@SuppressWarnings("unchecked")
	public <R extends ExcelRow> R registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
		getCells().forEach(c -> c.registerCellType(cellTypes));
		return (R) this;
	}

	public void erase() {
		getSheet().eraseRow(this);
		//TODO-dchubkov: return ExcelSheet and extend Table from ExcelSheet?
	}

	public void delete() {
		//TODO-dchubkov: return ExcelSheet and extend Table from ExcelSheet?
		getSheet().deleteRow(this);
	}

	@SuppressWarnings("unchecked")
	public <R extends ExcelRow> R copy(R destinationRow, boolean copyRowIndex) {
		destinationRow.setPoiRow(this.getPoiRow())
				.setCellTypes(this.getCellTypes())
				.setSheet(this.getSheet());

		if (copyRowIndex) {
			destinationRow.setRowIndex(this.getRowIndex());
		}

		for (ExcelCell cell : this) {
			cell.copy(destinationRow.getCell(cell.getColumnIndex()));
		}

		destinationRow.setCellsMap(getCellsMap());
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	protected <R extends ExcelRow, C extends ExcelCell> R setCellsMap(Map<Integer, C> cells) {
		this.cells = new HashMap<>(cells);
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	protected <C extends ExcelCell> Map<Integer, C> getCellsMap() {
		if (this.cells == null) {
			this.cells = new HashMap<>();
			Row poiRow = getPoiRow();
			if (getPoiRow() != null && getPoiRow().getLastCellNum() >= 0) {
				for (int i = 0; i < poiRow.getLastCellNum(); i++) {
					C cell = (C) new ExcelCell(poiRow.getCell(i), this, i + 1);
					this.cells.put(i + 1, cell);
				}
			}
		}
		return new HashMap<>((Map<Integer, C>) this.cells);
	}
}
