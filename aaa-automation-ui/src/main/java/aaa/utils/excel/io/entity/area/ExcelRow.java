package aaa.utils.excel.io.entity.area;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import toolkit.exceptions.IstfException;

public abstract class ExcelRow<CELL extends ExcelCell> extends CellsQueue<CELL> {
	private final Row row;

	protected ExcelRow(Row row, int rowIndexInArea, int rowIndexOnSheet, List<Integer> columnsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea) {
		this(row, rowIndexInArea, rowIndexOnSheet, columnsIndexesOnSheet, excelArea, excelArea.getCellTypes());
	}

	protected ExcelRow(Row row, int rowIndexInArea, int rowIndexOnSheet, List<Integer> columnsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea, List<CellType<?>> cellTypes) {
		super(rowIndexInArea, rowIndexOnSheet, columnsIndexesOnSheet, excelArea, cellTypes);
		this.row = row;
	}

	public Row getPoiRow() {
		return this.row;
	}

	@Override
	public List<Integer> getCellsIndexes() {
		return getCells().stream().map(ExcelCell::getColumnIndex).collect(Collectors.toList());
	}

	@Override
	public List<CELL> getCellsByIndexes(List<Integer> columnsIndexesInRow) {
		return getCells().stream().filter(c -> columnsIndexesInRow.contains(c.getColumnIndex())).collect(Collectors.toList());
	}

	@Override
	public boolean hasCell(int columnIndexInRow) {
		//return getCells().stream().anyMatch(c -> c.getColumnIndex() == columnIndexInRow);
		for (CELL cell : getCells()) {
			if (cell.getColumnIndex() == columnIndexInRow) {
				return true;
			}
		}
		return false;
	}

	@Override
	public CELL getCell(int columnIndexInRow) {
		//assertThat(hasCell(cellIndexInQueue)).as("There is no cell with %1$s index in %2$s", cellIndexInQueue, this).isTrue();
		/*return getCells().stream().filter(c -> c.getColumnIndex() == columnIndexInRow).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no cell with %1$s index in %2$s", columnIndexInRow, this)));*/
		for (CELL cell : getCells()) {
			if (cell.getColumnIndex() == columnIndexInRow) {
				return cell;
			}
		}
		throw new IstfException(String.format("There is no cell with %1$s index in %2$s", columnIndexInRow, this));
	}

	@Override
	public boolean isEmpty() {
		return getPoiRow() == null || getPoiRow().getLastCellNum() <= 0 || super.isEmpty();
	}

	@Override
	public ExcelArea<CELL, ?, ?> exclude() {
		return getArea().excludeRows(getIndex());
	}

	@Override
	public ExcelRow<CELL> copy(int destinationRowIndex) {
		for (CELL cell : getCells()) {
			cell.copy(destinationRowIndex, cell.getColumnIndex());
		}
		return this;
	}

	@Override
	public ExcelArea<CELL, ?, ?> delete() {
		return getArea().deleteRows(getIndex());
	}

	@Override
	public String toString() {
		return "ExcelRow{" +
				"sheetName=" + getSheetName() +
				", rowIndex=" + getIndex() +
				", columnsNumber=" + getCellsNumber() +
				", cellTypes=" + getCellTypes() +
				", values=" + getStringValues() +
				'}';
	}

	@Override
	protected Integer getCellIndexOnSheet(Integer columnIndexInRow) {
		return getCell(columnIndexInRow).getColumnIndexOnSheet();
	}

	@Override
	protected void removeCellsIndexes(Integer... columnIndexesInRow) {
		super.removeCellsIndexes(columnIndexesInRow);
		this.cells.removeIf(c -> Arrays.asList(columnIndexesInRow).contains(c.getColumnIndex()));
	}
}
