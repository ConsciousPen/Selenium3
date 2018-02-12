package aaa.utils.excel.io.entity.area;

import java.util.Set;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;

public abstract class ExcelRow<CELL extends ExcelCell> extends CellsQueue<CELL> {
	private Row row;

	protected ExcelRow(Row row, int rowIndexInArea, int rowIndexOnSheet, Set<Integer> columnsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea) {
		this(row, rowIndexInArea, rowIndexOnSheet, columnsIndexesOnSheet, excelArea, excelArea.getCellTypes());
	}

	protected ExcelRow(Row row, int rowIndexInArea, int rowIndexOnSheet, Set<Integer> columnsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea, Set<CellType<?>> cellTypes) {
		super(rowIndexInArea, rowIndexOnSheet, columnsIndexesOnSheet, excelArea, cellTypes);
		this.row = row;
	}

	public Row getPoiRow() {
		return this.row;
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
		for (CELL cell : this) {
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
				"rowIndex=" + getIndex() +
				", values=" + getValues() +
				'}';
	}

	@Override
	protected Integer getCellIndexOnSheet(Integer cellIndexInQueue) {
		return getCell(cellIndexInQueue).getColumnIndexOnSheet();
	}
}
